import { CONFIG, showAlert } from './config.js';

document.addEventListener('DOMContentLoaded', () => {
    const instituicaoId = localStorage.getItem('usuarioId'); 
    const tipoUsuario = localStorage.getItem('tipoUsuario');
    
    // 1. Proteção de Rota
    if (!instituicaoId || tipoUsuario !== 'INSTITUICAO') {
        window.location.href = 'login.html';
        return;
    }

    // 2. Controlos dos Modais
    const modalManual = document.getElementById('modalProfessorManual');
    const modalUpload = document.getElementById('modalUploadLista');

    document.getElementById('btnNovoProfessor').addEventListener('click', () => {
        modalManual.classList.add('active');
    });

    document.getElementById('btnUploadLista').addEventListener('click', () => {
        modalUpload.classList.add('active');
    });

    // ==========================================
    // 3. CARREGAR LISTA AO ABRIR A PÁGINA (GET)
    // ==========================================
    carregarProfessores(instituicaoId);

    // ==========================================
    // 4. CADASTRAR PROFESSOR MANUALMENTE (POST)
    // ==========================================
    document.getElementById('formProfessorManual').addEventListener('submit', async (e) => {
        e.preventDefault(); // Impede a página de recarregar

        // Monta o DTO exatamente como o Java espera
        const payload = {
            nome: document.getElementById('profNome').value,
            cpf: document.getElementById('profCpf').value,
            email: document.getElementById('profEmail').value,
            departamento: document.getElementById('profDepartamento').value,
            instituicaoId: parseInt(instituicaoId) // Vincula à instituição logada
        };

        try {
            const response = await fetch(`${CONFIG.API_URL}/api/professores`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                showAlert('Professor cadastrado! Carteira de 1000 Lúmens gerada.', 'success');
                modalManual.classList.remove('active');
                document.getElementById('formProfessorManual').reset(); // Limpa os campos
                carregarProfessores(instituicaoId); // Atualiza a tabela na hora
            } else {
                const erro = await response.json();
                showAlert(`Erro: ${erro.erro || 'Falha ao cadastrar.'}`, 'error');
            }
        } catch (error) {
            console.error('Erro no POST:', error);
            showAlert('Falha na comunicação com o servidor.', 'error');
        }
    });

    // 5. UPLOAD DE CSV (Deixaremos para o próximo passo)
    document.getElementById('formUploadLista').addEventListener('submit', async (e) => {
        e.preventDefault();
        modalUpload.classList.remove('active');
    });
});

// ==========================================
//  UPLOAD DE LISTA CSV (POST com FormData)
// ==========================================
document.getElementById('formUploadLista').addEventListener('submit', async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById('arquivoCsv');
    const file = fileInput.files[0];
    const instituicaoId = localStorage.getItem('usuarioId');

    if (!file) {
        showAlert('Por favor, selecione um ficheiro CSV.', 'error');
        return;
    }

    // Usamos FormData para enviar ficheiros em vez de JSON
    const formData = new FormData();
    formData.append('file', file);
    formData.append('instituicaoId', instituicaoId);

    try {
        // NOTA: Ao enviar FormData, NÃO se coloca o 'Content-Type' nos headers.
        // O navegador configura automaticamente o boundary correto para o ficheiro!
        const response = await fetch(`${CONFIG.API_URL}/api/professores/upload`, {
            method: 'POST',
            body: formData 
        });

        if (response.ok) {
            const data = await response.json();
            showAlert(data.mensagem, 'success');
            
            document.getElementById('modalUploadLista').classList.remove('active');
            fileInput.value = ''; // Limpa o input
            carregarProfessores(instituicaoId); // Recarrega a tabela na tela
        } else {
            const erro = await response.json();
            showAlert(`Erro: ${erro.erro || 'Falha ao processar o ficheiro.'}`, 'error');
        }
    } catch (error) {
        console.error('Erro no Upload:', error);
        showAlert('Falha na comunicação com o servidor.', 'error');
    }
});

// ==========================================
//  AÇÕES DA TABELA E MODAIS (EDITAR/EXCLUIR)
// ==========================================
let professorIdParaExcluir = null; // Guarda o ID temporariamente

document.getElementById('tabelaProfessoresBody').addEventListener('click', async (e) => {
    
    // --- AÇÃO DE EXCLUIR (Abre o Modal) ---
    const btnExcluir = e.target.closest('.btn-excluir-prof');
    if (btnExcluir) {
        professorIdParaExcluir = btnExcluir.getAttribute('data-id');
        document.getElementById('modalExcluirProfessor').classList.add('active');
    }

    // --- AÇÃO DE EDITAR (Abre o Modal preenchido) ---
    const btnEditar = e.target.closest('.btn-editar-prof');
    if (btnEditar) {
        const profId = btnEditar.getAttribute('data-id');
        const tr = btnEditar.closest('tr'); // Pega a linha inteira da tabela
        
        // Puxa os dados direto das colunas da tabela para a tela ficar super rápida
        document.getElementById('editProfId').value = profId;
        document.getElementById('editProfNome').value = tr.cells[0].textContent;
        document.getElementById('editProfCpf').value = tr.cells[1].textContent;
        document.getElementById('editProfEmail').value = tr.cells[2].textContent;
        document.getElementById('editProfDepartamento').value = tr.cells[3].textContent;

        document.getElementById('modalEditarProfessor').classList.add('active');
    }
});

// --- EXECUTA A EXCLUSÃO ---
document.getElementById('btnConfirmarExclusao').addEventListener('click', async () => {
        if (!professorIdParaExcluir) return;

        const instituicaoId = localStorage.getItem('usuarioId');

        try {
            const response = await fetch(`${CONFIG.API_URL}/api/professores/${professorIdParaExcluir}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                showAlert('Professor removido com sucesso!', 'success');
                document.getElementById('modalExcluirProfessor').classList.remove('active');
                carregarProfessores(instituicaoId); 
            } else {
                showAlert('Erro ao tentar remover o professor.', 'error');
            }
        } catch (error) {
            console.error('Erro no DELETE:', error);
            showAlert('Falha na comunicação com o servidor.', 'error');
        }
    });

// --- EXECUTA A EDIÇÃO ---
document.getElementById('formEditarProfessor').addEventListener('submit', async (e) => {
        e.preventDefault();

        const instituicaoId = localStorage.getItem('usuarioId');

        const profId = document.getElementById('editProfId').value;
        const payload = {
            nome: document.getElementById('editProfNome').value,
            cpf: document.getElementById('editProfCpf').value,
            email: document.getElementById('editProfEmail').value,
            departamento: document.getElementById('editProfDepartamento').value,
            instituicaoId: parseInt(instituicaoId)
        };

        try {
            const response = await fetch(`${CONFIG.API_URL}/api/professores/${profId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                showAlert('Professor atualizado com sucesso!', 'success');
                document.getElementById('modalEditarProfessor').classList.remove('active');
                carregarProfessores(instituicaoId); // Atualiza a tela sem dar erro
            } else {
                const erro = await response.json();
                showAlert(`Erro: ${erro.erro || 'Falha ao atualizar.'}`, 'error');
            }
        } catch (error) {
            console.error('Erro no PUT:', error);
            showAlert('Falha na comunicação com o servidor.', 'error');
        }
    });

// ==========================================
// FUNÇÕES AUXILIARES
// ==========================================

async function carregarProfessores(instituicaoId) {
    
    const tbody = document.getElementById('tabelaProfessoresBody');
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 2rem;">Carregando professores...</td></tr>`;

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/professores/instituicao/${instituicaoId}`);
        
        if (response.ok) {
            const professores = await response.json();
            
            if (professores.length === 0) {
                renderizarTabelaVazia();
            } else {
                tbody.innerHTML = ''; // Limpa a tabela
                
                // Injeta cada professor retornado do banco de dados na tabela HTML
                professores.forEach(prof => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td><strong>${prof.nome}</strong></td>
                        <td>${prof.cpf}</td>
                        <td>${prof.email}</td>
                        <td>${prof.departamento}</td>
                        <td style="text-align: center;">
                            <button class="btn-icon btn-editar-prof" data-id="${prof.id}" title="Editar"><span class="material-symbols-outlined">edit</span></button>
                            <button class="btn-icon danger btn-excluir-prof" data-id="${prof.id}" title="Excluir"><span class="material-symbols-outlined">delete</span></button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        } else {
            renderizarTabelaVazia();
            showAlert('Erro ao buscar a lista de professores.', 'error');
        }
    } catch (error) {
        console.error('Erro no GET:', error);
        renderizarTabelaVazia();
    }
}

function renderizarTabelaVazia() {
    const tbody = document.getElementById('tabelaProfessoresBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="5" style="text-align: center; padding: 2rem; color: var(--text-secondary);">
                <span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.5; display: block; margin-bottom: 10px;">group_off</span>
                Nenhum professor cadastrado ainda. <br> Utilize os botões acima para adicionar.
            </td>
        </tr>
    `;
}