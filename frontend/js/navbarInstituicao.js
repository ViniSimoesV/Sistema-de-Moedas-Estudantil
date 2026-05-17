document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="instituicaoPerfil.html" class="nav-link">
                    <span class="material-symbols-outlined">person</span>
                    Perfil
                </a>
                <a href="listaProfessores.html" class="nav-link">
                    <span class="material-symbols-outlined">badge</span>
                    Professores
                </a>
                <a href="#" id="btnSair" class="nav-link">
                    <span class="material-symbols-outlined">exit_to_app</span>
                    Sair
                </a>
            </nav>
        </header>
        `;

        // Lógica inteligente para destacar a página ativa
        const currentPage = window.location.pathname.split('/').pop();
        const navLinks = document.querySelectorAll('.nav-link');
        
        navLinks.forEach(link => {
            if (link.getAttribute('href') === currentPage) {
                link.classList.add('active');
            }
        });

        // Lógica segura para deslogar e limpar a sessão
        document.getElementById('btnSair').addEventListener('click', (e) => {
            e.preventDefault(); // Impede o '#' de jogar o scroll para o topo
            localStorage.clear();
            window.location.href = '../index.html'; 
        });
    }
});