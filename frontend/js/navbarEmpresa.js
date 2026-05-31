document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        // Pega o nome do arquivo atual (ex: "vantagens.html")
        const currentPage = window.location.pathname.split('/').pop();
        
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="empresaPerfil.html" class="nav-link ${currentPage === 'empresaPerfil.html' ? 'active' : ''}">
                    <span class="material-symbols-outlined">person</span>
                    Perfil
                </a>
                <a href="vantagens.html" class="nav-link ${currentPage === 'vantagens.html' ? 'active' : ''}">
                    <span class="material-symbols-outlined">inventory</span>
                    Vantagens
                </a>
                <a href="../index.html" class="nav-link">
                    <span class="material-symbols-outlined">logout</span>
                    Sair
                </a>
            </nav>
        </header>
        `;
    }
});