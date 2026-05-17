import { CONFIG, showAlert } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const professorId = localStorage.getItem('usuarioId'); 
    const tipoUsuario = localStorage.getItem('tipoUsuario');
    
    // Proteção de Rota
    if (!professorId || tipoUsuario !== 'PROFESSOR') {
        window.location.href = 'login.html';
        return;
    }

    // 1. CARREGAR DADOS DO PROFESSOR
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/professores/${professorId}`);
        
        if (response.ok) {
            const prof = await response.json();
            
            // Preenche o HTML
            document.querySelector('.user-name').textContent = prof.nome;
            document.getElementById('infoDepartamento').textContent = prof.departamento;
            document.getElementById('infoEmail').textContent = prof.email;
            document.getElementById('infoCpf').textContent = prof.cpf;

            // Preenche o saldo da carteira (se existir)
            if (prof.carteira && prof.carteira.saldoAtual !== undefined) {
                document.getElementById('saldoLumens').textContent = prof.carteira.saldoAtual;
            }

            // Preenche a foto se existir
            if (prof.urlFotoPerfil) {
                document.querySelector('.perfil-avatar').src = prof.urlFotoPerfil;
            }
        } else {
            showAlert("Erro ao carregar os dados do perfil.", "error");
        }
    } catch (error) {
        console.error('Erro ao carregar perfil:', error);
        showAlert("Erro de conexão com o servidor.", "error");
    }
    
    // 2. CONTROLO DO MODAL DE EDIÇÃO
    const modal = document.getElementById('modalEditar');
    
    document.getElementById('btnAbrirEdicao').addEventListener('click', () => {
        document.getElementById('editNome').value = document.querySelector('.user-name').textContent;
        // Puxa o email atual para o modal
        document.getElementById('editEmail').value = document.getElementById('infoEmail').textContent; 
        modal.classList.add('active');
    });

    document.getElementById('btnFecharModal').addEventListener('click', () => {
        modal.classList.remove('active');
    });

    // 3. SALVAR ALTERAÇÕES 
    document.getElementById('formEditarPerfil').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const payload = {
            nome: document.getElementById('editNome').value,
            email: document.getElementById('editEmail').value, // Envia o novo e-mail
            cpf: document.getElementById('infoCpf').textContent,
            departamento: document.getElementById('infoDepartamento').textContent,
        };

        const inputFoto = document.getElementById('editFotoFile');
        const fotoFile = inputFoto.files[0];

        try {
            if (fotoFile) {
                payload.urlFotoPerfil = await converteBase64(fotoFile);
            }

            const response = await fetch(`${CONFIG.API_URL}/api/professores/${professorId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                const profAtualizado = await response.json();
                
                // Atualiza a tela imediatamente
                document.querySelector('.user-name').textContent = profAtualizado.nome;
                document.getElementById('infoEmail').textContent = profAtualizado.email;
                
                if (profAtualizado.urlFotoPerfil) {
                    document.querySelector('.perfil-avatar').src = profAtualizado.urlFotoPerfil;
                }
                
                inputFoto.value = ''; // Limpa o input de arquivo
                modal.classList.remove('active');
                showAlert('Perfil atualizado com sucesso!', 'success');
            } else {
                const erro = await response.json();
                showAlert(`Erro: ${erro.erro || 'Não foi possível atualizar os dados.'}`, 'error');
            }
        } catch (error) {
            console.error('Erro no PUT:', error);
            showAlert('Falha na comunicação com o servidor.', 'error');
        }
    });

    function converteBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
        });
    }
});