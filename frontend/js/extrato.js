import { CONFIG } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const usuarioId = localStorage.getItem('usuarioId');
    const tipoUsuario = localStorage.getItem('tipoUsuario');

    if (!usuarioId || !tipoUsuario) {
        window.location.href = 'login.html';
        return;
    }

    // 1. INJEÇÃO DINÂMICA DA NAVBAR HÍBRIDA
    const navContainer = document.getElementById('navbar');
    if (navContainer) {
        if (tipoUsuario === 'PROFESSOR') {
            navContainer.innerHTML = `
            <header class="navbar">
                <div class="logo">Uni<span>Rewards</span></div>
                <nav class="nav-links">
                    <a href="professorPerfil.html" class="nav-link">
                        <span class="material-symbols-outlined">person</span> Perfil
                    </a>
                    <a href="transacoes.html" class="nav-link">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="extrato.html" class="nav-link active">
                        <span class="material-symbols-outlined">receipt_long</span> Extrato
                    </a>
                    <a href="#" id="btnSair" class="nav-link">
                        <span class="material-symbols-outlined">exit_to_app</span> Sair
                    </a>
                </nav>
            </header>
            `;
        } else {
            navContainer.innerHTML = `
            <header class="navbar">
                <div class="logo">Uni<span>Rewards</span></div>
                <nav class="nav-links">
                    <a href="alunoPerfil.html" class="nav-link">
                        <span class="material-symbols-outlined">person</span> Perfil
                    </a>
                    <a href="transacoes.html" class="nav-link">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="loja.html" class="nav-link">
                        <span class="material-symbols-outlined">shopping_bag</span> Loja
                    </a>
                    <a href="inventario.html" class="nav-link">
                        <span class="material-symbols-outlined">inventory</span> Inventário
                    </a>
                    <a href="extrato.html" class="nav-link active">
                        <span class="material-symbols-outlined">receipt_long</span> Extrato
                    </a>
                    <a href="#" id="btnSair" class="nav-link">
                        <span class="material-symbols-outlined">exit_to_app</span> Sair
                    </a>
                </nav>
            </header>
            `;
        }

        document.getElementById('btnSair')?.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.clear();
            window.location.href = '../index.html';
        });
    }

    const containerExtrato = document.getElementById('containerExtrato');
    const totalRecebidoEl = document.getElementById('totalRecebido');
    const totalEnviadoEl = document.getElementById('totalEnviado');
    const contadorTransacoesEl = document.getElementById('contadorTransacoes');
    const extratoVazioEl = document.getElementById('extratoVazio');

    // Limpa os dados estáticos/prefixos do HTML de molde
    containerExtrato.innerHTML = '';

    // Função auxiliar para formatar a data ISO de forma elegante
    function formatarData(dataIso) {
        if (!dataIso) return 'Data desconhecida';
        const data = new Date(dataIso);
        return data.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        }) + ' às ' + data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    }

    // 2. RENDERIZAÇÃO DOS FLUXOS DE DADOS
    try {
        if (tipoUsuario === 'PROFESSOR') {
            // Histórico do Professor: Apenas distribuições de saídas
            const response = await fetch(`${CONFIG.API_URL}/api/transacoes/professor/${usuarioId}`);
            if (response.ok) {
                const transacoes = await response.json();
                
                let totalDistribuido = 0;
                contadorTransacoesEl.textContent = `${transacoes.length} distribuições`;

                if (transacoes.length === 0) {
                    extratoVazioEl.classList.remove('hidden');
                    return;
                }

                transacoes.forEach(t => {
                    totalDistribuido += t.valor;
                    const itemHtml = `
                    <div class="transaction-item exit">
                        <div class="item-left">
                            <span class="material-symbols-outlined type-icon">remove_circle</span>
                            <div class="item-details">
                                <p class="item-title">Envio de prêmio para <strong>${t.aluno.nome}</strong></p>
                                <span class="item-date">${formatarData(t.dataEnvio)}</span>
                                <p class="item-reason">"${t.mensagem || 'Sem justificativa informada.'}"</p>
                            </div>
                        </div>
                        <div class="item-right">
                            <span class="item-amount">- ${t.valor} Lúmens</span>
                        </div>
                    </div>`;
                    containerExtrato.insertAdjacentHTML('beforeend', itemHtml);
                });

                totalRecebidoEl.textContent = "0";
                totalEnviadoEl.textContent = `- ${totalDistribuido}`;
            }
        } else {
            // Histórico do Aluno: Mistura entradas (professores/colegas) e saídas (colegas)
            const response = await fetch(`${CONFIG.API_URL}/api/transacoes/aluno/${usuarioId}`);
            if (response.ok) {
                const dados = await response.json();
                
                let listaUnificada = [];
                let acumuladoRecebido = 0;
                let acumuladoEnviado = 0;

                // Processa entradas vindas de Professores
                dados.recebidosProfessores.forEach(t => {
                    acumuladoRecebido += t.valor;
                    listaUnificada.push({
                        tipo: 'entry',
                        titulo: `Recompensa recebida do <strong>Prof. ${t.professor.nome}</strong>`,
                        data: t.dataEnvio,
                        motivo: t.mensagem,
                        valor: `+ ${t.valor} Lúmens`
                    });
                });

                // Processa entradas vindas de outros Alunos
                dados.recebidosAlunos.forEach(t => {
                    acumuladoRecebido += t.valor;
                    listaUnificada.push({
                        tipo: 'entry',
                        titulo: `Transferência recebida de <strong>${t.remetente.nome}</strong>`,
                        data: t.dataEnvio,
                        motivo: t.motivo,
                        valor: `+ ${t.valor} Lúmens`
                    });
                });

                // Processa saídas enviadas para outros Alunos
                dados.enviadosAlunos.forEach(t => {
                    acumuladoEnviado += t.valor;
                    listaUnificada.push({
                        tipo: 'exit',
                        titulo: `Transferência enviada para <strong>${t.destinatario.nome}</strong>`,
                        data: t.dataEnvio,
                        motivo: t.motivo,
                        valor: `- ${t.valor} Lúmens`
                    });
                });

                // Ordena a pilha inteira por data decrescente (mais recente primeiro)
                listaUnificada.sort((a, b) => new Date(b.data) - new Date(a.data));
                contadorTransacoesEl.textContent = `${listaUnificada.length} operações`;

                if (listaUnificada.length === 0) {
                    extratoVazioEl.classList.remove('hidden');
                    return;
                }

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

                totalRecebidoEl.textContent = `+ ${acumuladoRecebido}`;
                totalEnviadoEl.textContent = `- ${acumuladoEnviado}`;
            }
        }
    } catch (error) {
        console.error('Erro ao renderizar a linha do tempo do extrato:', error);
        containerExtrato.innerHTML = `<p style="text-align:center;color:#ff4d4d;padding:2rem;">Falha de comunicação ao carregar o extrato bancário.</p>`;
    }
});