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
            const text = await response.text(); // Pega como texto puro primeiro
            console.log("CONTEÚDO RECEBIDO DO SERVIDOR:", text); 
            
            try {
                const usuario = JSON.parse(text); // Tenta converter manualmente
                localStorage.setItem('usuarioId', usuario.id);
                localStorage.setItem('tipoUsuario', usuario.tipoUsuario);

                if (usuario.tipoUsuario === 'ALUNO') {
                    window.location.href = 'alunoPerfil.html';
                } else if (usuario.tipoUsuario === 'EMPRESA') {
                    window.location.href = 'empresaPerfil.html';
                }
            } catch (e) {
                console.error("FALHA AO CONVERTER JSON:", e);
                alert("O servidor enviou um formato inválido. Veja o console.");
            }
        } else {
            alert('E-mail ou senha incorretos.');
        }
    } catch (error) {
        console.error('Erro ao conectar:', error);
        alert('Não foi possível conectar ao servidor.');
    }
});