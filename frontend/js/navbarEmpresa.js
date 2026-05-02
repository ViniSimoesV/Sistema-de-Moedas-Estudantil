document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="dashboardEmpresa.html" class="nav-link">Início</a>
                <a href="vantagens.html" class="nav-link">Vantagens</a>
                <a href="perfilEmpresa.html" class="nav-link active">Perfil</a>
                <a href="../index.html" class="nav-link">Sair</a>
            </nav>
        </header>
        `;
    }
});