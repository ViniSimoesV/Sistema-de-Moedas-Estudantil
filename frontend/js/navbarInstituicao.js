document.addEventListener("DOMContentLoaded", () => {
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        navContainer.innerHTML = `
        <header class="navbar">
            <div class="logo">Uni<span>Rewards</span></div>
            <nav class="nav-links">
                <a href="listaProfessores.html" class="nav-link">
                    <span class="material-symbols-outlined">school</span>
                    Professores
                </a>
                <a href="instituicaoPerfil.html" class="nav-link active">
                    <span class="material-symbols-outlined">account_balance</span>
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