import { CONFIG, showAlert } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const substituicaoId = localStorage.getItem('usuarioId'); 
    const tipoUsuario = localStorage.getItem('tipoUsuario');
    
    // Proteção de Rota
    if (!substituicaoId || tipoUsuario !== 'INSTITUICAO') {
        window.location.href = 'login.html';
        return;
    }

    // ==========================================
    // 1. CARREGAR PERFIL (GET)
    // ==========================================
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/instituicoes/${substituicaoId}`);
        
        if (response.ok) {
            const inst = await response.json();
            document.querySelector('.user-name').textContent = inst.nome;
            document.querySelector('.sigla-valor').textContent = inst.sigla;
            if (inst.urlFotoPerfil) {
                document.querySelector('.perfil-avatar').src = inst.urlFotoPerfil;
            }
        }
    } catch (error) {
        console.error('Erro ao carregar perfil:', error);
        showAlert("Erro ao conectar com o servidor.", "error");
    }
    
    // ==========================================
    // 2. ABRIR E FECHAR MODAL
    // ==========================================
    const modal = document.getElementById('modalEditar');
    
    document.getElementById('btnAbrirEdicao').addEventListener('click', () => {
        document.getElementById('editNome').value = document.querySelector('.user-name').textContent;
        document.getElementById('editSigla').value = document.querySelector('.sigla-valor').textContent;
        modal.classList.add('active');
    });

    document.getElementById('btnFecharModal').addEventListener('click', () => {
        modal.classList.remove('active');
    });

    // ==========================================
    // 3. SALVAR ALTERAÇÕES (PUT)
    // ==========================================
    const formEditar = document.getElementById('formEditarPerfil');
    
    formEditar.addEventListener('submit', async (event) => {
        event.preventDefault(); 

        const novoNome = document.getElementById('editNome').value;
        const novaSigla = document.getElementById('editSigla').value;
        const inputFoto = document.getElementById('editFotoFile');
        const fotoFile = inputFoto.files[0]; // Captura o arquivo de imagem selecionado

        // Cria o payload base
        const payload = {
            nome: novoNome,
            sigla: novaSigla
        };

        try {
            // Se o usuário tiver selecionado um arquivo de imagem, converte para Base64
            if (fotoFile) {
                payload.urlFotoPerfil = await converteBase64(fotoFile);
            }

            const response = await fetch(`${CONFIG.API_URL}/api/instituicoes/${substituicaoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload) // Agora envia a foto tratada no JSON!
            });

            if (response.ok) {
                const instAtualizada = await response.json();
                
                // Atualiza a tela imediatamente com os novos dados retornados do Java
                document.querySelector('.user-name').textContent = instAtualizada.nome;
                document.querySelector('.sigla-valor').textContent = instAtualizada.sigla;
                
                if (instAtualizada.urlFotoPerfil) {
                    document.querySelector('.perfil-avatar').src = instAtualizada.urlFotoPerfil;
                }
                
                // Limpa o input de arquivo para o próximo uso
                inputFoto.value = '';
                
                modal.classList.remove('active');
                showAlert('Perfil atualizado com sucesso!', 'success');
            } else {
                showAlert('Não foi possível atualizar os dados.', 'error');
            }
        } catch (error) {
            console.error('Erro no PUT:', error);
            showAlert('Falha na comunicação com o servidor.', 'error');
        }
    });

    // Função utilitária para converter arquivos de imagem em String Base64
    function converteBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
        });
    }

    // ==========================================
    // 4. ZONA DE PERIGO: EXCLUIR CONTA (DELETE)
    // ==========================================
    const btnExcluir = document.getElementById('btnExcluirConta');
    if (btnExcluir) {
        btnExcluir.addEventListener('click', async () => {
            const confirmacao = confirm("Tem certeza absoluta? Esta ação apagará a sua instituição e os vínculos com os professores de forma permanente!");
            
            if (confirmacao) {
                try {
                    const response = await fetch(`${CONFIG.API_URL}/api/instituicoes/${substituicaoId}`, {
                        method: 'DELETE'
                    });

                    if (response.ok) {
                        alert("Instituição excluída com sucesso.");
                        localStorage.clear();
                        window.location.href = '../index.html';
                    } else {
                        showAlert("Erro ao tentar excluir a conta.", "error");
                    }
                } catch (error) {
                    console.error('Erro no DELETE:', error);
                    showAlert("Falha na comunicação com o servidor.", "error");
                }
            }
        });
    }
});