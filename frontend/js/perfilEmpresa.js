import { CONFIG } from './config.js';

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