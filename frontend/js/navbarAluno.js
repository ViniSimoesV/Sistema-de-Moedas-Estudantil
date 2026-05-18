document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="alunoPerfil.html" class="nav-link active">
                    <span class="material-symbols-outlined">person</span>
                    Perfil
                </a>
                <a href="transacoes.html" class="nav-link">
                    <span class="material-symbols-outlined">send_money</span>
                    Transferir
                </a>
                <a href="loja.html" class="nav-link">
                    <span class="material-symbols-outlined">shopping_bag</span>
                    Loja
                </a>
                <a href="inventario.html" class="nav-link">
                    <span class="material-symbols-outlined">inventory</span>
                    Inventário
                </a>
                <a href="../index.html" class="nav-link">
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
    }

    
});