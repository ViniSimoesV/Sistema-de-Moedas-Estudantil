document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="professorPerfil.html" class="nav-link">
                    <span class="material-symbols-outlined">person</span>
                    Perfil
                </a>
                <a href="transacoes.html" class="nav-link">
                    <span class="material-symbols-outlined">send_money</span>
                    Transferir
                </a>
                <a href="extrato.html" class="nav-link">
                    <span class="material-symbols-outlined">receipt_long</span>
                    Extrato
                </a>
                <a href="#" id="btnSair" class="nav-link">
                    <span class="material-symbols-outlined">exit_to_app</span>
                    Sair
                </a>
            </nav>
        </header>
        `;

        // Lógica limpa para destacar a página atual com a classe padrão 'active'
        const currentPage = window.location.pathname.split('/').pop();
        const navLinks = document.querySelectorAll('.nav-link');
        
        navLinks.forEach(link => {
            if (link.getAttribute('href') === currentPage) {
                link.classList.add('active');
            }
        });

        // Lógica para deslogar e limpar o localStorage com segurança
        document.getElementById('btnSair').addEventListener('click', (e) => {
            e.preventDefault(); 
            localStorage.clear();
            window.location.href = '../index.html'; 
        });
    }
});