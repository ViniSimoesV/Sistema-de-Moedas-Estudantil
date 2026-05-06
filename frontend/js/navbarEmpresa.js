document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="vantagens.html" class="nav-link">
                    <span class="material-symbols-outlined">inventory</span>
                    Vantagens
                </a>
                <a href="perfilEmpresa.html" class="nav-link active">
                    <span class="material-symbols-outlined">person</span>
                    Perfil
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