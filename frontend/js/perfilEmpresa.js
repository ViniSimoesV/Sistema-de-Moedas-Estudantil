import { CONFIG } from './config.js';

const modal = document.getElementById('modalEditar');
const btnEditar = document.querySelector('.btn-edit-text');
const btnFechar = document.getElementById('btnFecharModal');
const formEditar = document.getElementById('formEditarPerfil');
const btnExcluir = document.getElementById('btnExcluirConta');
const modalExcluir = document.getElementById('modalConfirmarExclusao');
const btnAbrirExcluir = document.getElementById('btnExcluirConta');
const btnCancelarExcluir = document.getElementById('btnCancelarExclusao');
const btnConfirmarFinal = document.getElementById('btnConfirmarExclusaoReal');

document.addEventListener('DOMContentLoaded', async () => {
    const empresaId = localStorage.getItem('usuarioId'); 
    
    if (!empresaId) {
        window.location.href = 'login.html';
        return;
    }

    try {
        // Usa CONFIG.API_URL para montar a URL da API
        const response = await fetch(`${CONFIG.API_URL}/api/empresas/${empresaId}`);
        
        if (response.ok) {
            const empresa = await response.json();
            
            // 1. Exibe os dados básicos (nome e CNPJ)
            document.querySelector('.user-name').textContent = empresa.nome;
            document.querySelector('.data-value').textContent = empresa.cnpj;

            // 2. Ramo de Atuação
            const ramoElement = document.querySelector('.ramo-valor') || document.querySelectorAll('.data-value')[1];
            if (ramoElement) {
                ramoElement.textContent = empresa.ramoAtuacao || "Não informado";
            }
            
            // 3. Contador de Vantagens (O que você pediu)
            const countElement = document.querySelector('.lumen-count');
            if (countElement) {
                countElement.textContent = empresa.totalResgatados || 0;
            }
            
            // 4. Foto de Perfil
            if (empresa.urlFotoPerfil) {
                document.querySelector('.perfil-avatar').src = empresa.urlFotoPerfil;
            }

            
        }
    } catch (error) {
        console.error('Erro ao carregar perfil:', error);
    }
});


// Abrir modal
btnEditar.addEventListener('click', () => {
    // Preenche os dados nos campos do modal antes de abrir
    document.getElementById('editNome').value = document.querySelector('.user-name').textContent;
    const ramoAtual = document.querySelector('.ramo-valor').textContent;
    document.getElementById('editRamo').value = ramoAtual === "Não informado" ? "" : ramoAtual;
    
    modal.classList.add('active');
});

// Fechar modal
btnFechar.addEventListener('click', () => {
    modal.classList.remove('active');
});

// Fechar modal ao clicar fora da caixa branca
modal.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.classList.remove('active');
    }
});

// Enviar atualização para o Java
formEditar.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = localStorage.getItem('usuarioId');
    const fileInput = document.getElementById('editFotoFile');
    let urlFotoFinal = document.querySelector('.perfil-avatar').src;

    if (fileInput && fileInput.files.length > 0) {
        const file = fileInput.files[0];
        const fileExt = file.name.split('.').pop();
        const fileName = `${id}-${Date.now()}.${fileExt}`;

        try {
            // Usamos x-upsert para permitir sobrescrever se necessário e simplificar o header
            const uploadResponse = await fetch(`${CONFIG.SUPABASE_URL}/storage/v1/object/foto_de_perfil/${fileName}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${CONFIG.SUPABASE_KEY}`,
                    'apikey': CONFIG.SUPABASE_KEY // Adicionar este campo é o segredo para evitar erros de CORS no Storage
                },
                body: file // O navegador já define o Content-Type automaticamente para arquivos
            });

            if (uploadResponse.ok) {
                urlFotoFinal = `${CONFIG.SUPABASE_URL}/storage/v1/object/public/foto_de_perfil/${fileName}`;
                console.log("Upload OK:", urlFotoFinal);
            } else {
                const errorData = await uploadResponse.json();
                console.error("Erro Supabase:", errorData);
                alert("Erro no servidor: " + errorData.message);
                return;
            }
        } catch (error) {
            console.error("Erro de Rede:", error);
            // Verifique se o CONFIG.SUPABASE_URL não tem barra no final!
            alert("Falha de conexão. Verifique se o URL do Supabase no config.js está correto.");
            return;
        }
    }

    // Só chega aqui se o upload funcionou ou se não havia foto nova
    const dadosAtualizados = {
        nome: document.getElementById('editNome').value,
        ramoAtuacao: document.getElementById('editRamo').value,
        urlFotoPerfil: urlFotoFinal 
    };

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/empresas/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosAtualizados)
        });

        if (response.ok) {
            alert("Perfil atualizado com sucesso!");
            location.reload();
        }
    } catch (error) {
        console.error("Erro ao salvar no Java:", error);
    }
});


// Exclusão de conta
btnExcluir.addEventListener('click', async () => {
    const confirmacao = confirm("Tem certeza absoluta? Todos os seus dados e vantagens serão apagados permanentemente.");
    
    if (confirmacao) {
        const id = localStorage.getItem('usuarioId');
        
        try {
            const response = await fetch(`${CONFIG.API_URL}/api/empresas/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                alert("Conta excluída com sucesso.");
                localStorage.clear(); // Limpa os dados de login
                window.location.href = 'login.html';
            } else {
                alert("Erro ao excluir conta. Tente novamente mais tarde.");
            }
        } catch (error) {
            console.error("Erro na requisição de exclusão:", error);
        }
    }
});