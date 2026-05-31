import { CONFIG, showAlert } from './config.js';

const modal = document.getElementById('modalVantagem');
const modalExcluir = document.getElementById('modalExcluirVantagem');
const btnNova = document.getElementById('btnNovaVantagem');
const btnFechar = document.getElementById('btnFecharModalVantagem');
const form = document.getElementById('formVantagem');
const container = document.getElementById('listaVantagens');

const empresaId = localStorage.getItem('usuarioId');
let vantagemIdParaExcluir = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!empresaId) {
        window.location.href = 'login.html';
        return;
    }
    carregarVantagens();
});

// Busca todas as vantagens da empresa
async function carregarVantagens() {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/vantagens/empresa/${empresaId}`);
        if (response.ok) {
            const vantagens = await response.json();
            renderizarVantagens(vantagens);
        }
    } catch (error) {
        console.error('Erro ao buscar vantagens:', error);
        showAlert('Erro ao carregar o catálogo de vantagens.', 'error');
    }
}

function renderizarVantagens(vantagens) {
    container.innerHTML = '';
    if (vantagens.length === 0) {
        container.innerHTML = `<p class="text-center-muted col-span-full">Você ainda não cadastrou nenhuma vantagem.</p>`;
        return;
    }

    vantagens.forEach(v => {
        const fotoHtml = v.urlFoto 
            ? `<img src="${v.urlFoto}" class="vantagem-img">` 
            : `<span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.5;">inventory_2</span>`;

        container.innerHTML += `
            <div class="glass vantagem-card">
                <div>
                    <div class="vantagem-img-container">
                        ${fotoHtml}
                    </div>
                    <h5 class="vantagem-title">${v.nome}</h5>
                    <p class="vantagem-desc">${v.descricao}</p>
                    <p class="vantagem-custo">
                        <img src="../assets/Lumen.png" alt="Lúmens" style="width: 40px; height: 28px; margin-right: 8px;"> 
                        ${v.custo} Lúmens
                    </p>
                </div>
                <div class="vantagem-actions">
                    <button class="btn-edit-text btn-action" onclick="editarVantagem(${v.id}, '${v.nome}', '${v.descricao}', ${v.custo}, '${v.urlFoto || ''}')">Editar</button>
                    <button class="btn-edit-text btn-action btn-danger" onclick="excluirVantagem(${v.id})">Excluir</button>
                </div>
            </div>
        `;
    });
}

// Controle do Modal
btnNova.addEventListener('click', () => {
    document.getElementById('modalVantagemTitulo').textContent = "Adicionar Vantagem";
    form.reset();
    document.getElementById('vantagemId').value = '';
    modal.classList.add('active');
});

btnFechar.addEventListener('click', () => modal.classList.remove('active'));

window.editarVantagem = function(id, nome, desc, custo, urlFoto) {
    document.getElementById('modalVantagemTitulo').textContent = "Editar Vantagem";
    document.getElementById('vantagemId').value = id;
    document.getElementById('vantagemNome').value = nome;
    document.getElementById('vantagemDesc').value = desc;
    document.getElementById('vantagemCusto').value = custo;
    
    // Preenche a URL e limpa o input de arquivo antigo
    document.getElementById('vantagemFotoUrl').value = urlFoto || '';
    document.getElementById('vantagemFotoFile').value = ''; 
    
    modal.classList.add('active');
};

// Salvar (Criar ou Editar)
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('vantagemId').value;
    
    const fileInput = document.getElementById('vantagemFotoFile');
    let urlFotoFinal = document.getElementById('vantagemFotoUrl').value;

    // 1. Se o usuário anexou um arquivo, faremos o upload para o Supabase primeiro!
    if (fileInput && fileInput.files.length > 0) {
        const file = fileInput.files[0];
        const fileExt = file.name.split('.').pop();
        const fileName = `vantagem-${empresaId}-${Date.now()}.${fileExt}`;

        try {
            // ATENÇÃO: Crie um bucket público chamado 'vantagens' no Supabase!
            const uploadResponse = await fetch(`${CONFIG.SUPABASE_URL}/storage/v1/object/vantagens/${fileName}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${CONFIG.SUPABASE_KEY}`,
                    'apikey': CONFIG.SUPABASE_KEY
                },
                body: file
            });

            if (uploadResponse.ok) {
                // Sobrescreve a URL final com o link da imagem salva no Supabase
                urlFotoFinal = `${CONFIG.SUPABASE_URL}/storage/v1/object/public/vantagens/${fileName}`;
            } else {
                const errorData = await uploadResponse.json();
                showAlert("Erro no Supabase: " + errorData.message, 'error');
                return;
            }
        } catch (error) {
            console.error("Erro de Rede ao subir imagem:", error);
            showAlert("Falha ao subir a imagem para a nuvem.", 'error');
            return;
        }
    }

    // 2. Monta o payload final (já com a URL da foto resolvida: seja do Supabase ou digitada)
    const payload = {
        nome: document.getElementById('vantagemNome').value,
        descricao: document.getElementById('vantagemDesc').value,
        custo: parseInt(document.getElementById('vantagemCusto').value),
        urlFoto: urlFotoFinal,
        empresaId: parseInt(empresaId)
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${CONFIG.API_URL}/api/vantagens/${id}` : `${CONFIG.API_URL}/api/vantagens`;

    // 3. Salva no nosso Banco de Dados Java
    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            showAlert(`Vantagem ${id ? 'atualizada' : 'cadastrada'} com sucesso!`, 'success');
            modal.classList.remove('active');
            carregarVantagens(); // Recarrega o catálogo na hora
        } else {
            showAlert('Erro ao salvar a vantagem.', 'error');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        showAlert('Falha na comunicação com o servidor.', 'error');
    }
});

window.excluirVantagem = function(id) {
    vantagemIdParaExcluir = id;
    modalExcluir.classList.add('active');
};

document.getElementById('btnConfirmarExclusao').addEventListener('click', async () => {
    if (!vantagemIdParaExcluir) return;

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/vantagens/${vantagemIdParaExcluir}`, { method: 'DELETE' });
        if (response.ok) {
            showAlert('Vantagem removida.', 'success');
            modalExcluir.classList.remove('active');
            carregarVantagens();
        } else {
            showAlert('Erro ao remover vantagem.', 'error');
        }
    } catch (error) {
        showAlert('Falha na comunicação com o servidor.', 'error');
    }
});