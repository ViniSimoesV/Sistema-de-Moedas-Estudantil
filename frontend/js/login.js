import { CONFIG } from './config.js';

const formLogin = document.getElementById('form-login');

formLogin.addEventListener('submit', async (event) => {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        if (response.ok) {
            const usuario = await response.json();
            
            // Salva dados essenciais para os perfis carregarem
            localStorage.setItem('usuarioId', usuario.id);
            localStorage.setItem('tipoUsuario', usuario.tipoUsuario);

            alert(`Bem-vindo, ${usuario.nome}!`);

            // Redireciona conforme o tipo de usuário
            if (usuario.tipoUsuario === 'ALUNO') {
                window.location.href = 'alunoPerfil.html';
            } else if (usuario.tipoUsuario === 'EMPRESA') {
                window.location.href = 'empresaPerfil.html';
            }
        } else {
            alert('E-mail ou senha incorretos.');
        }
    } catch (error) {
        console.error('Erro ao conectar:', error);
        alert('Não foi possível conectar ao servidor.');
    }
});