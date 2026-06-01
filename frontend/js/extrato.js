import { CONFIG } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const usuarioId = localStorage.getItem('usuarioId');
    const tipoUsuario = localStorage.getItem('tipoUsuario');

    if (!usuarioId || !tipoUsuario) {
        window.location.href = 'login.html';
        return;
    }

    // 1. INJEÇÃO DINÂMICA DA NAVBAR
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        if (tipoUsuario === 'PROFESSOR') {
            navContainer.innerHTML = `
            <header class="navbar">
                <div class="logo">Uni<span>Rewards</span></div>
                <nav class="nav-links">
                    <a href="professorPerfil.html" class="nav-link nav-item-perfil">
                        <span class="material-symbols-outlined">person</span> Perfil
                    </a>
                    <a href="transacoes.html" class="nav-link nav-item-transferir">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="extrato.html" class="nav-link nav-item-extrato active">
                        <span class="material-symbols-outlined">receipt_long</span> Extrato
                    </a>
                </nav>
            </header>
            `;
        } else if (tipoUsuario === 'ALUNO') {
            navContainer.innerHTML = `
            <header class="navbar">
                <div class="logo">Uni<span>Rewards</span></div>
                <nav class="nav-links">
                    <a href="alunoPerfil.html" class="nav-link nav-item-perfil">
                        <span class="material-symbols-outlined">person</span> Perfil
                    </a>
                    <a href="transacoes.html" class="nav-link nav-item-transferir">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="inventario.html" class="nav-link nav-item-inventario">
                        <span class="material-symbols-outlined">inventory</span> Inventário
                    </a>
                    <a href="lojaAluno.html" class="nav-link nav-item-loja">
                        <span class="material-symbols-outlined">shopping_bag</span> Loja
                    </a>
                    <a href="extrato.html" class="nav-link nav-item-extrato active">
                        <span class="material-symbols-outlined">receipt_long</span> Extrato
                    </a>
                </nav>
            </header>
            `;
        }
    }

    const containerExtrato = document.getElementById('containerExtrato'); 
    const extratoVazio = document.getElementById('extratoVazio');
    const totalRecebidoEl = document.getElementById('totalRecebido'); 
    const totalEnviadoEl = document.getElementById('totalEnviado');   
    const contadorTransacoes = document.getElementById('contadorTransacoes');

    if (!containerExtrato) return;

    try {
        let listaUnificada = [];
        let acumuladoRecebido = 0;
        let acumuladoEnviado = 0;

        // Função utilitária para extrair arrays mesmo se o backend mandar um Objeto (Map)
        const extrairLista = (dados) => {
            if (Array.isArray(dados)) return dados;
            if (typeof dados === 'object' && dados !== null) {
                let extraido = [];
                Object.values(dados).forEach(val => {
                    if (Array.isArray(val)) extraido = extraido.concat(val);
                });
                return extraido;
            }
            return [];
        };

        // Função para mapear Transações (comum para Aluno e Professor)
        const mapearTransacoes = (transacoes) => {
            return transacoes.map(t => {
                // Tenta achar os nomes independente de como o Java mapeou
                const remetenteObj = t.remetente || t.professor || t.origem;
                const destinatarioObj = t.destinatario || t.aluno || t.destino;

                const remetenteId = remetenteObj ? remetenteObj.id : null;
                const remetenteNome = remetenteObj ? remetenteObj.nome : 'Instituição';
                const destinatarioNome = destinatarioObj ? destinatarioObj.nome : 'Usuário';

                const isSaida = (remetenteId == usuarioId);

                if (isSaida) acumuladoEnviado += t.valor;
                else acumuladoRecebido += t.valor;

                return {
                    titulo: isSaida ? `Enviado para ${destinatarioNome}` : `Recebido de ${remetenteNome}`,
                    data: t.dataEnvio || t.dataTransferencia || t.data,
                    valor: (isSaida ? '- ' : '+ ') + t.valor + ' Lúmens',
                    tipo: isSaida ? 'exit' : 'entry',
                    motivo: t.motivo || ''
                };
            });
        };

        if (tipoUsuario === 'PROFESSOR') {
            const resTransacoes = await fetch(`${CONFIG.API_URL}/api/transacoes/professor/${usuarioId}`);
            if (resTransacoes.ok) {
                const dataT = await resTransacoes.json();
                const transacoes = extrairLista(dataT);
                listaUnificada = mapearTransacoes(transacoes);
            }
        } else if (tipoUsuario === 'ALUNO') {
            const [resTransacoes, resPagamentos] = await Promise.all([
                fetch(`${CONFIG.API_URL}/api/transacoes/aluno/${usuarioId}`),
                fetch(`${CONFIG.API_URL}/api/pagamentos/aluno/${usuarioId}`)
            ]);

            let transacoes = [];
            let pagamentos = [];

            if (resTransacoes.ok) {
                const dataT = await resTransacoes.json();
                transacoes = extrairLista(dataT);
            }
            if (resPagamentos.ok) {
                const dataP = await resPagamentos.json();
                pagamentos = extrairLista(dataP);
            }

            const listaT = mapearTransacoes(transacoes);

            const listaP = pagamentos.map(p => {
                acumuladoEnviado += p.valorPago;
                return {
                    titulo: `Resgate na Loja: ${p.vantagem ? p.vantagem.nome : 'Vantagem'}`,
                    data: p.dataCompra || p.data, 
                    valor: '- ' + p.valorPago + ' Lúmens',
                    tipo: 'exit',
                    motivo: 'Vantagem resgatada com sucesso.'
                };
            });

            listaUnificada = [...listaT, ...listaP];
        }

        // Ordena tudo pela data (mais recente primeiro)
        listaUnificada.sort((a, b) => new Date(b.data) - new Date(a.data));

        containerExtrato.innerHTML = '';

        if (listaUnificada.length === 0) {
            if (extratoVazio) extratoVazio.classList.remove('hidden');
        } else {
            if (extratoVazio) extratoVazio.classList.add('hidden');

            listaUnificada.forEach(item => {
                const itemHtml = `
                <div class="transaction-item ${item.tipo}">
                    <div class="item-left">
                        <span class="material-symbols-outlined type-icon">${item.tipo === 'entry' ? 'add_circle' : 'remove_circle'}</span>
                        <div class="item-details">
                            <p class="item-title">${item.titulo}</p>
                            <span class="item-date">${formatarData(item.data)}</span>
                            <p class="item-reason">"${item.motivo || 'Sem mensagem descrita.'}"</p>
                        </div>
                    </div>
                    <div class="item-right">
                        <span class="item-amount">${item.valor}</span>
                    </div>
                </div>`;
                containerExtrato.insertAdjacentHTML('beforeend', itemHtml);
            });
        }

        // Atualizações dos painéis superiores
        if (totalRecebidoEl) totalRecebidoEl.textContent = `+ ${acumuladoRecebido}`;
        if (totalEnviadoEl) totalEnviadoEl.textContent = `- ${acumuladoEnviado}`;
        if (contadorTransacoes) contadorTransacoes.textContent = `${listaUnificada.length} operações`;

    } catch (error) {
        console.error('Erro ao renderizar a linha do tempo do extrato:', error);
        containerExtrato.innerHTML = `<p style="text-align:center;color:#ff4d4d;padding:2rem;">Falha de comunicação ao carregar o extrato.</p>`;
    }
});

function formatarData(dataString) {
    if (!dataString) return '';
    const data = new Date(dataString);
    if (isNaN(data)) return 'Data Indisponível';
    
    return data.toLocaleString('pt-BR', {
        day: '2-digit', month: 'long', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}