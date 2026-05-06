import { CONFIG } from './config.js';

const alunoId = localStorage.getItem('usuarioId');
let dadosAtuaisAluno = {}; // Armazena dados para evitar NULL no banco[cite: 57]

document.addEventListener('DOMContentLoaded', async () => {
    if (!alunoId) { window.location.href = 'login.html'; return; }
    await carregarDadosAluno(alunoId);
    configurarEventos();
});

async function carregarDadosAluno(id) {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/alunos/${id}`);
        if (response.ok) {
            dadosAtuaisAluno = await response.json();
            const aluno = dadosAtuaisAluno;
            
            // Atualiza Sidebar[cite: 57]
            document.querySelector('.user-name').textContent = aluno.nome;
            document.querySelector('.level-badge').textContent = `Nível ${aluno.nivel || 1}`;
            document.querySelector('.user-rank').textContent = "Explorador de Lúmens";
            
            if (aluno.urlFotoPerfil) {
                document.querySelector('.perfil-avatar').src = aluno.urlFotoPerfil;
            }
            
            const lumenCount = document.querySelector('.lumen-count');
            if (lumenCount) {
                const saldo = aluno.carteira ? aluno.carteira.saldoAtual : 0;
                lumenCount.textContent = saldo.toLocaleString('pt-BR');
            }
            
            // Dados Acadêmicos[cite: 57]
            const dataValues = document.querySelectorAll('.data-value');
            dataValues[0].textContent = aluno.curso || "Não informado";
            dataValues[1].textContent = aluno.instituicao?.nome || "Não informado";

            // Lógica de Endereço[cite: 57]
            const areaEndereco = document.getElementById('completar-perfil-area');
            if (aluno.rua && aluno.numero) {
                // Caso JÁ TENHA endereço: 
                // 1. Mostra os dados formatados
                // 2. Muda o botão para o estilo 'btn-edit-text' (amarelo com borda)
                areaEndereco.innerHTML = `
                    <div style="text-align: left; width: 100%;">
                        <p class="data-value">${aluno.rua}, ${aluno.numero} ${aluno.complemento || ''}</p>
                        <p class="data-label">${aluno.bairro} - ${aluno.cidade}</p>
                    </div>
                    <div class="card-footer" style="margin-top: 1.5rem;">
                        <button class="btn-edit-text" onclick="abrirModalEndereco()">Editar Endereço</button>
                    </div>
                `;
                areaEndereco.classList.remove('centered');
            } else {
                // Caso NÃO TENHA endereço:
                // Mantém o estado original: texto de aviso e botão 'btn-confirmar' (verde preenchido)[cite: 57, 59]
                areaEndereco.innerHTML = `
                    <p class="status-text">Você ainda não cadastrou seu endereço para resgate de prêmios.</p>
                    <button class="btn btn-confirmar btn-small" onclick="abrirModalEndereco()">Completar Perfil</button>
                `;
                areaEndereco.classList.add('centered');
            }

            // Preenche campos do modal para edição
            document.getElementById('editCurso').value = aluno.curso || "";
            document.getElementById('editRua').value = aluno.rua || "";
            document.getElementById('editNumero').value = aluno.numero || "";
            document.getElementById('editBairro').value = aluno.bairro || "";
            document.getElementById('editCidade').value = aluno.cidade || "";
            document.getElementById('editComplemento').value = aluno.complemento || "";
        }
    } catch (e) { console.error("Erro ao carregar perfil:", e); }
}

window.abrirModalEndereco = () => document.getElementById('modalEndereco').classList.add('active');
window.fecharModal = (id) => document.getElementById(id).classList.remove('active');

function configurarEventos() {
    // Abrir modal de edição acadêmica
    document.querySelector('.btn-edit-text').addEventListener('click', () => {
        document.getElementById('modalEditar').classList.add('active');
    });

    // Submit Acadêmico (Curso e Foto) - Baseado na lógica da Empresa
    document.getElementById('formEditarPerfil').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fileInput = document.getElementById('editFotoFile');
        let urlFotoFinal = dadosAtuaisAluno.urlFotoPerfil;

        // Lógica de Upload Supabase
        if (fileInput.files.length > 0) {
            const file = fileInput.files[0];
            const fileExt = file.name.split('.').pop();
            const fileName = `aluno-${alunoId}-${Date.now()}.${fileExt}`;

            try {
                const uploadResponse = await fetch(`${CONFIG.SUPABASE_URL}/storage/v1/object/foto_de_perfil/${fileName}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${CONFIG.SUPABASE_KEY}`,
                        'apikey': CONFIG.SUPABASE_KEY
                    },
                    body: file
                });

                if (uploadResponse.ok) {
                    urlFotoFinal = `${CONFIG.SUPABASE_URL}/storage/v1/object/public/foto_de_perfil/${fileName}`;
                    console.log("Upload OK:", urlFotoFinal);
                } else {
                    const errorData = await uploadResponse.json();
                    alert("Erro no servidor de imagens: " + errorData.message);
                    return;
                }
            } catch (error) {
                console.error("Erro de Rede:", error);
                alert("Falha de conexão com o servidor de imagens.");
                return;
            }
        }

        // Payload completo para evitar erros de integridade no banco
        const payload = {
            ...dadosAtuaisAluno,
            curso: document.getElementById('editCurso').value,
            urlFotoPerfil: urlFotoFinal,
            instituicaoId: dadosAtuaisAluno.instituicao?.id
        };
        
        await enviarPut(payload, 'modalEditar');
    });

    // Submit Endereço[cite: 57]
    document.getElementById('formEndereco').addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            ...dadosAtuaisAluno,
            rua: document.getElementById('editRua').value,
            numero: parseInt(document.getElementById('editNumero').value),
            bairro: document.getElementById('editBairro').value,
            cidade: document.getElementById('editCidade').value,
            complemento: document.getElementById('editComplemento').value,
            instituicaoId: dadosAtuaisAluno.instituicao?.id
        };
        await enviarPut(payload, 'modalEndereco');
    });

    // Exclusão de conta
    document.getElementById('btnExcluirConta').addEventListener('click', async () => {
        if(confirm("Deseja mesmo excluir sua conta permanentemente?")) {
            try {
                const res = await fetch(`${CONFIG.API_URL}/api/alunos/${alunoId}`, { method: 'DELETE' });
                if (res.ok) {
                    localStorage.clear();
                    window.location.href = 'login.html';
                }
            } catch (e) { console.error("Erro ao excluir:", e); }
        }
    });

    // Fecha qualquer modal ao clicar na área escura (overlay)[cite: 41, 54]
    window.addEventListener('click', (e) => {
        // Se o alvo do clique for a classe 'modal-overlay', nós o fechamos[cite: 41, 54]
        if (e.target.classList.contains('modal-overlay')) {
            e.target.classList.remove('active');
        }
    });
}

async function enviarPut(payload, modalId) {
    try {
        const res = await fetch(`${CONFIG.API_URL}/api/alunos/${alunoId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        if (res.ok) {
            location.reload();
        } else {
            const erroData = await res.json();
            console.error("Erro do Servidor:", erroData);
            alert("Erro ao salvar: " + (erroData.mensagem || "Verifique os dados."));
        }
    } catch (e) {
        console.error("Erro na requisição:", e);
        alert("Erro de conexão com o servidor.");
    }
}