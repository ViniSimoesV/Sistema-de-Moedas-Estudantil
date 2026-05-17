import { CONFIG } from './config.js';
import { showAlert } from './config.js';

// Adicionado o listener para carregar os alertas pendentes (como o do cadastro com sucesso)
document.addEventListener('DOMContentLoaded', () => {
    const pendingAlert = sessionStorage.getItem('pendingAlert');
    if (pendingAlert) {
        const { message, type } = JSON.parse(pendingAlert);
        showAlert(message, type);
        sessionStorage.removeItem('pendingAlert');
    }
});

const formLogin = document.getElementById('form-login');

formLogin.addEventListener('submit', async (event) => {
    event.preventDefault();

    // Pegamos o novo campo 'identificacao'
    const identificacao = document.getElementById('identificacao').value.trim();
    const senha = document.getElementById('senha').value;

    const apenasNumeros = identificacao.replace(/\D/g, ''); // Extrai apenas os números da digitação

    let endpoint = '';
    let payload = {};

    // LÓGICA INTELIGENTE: Se tem '@', é email. Se não tem, é a Sigla da Instituição.
    if (identificacao.includes('@')) {
        // 1. Tem '@' -> É E-mail (Aluno ou Professor)
        endpoint = '/api/usuarios/login';
        payload = { email: identificacao, senha: senha };
        
    } else if (apenasNumeros.length >= 14 || !isNaN(identificacao.charAt(0))) {
        // 2. Não tem '@' e começa com número (ou tem 14 dígitos) -> É CNPJ (Empresa)
        endpoint = '/api/empresas/login';
        payload = { cnpj: identificacao, senha: senha };
        
    } else {
        // 3. Não tem '@' e é texto -> É Sigla (Instituição)
        endpoint = '/api/instituicoes/login';
        payload = { sigla: identificacao, senha: senha };
    }

    try {
        const response = await fetch(`${CONFIG.API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const text = await response.text(); 
            
            try {
                const usuario = JSON.parse(text); 
                localStorage.setItem('usuarioId', usuario.id);
                localStorage.setItem('tipoUsuario', usuario.tipoUsuario);

                // Redirecionamento baseado no tipo
                if (usuario.tipoUsuario === 'ALUNO') {
                    window.location.href = 'alunoPerfil.html';
                } else if (usuario.tipoUsuario === 'EMPRESA') {
                    window.location.href = 'empresaPerfil.html';
                } else if (usuario.tipoUsuario === 'INSTITUICAO') {
                    window.location.href = 'instituicaoPerfil.html'; 
                } else if (usuario.tipoUsuario === 'PROFESSOR') {
                    window.location.href = 'professorPerfil.html'; 
                }
            } catch (e) {
                console.error("FALHA AO CONVERTER JSON:", e);
                showAlert("O servidor enviou um formato inválido. Veja o console.", 'error');
            }
        } else {
            // Se falhou, exibe erro genérico
            showAlert('Credenciais incorretas.', 'error');
        }
    } catch (error) {
        console.error('Erro ao conectar:', error);
        showAlert('Não foi possível conectar ao servidor.', 'error');
    }
});