import { CONFIG, showAlert } from './config.js';

document.addEventListener('DOMContentLoaded', async () => {
    const usuarioId = localStorage.getItem('usuarioId');
    const tipoUsuario = localStorage.getItem('tipoUsuario');

    if (!usuarioId || !tipoUsuario) {
        window.location.href = 'login.html';
        return;
    }

    // Injeção Dinâmica da Navbar
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
                    <a href="transacoes.html" class="nav-link active">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="professorExtrato.html" class="nav-link">
                        <span class="material-symbols-outlined">receipt_long</span> Extrato
                    </a>
                    <a href="#" id="btnSair" class="nav-link">
                        <span class="material-symbols-outlined">exit_to_app</span> Sair
                    </a>
                </nav>
            </header>
            `;
        } else if (tipoUsuario === 'ALUNO') {
            navContainer.innerHTML = `
            <header class="navbar">
                <div class="logo">Uni<span>Rewards</span></div>
                <nav class="nav-links">
                    <a href="alunoPerfil.html" class="nav-link">
                        <span class="material-symbols-outlined">person</span> Perfil
                    </a>
                    <a href="transacoes.html" class="nav-link active">
                        <span class="material-symbols-outlined">send_money</span> Transferir
                    </a>
                    <a href="loja.html" class="nav-link">
                        <span class="material-symbols-outlined">shopping_bag</span> Loja
                    </a>
                    <a href="inventario.html" class="nav-link">
                        <span class="material-symbols-outlined">inventory</span> Inventário
                    </a>
                    <a href="extrato.html" class="nav-link">
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

    // Adaptação Textual dos Elementos
    const instrucao = document.getElementById('instrucaoTipoUsuario');
    const labelSaldo = document.getElementById('labelTipoSaldo');
    const labelDestinatario = document.getElementById('labelDestinatario');
    const selectDestinatario = document.getElementById('selectDestinatario');

    if (tipoUsuario === 'PROFESSOR') {
        if (instrucao) instrucao.textContent = 'Distribua prêmios acadêmicos aos seus alunos';
        if (labelSaldo) labelSaldo.textContent = 'Lúmens institucionais disponíveis para envio';
        if (labelDestinatario) labelDestinatario.textContent = 'Selecione o Aluno Destinatário';
    } else {
        if (instrucao) instrucao.textContent = 'Envie moedas para colegas ou resgate benefícios';
        if (labelSaldo) labelSaldo.textContent = 'Seus Lúmens acumulados na carteira';
        if (labelDestinatario) labelDestinatario.textContent = 'Selecione o Aluno Destinatário';
    }

    let instituicaoIdAtual = null;

    // Busca de Dados do Usuário Logado e do Saldo da Carteira
    async function carregarDadosUsuario() {
        try {
            const endpoint = tipoUsuario === 'PROFESSOR' ? `professores/${usuarioId}` : `alunos/${usuarioId}`;
            const response = await fetch(`${CONFIG.API_URL}/api/${endpoint}`);
            
            if (response.ok) {
                const dados = await response.json();
                
                const saldoElement = document.getElementById('saldoDisponivel');
                if (saldoElement) {
                    if (dados.carteira && dados.carteira.saldoAtual !== undefined) {
                        saldoElement.textContent = dados.carteira.saldoAtual;
                    } else {
                        saldoElement.textContent = "0";
                    }
                }

                // Captura robusta da Instituição
                if (dados.instituicao && dados.instituicao.id) {
                    instituicaoIdAtual = dados.instituicao.id;
                } else if (dados.instituicaoId) {
                    instituicaoIdAtual = dados.instituicaoId;
                } else if (dados.usuarioAcademico && dados.usuarioAcademico.instituicao) {
                    instituicaoIdAtual = dados.usuarioAcademico.instituicao.id;
                }

                if (instituicaoIdAtual) {
                    await carregarDestinatarios();
                } else {
                    selectDestinatario.innerHTML = `<option value="" disabled selected>Erro: Vínculo institucional não encontrado</option>`;
                }
            } else {
                showAlert('Não foi possível carregar os dados do seu perfil.', 'error');
            }
        } catch (error) {
            console.error('Erro ao buscar dados do usuário:', error);
            const saldoElement = document.getElementById('saldoDisponivel');
            if (saldoElement) saldoElement.textContent = "Erro";
        }
    }

    // Carregar Alunos por Instituição
    async function carregarDestinatarios() {
        try {
            const response = await fetch(`${CONFIG.API_URL}/api/alunos/instituicao/${instituicaoIdAtual}`);
            
            if (response.ok) {
                const alunos = await response.json();
                
                if (!alunos || alunos.length === 0) {
                    selectDestinatario.innerHTML = `<option value="" disabled selected>Nenhum aluno localizado nesta instituição</option>`;
                    return;
                }

                selectDestinatario.innerHTML = `<option value="" disabled selected>Escolha o aluno destinatário...</option>`;
                
                alunos.forEach(aluno => {
                    if (tipoUsuario === 'ALUNO' && String(aluno.id) === String(usuarioId)) {
                        return;
                    }
                    
                    const option = document.createElement('option');
                    option.value = aluno.id;
                    option.textContent = `${aluno.nome} (${aluno.curso || 'Estudante'})`;
                    selectDestinatario.appendChild(option);
                });
            } else {
                selectDestinatario.innerHTML = `<option value="" disabled selected>Erro ao carregar alunos do servidor</option>`;
            }
        } catch (error) {
            console.error('Erro ao buscar destinatários:', error);
            selectDestinatario.innerHTML = `<option value="" disabled selected>Falha na conexão com os dados</option>`;
        }
    }

    await carregarDadosUsuario();

    // Manipulação do Formulário de Envio
    const formTransferencia = document.getElementById('formTransferência');
    formTransferencia.addEventListener('submit', async (e) => {
        e.preventDefault();

        const destinatarioId = selectDestinatario.value;
        const quantidade = parseInt(document.getElementById('inputQuantidade').value, 10);
        const motivo = document.getElementById('inputMotivo').value;

        if (!destinatarioId) {
            showAlert('Por favor, escolha um aluno destinatário.', 'error');
            return;
        }

        const saldoAtualNum = parseInt(document.getElementById('saldoDisponivel').textContent, 10);
        if (quantidade > saldoAtualNum) {
            showAlert('Você não possui saldo de Lúmens suficiente.', 'error');
            return;
        }

        const transacaoPayload = {
            remetenteId: usuarioId,
            destinatarioId: destinatarioId,
            valor: quantidade,
            motivo: motivo,
            tipoRemetente: tipoUsuario
        };

        try {
            const response = await fetch(`${CONFIG.API_URL}/api/transacoes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(transacaoPayload)
            });

            if (response.ok) {
                showAlert('Transferência efetuada com sucesso!', 'success');
                formTransferencia.reset();
                await carregarDadosUsuario();
            } else {
                const erro = await response.json();
                showAlert(`Erro: ${erro.mensagem || 'Falha ao processar transferência.'}`, 'error');
            }
        } catch (error) {
            console.error('Erro no processo de transação:', error);
            showAlert('Erro de comunicação de rede.', 'error');
        }
    });
});