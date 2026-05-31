import { CONFIG, showAlert } from './config.js';

const container = document.getElementById('listaInventario');
const modalCupom = document.getElementById('modalCupom');
const alunoId = localStorage.getItem('usuarioId');

document.addEventListener('DOMContentLoaded', async () => {
    if (!alunoId) {
        window.location.href = 'login.html';
        return;
    }
    await carregarInventario();
});

async function carregarInventario() {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/inventarios/aluno/${alunoId}`);
        if (response.ok) {
            const inventarios = await response.json();
            renderizarInventario(inventarios);
        } else {
            container.innerHTML = `<p class="text-center-muted col-span-full empty-state-padding">Não foi possível carregar seu inventário no momento.</p>`;
        }
    } catch (error) {
        console.error('Erro ao buscar inventário:', error);
        container.innerHTML = `<p class="text-center-muted col-span-full empty-state-padding">Falha na conexão com o servidor.</p>`;
    }
}

function renderizarInventario(lista) {
    container.innerHTML = '';
    
    if (lista.length === 0) {
        container.innerHTML = `<p class="text-center-muted col-span-full empty-state-padding">Você ainda não resgatou nenhuma vantagem. Visite a Loja para gastar seus Lúmens!</p>`;
        return;
    }

    // Inverte a lista para mostrar os cupons mais recentes primeiro
    lista.reverse().forEach(inv => {
        const v = inv.vantagem;
        if (!v) return;

        const fotoHtml = v.urlFoto 
            ? `<img src="${v.urlFoto}" class="vantagem-img">` 
            : `<span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.5;">inventory_2</span>`;

        // Controle visual de status do cupom
        const isAtivo = (inv.status === 'DISPONIVEL' || inv.status === 'Disponivel');
        const statusBadge = isAtivo 
            ? `<span style="font-size: 0.65rem; background: rgba(76, 175, 80, 0.15); border: 1px solid #4caf50; padding: 3px 8px; border-radius: 10px; color: #4caf50; font-weight: bold; letter-spacing: 1px;">DISPONÍVEL</span>`
            : `<span style="font-size: 0.65rem; background: rgba(255, 255, 255, 0.1); border: 1px solid rgba(255, 255, 255, 0.2); padding: 3px 8px; border-radius: 10px; color: var(--text-secondary); letter-spacing: 1px;">UTILIZADO</span>`;

        // Se já foi utilizado, deixamos o card mais transparente
        const opacidadeCard = isAtivo ? '1' : '0.5';

        container.innerHTML += `
            <div class="glass vantagem-card" style="opacity: ${opacidadeCard}; transition: opacity 0.3s;">
                <div>
                    <div class="vantagem-img-container">
                        ${fotoHtml}
                    </div>
                    <div class="header-flex" style="align-items: flex-start !important; margin-bottom: 5px;">
                        <h5 class="vantagem-title">${v.nome}</h5>
                        ${statusBadge}
                    </div>
                    <p class="vantagem-desc loja-desc-margin">${v.descricao}</p>
                </div>
                <div class="vantagem-actions actions-center">
                    <button class="btn btn-edit-text btn-full-width" style="border-color: var(--lumen-gold); color: var(--lumen-gold);" onclick="abrirModalCupom('${v.nome}', '${inv.codigoCupom}')">
                        <span class="material-symbols-outlined">local_activity</span>
                        Ver Cupom
                    </button>
                </div>
            </div>
        `;
    });
}

// Abre o cupom para o aluno apresentar na loja
window.abrirModalCupom = function(nome, codigo) {
    document.getElementById('cupomTitulo').textContent = nome;
    document.getElementById('codigoCupomExibicao').textContent = codigo;
    
    // Gera o QR Code
    const container = document.getElementById("qrcodeContainer");
    container.innerHTML = ""; // Limpa anterior
    new QRCode(container, {
        text: codigo,
        width: 150,
        height: 150
    });
    
    modalCupom.classList.add('active');
};