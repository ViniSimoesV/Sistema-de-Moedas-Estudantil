import { CONFIG } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const alunoId = localStorage.getItem('usuarioId');
    const tipoUsuario = localStorage.getItem('tipoUsuario');

    if (!alunoId || tipoUsuario !== 'ALUNO') {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/alunos/${alunoId}`);
        if (response.ok) {
            const aluno = await response.json();

            // Dados da tabela 'usuarios'
            document.querySelector('.user-name').textContent = aluno.nome;
            if (aluno.urlFotoPerfil) {
                document.querySelector('.perfil-avatar').src = aluno.urlFotoPerfil;
            }

            // Dados da tabela 'alunos'
            const dataValues = document.querySelectorAll('.data-value');
            dataValues[0].textContent = aluno.curso || "Não informado";
            
            // Dados da tabela 'instituicoes' via 'usuarios_academicos'
            dataValues[1].textContent = aluno.instituicao ? aluno.instituicao.nome : "Não informado";

            // Lógica da Carteira (tabela 'carteiras')
            const lumenCount = document.querySelector('.lumen-count');
            if (lumenCount && aluno.carteira) {
                lumenCount.textContent = aluno.carteira.saldoAtual || 0;
            }

            // Lógica de Endereço (tabela 'alunos')
            const areaCompletar = document.getElementById('completar-perfil-area');
            if (aluno.rua && aluno.numero) {
                areaCompletar.innerHTML = `
                    <div class="data-group">
                        <span class="data-label">Logradouro</span>
                        <p class="data-value">${aluno.rua}, ${aluno.numero} ${aluno.complemento || ''}</p>
                    </div>
                    <div class="data-group">
                        <span class="data-label">Localidade</span>
                        <p class="data-value">${aluno.bairro} - ${aluno.cidade}</p>
                    </div>
                `;
                areaCompletar.classList.remove('centered');
            }
        }
    } catch (error) {
        console.error('Erro ao carregar perfil:', error);
    }
});