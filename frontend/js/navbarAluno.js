document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="dashboard.html" class="nav-link">Home</a>
                <a href="loja.html" class="nav-link">Loja</a>
                <a href="alunoPerfil.html" class="nav-link active">Perfil</a>
                <a href="../index.html" class="nav-link">Sair</a>
            </nav>
        </header>
        `;
    }
});