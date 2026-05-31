import { CONFIG, showAlert } from './config.js';

const container = document.getElementById('listaLoja');
const inputBusca = document.getElementById('buscaTexto');
const inputPrecoMax = document.getElementById('buscaPrecoMax');

const modalResgate = document.getElementById('modalResgate');
const btnConfirmarResgate = document.getElementById('btnConfirmarResgate');
const modalSucesso = document.getElementById('modalSucesso');

const alunoId = localStorage.getItem('usuarioId');
let todasVantagens = [];
let vantagemSelecionadaParaResgate = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!alunoId) {
        window.location.href = 'login.html';
        return;
    }
    await carregarSaldoAluno();
    await carregarLoja();
});

// Busca os dados do aluno para exibir o saldo no topo
async function carregarSaldoAluno() {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/alunos/${alunoId}`);
        if (response.ok) {
            const aluno = await response.json();
            const saldo = aluno.carteira ? aluno.carteira.saldoAtual : 0;
            document.getElementById('saldoAlunoTopo').textContent = saldo;
        }
    } catch (error) {
        console.error("Erro ao buscar saldo:", error);
    }
}

// Busca o catálogo geral
async function carregarLoja() {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/vantagens`);
        if (response.ok) {
            todasVantagens = await response.json();
            renderizarLoja(todasVantagens);
        }
    } catch (error) {
        console.error('Erro ao buscar loja:', error);
        container.innerHTML = `<p class="text-center-muted col-span-full">Erro ao carregar os produtos. Tente novamente.</p>`;
    }
}

// Renderiza os cards na tela
function renderizarLoja(lista) {
    container.innerHTML = '';
    
    if (lista.length === 0) {
        container.innerHTML = `<p class="text-center-muted col-span-full empty-state-padding">Nenhuma vantagem encontrada com esses filtros.</p>`;
        return;
    }

    lista.forEach(v => {
        const fotoHtml = v.urlFoto 
            ? `<img src="${v.urlFoto}" class="vantagem-img">` 
            : `<span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.5;">inventory_2</span>`;

        container.innerHTML += `
            <div class="glass vantagem-card">
                <div>
                    <div class="vantagem-img-container">
                        ${fotoHtml}
                    </div>
                    <div class="header-flex" style="align-items: flex-start !important;">
                        <h5 class="vantagem-title">${v.nome}</h5>
                        <!-- Pequeno badge com o nome da empresa -->
                        <span class="vantagem-empresa-badge">${v.empresa ? v.empresa.nome : 'Parceiro'}</span>
                    </div>
                    <p class="vantagem-desc loja-desc-margin">${v.descricao}</p>
                    <p class="vantagem-custo">
                        <img src="../assets/Lumen.png" alt="Lúmens" style="width: 2.5rem; height: 1.8rem; margin-right: 0.5rem;"> 
                        ${v.custo} Lúmens
                    </p>
                </div>
                <div class="vantagem-actions actions-center">
                    <button class="btn btn-confirmar btn-full-width" onclick="abrirModalResgate(${v.id}, '${v.nome}', ${v.custo})">
                        <span class="material-symbols-outlined">shopping_cart_checkout</span>
                        Resgatar
                    </button>
                </div>
            </div>
        `;
    });
}

// --- LÓGICA DE FILTROS (Pesquisa no Front-end para ser instantâneo) ---
function aplicarFiltros() {
    const texto = inputBusca.value.toLowerCase();
    const precoMax = parseInt(inputPrecoMax.value);

    const filtradas = todasVantagens.filter(v => {
        const nomeMatch = v.nome.toLowerCase().includes(texto);
        const empresaMatch = v.empresa && v.empresa.nome.toLowerCase().includes(texto);
        const descMatch = v.descricao.toLowerCase().includes(texto);
        
        const textMatch = nomeMatch || empresaMatch || descMatch;
        const precoMatch = isNaN(precoMax) || v.custo <= precoMax;

        return textMatch && precoMatch;
    });

    renderizarLoja(filtradas);
}

inputBusca.addEventListener('input', aplicarFiltros);
inputPrecoMax.addEventListener('input', aplicarFiltros);


// --- LÓGICA DE COMPRA / RESGATE ---
window.abrirModalResgate = function(id, nome, custo) {
    vantagemSelecionadaParaResgate = { id, nome, custo };
    
    document.getElementById('resgateTitulo').textContent = nome;
    document.getElementById('resgateCusto').textContent = custo;
    
    modalResgate.classList.add('active');
};

btnConfirmarResgate.addEventListener('click', async () => {
    if (!vantagemSelecionadaParaResgate) return;

    // Desativa o botão para evitar clique duplo
    btnConfirmarResgate.disabled = true;
    btnConfirmarResgate.innerHTML = "Processando...";

    const payload = {
        alunoId: parseInt(alunoId),
        vantagemId: vantagemSelecionadaParaResgate.id
    };

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/pagamentos/resgatar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            // Sucesso! Esconde o modal de confirmação e mostra o cupom
            modalResgate.classList.remove('active');
            document.getElementById('codigoCupomExibicao').textContent = data.cupom;
            const container = document.getElementById("qrcodeContainer");
            container.innerHTML = ""; 
            // Gera o novo QR Code com o código que veio do servidor
            new QRCode(container, {
                text: data.cupom,
                width: 150,
                height: 150
            });
            modalSucesso.classList.add('active');
        } else {
            // Provavelmente saldo insuficiente
            showAlert(data.erro || "Erro ao processar o resgate.", 'error');
            modalResgate.classList.remove('active');
        }
    } catch (error) {
        console.error('Erro na requisição de resgate:', error);
        showAlert('Falha na comunicação com o servidor.', 'error');
        modalResgate.classList.remove('active');
    } finally {
        // Restaura o botão
        btnConfirmarResgate.disabled = false;
        btnConfirmarResgate.innerHTML = "Confirmar Compra";
    }
});