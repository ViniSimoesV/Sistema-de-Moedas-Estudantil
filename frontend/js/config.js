// js/config.js
const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';

export const CONFIG = {
    // Detecta se deve usar o backend local ou o do Render
    API_URL: isLocalhost 
        ? 'http://localhost:8080'
        : 'https://sistema-de-moedas-estudantil.onrender.com',
    SUPABASE_URL: 'https://logixvrbnxyuklwwouad.supabase.co/',
    SUPABASE_KEY: 'sb_publishable_hTOjl9pkgv_KwUiOMxw3mA_LY9wxc4c'
};

export function showAlert(message, type = 'info') {
    // Cria o container se não existir
    let container = document.querySelector('.custom-alert-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'custom-alert-container';
        document.body.appendChild(container);
    }

    // Cria o alerta
    const alert = document.createElement('div');
    alert.className = `custom-alert ${type}`;
    alert.innerHTML = `
        <span>${message}</span>
        <button style="background:none; border:none; color:white; cursor:pointer; margin-left:10px;">&times;</button>
    `;

    container.appendChild(alert);

    // Remove automaticamente após 4 segundos
    setTimeout(() => {
        alert.style.animation = 'fadeOut 0.5s ease forwards';
        setTimeout(() => alert.remove(), 500);
    }, 4000);

    // Fechar no clique do 'x'
    alert.querySelector('button').onclick = () => alert.remove();
}