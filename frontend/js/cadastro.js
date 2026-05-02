let currentType = 'aluno';
const form = document.getElementById('form-cadastro');

function setFormType(type) {
    currentType = type;
    
    // Atualiza botões
    document.querySelectorAll('.type-btn').forEach(btn => {
        btn.classList.remove('active');

        const btnAction = btn.getAttribute('onclick');
        if(btnAction && btnAction.includes(type)) btn.classList.add('active');
    });

    // Referências dos campos
    const inputNome = document.getElementById('nome');
    const inputDoc = document.getElementById('documento');
    const groupVinculo = document.getElementById('group-vinculo');
    const selectVinculo = document.getElementById('vinculo');

    // Lógica de troca
    if (type === 'aluno') {
        inputNome.placeholder = "Nome Completo";
        inputDoc.placeholder = "CPF";
        groupVinculo.style.display = "block";
        selectVinculo.required = true;
    } else if (type === 'empresa') {
        inputNome.placeholder = "Nome da Empresa";
        inputDoc.placeholder = "CNPJ";
        groupVinculo.style.display = "none";
        selectVinculo.required = false;
    } else if (type === 'instituicao') {
        inputNome.placeholder = "Nome da Instituição";
        inputDoc.placeholder = "CNPJ / Registro MEC";
        groupVinculo.style.display = "none";
    }
};

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const dados = Object.fromEntries(formData.entries());
    
    let endpoint = '';
    let payload = {};

    // Lógica para decidir o DTO e o Endpoint
    if (currentType === 'aluno') {
        endpoint = '/api/alunos';
        payload = {
            nome: dados.nome, 
            email: dados.email,
            senha: dados.senha,
            cpf: dados.documento,
            rg: dados.rg,
            curso: dados.curso,
            instituicaoId: parseInt(dados.instituicao),
            rua: dados.rua,
            numero: parseInt(dados.numero),
            complemento: dados.complemento,
            bairro: dados.bairro,
            cidade: dados.cidade,
            urlFotoPerfil: ""
        };
    } else if (currentType === 'empresa') {
        endpoint = '/api/empresas';
        payload = {
            nome: dados.nome,
            senha: dados.senha,
            cnpj: dados.documento,
            urlFotoPerfil: ""
        };
    }

    try {
        // Use a URL base correta (localhost para dev ou vercel para prod)
        const response = await fetch(`http://localhost:8080${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            // Não tentamos mais dar parse no JSON aqui
            alert(`Cadastro de ${currentType} realizado com sucesso!`);
            window.location.href = 'login.html';
        } else {
            const erro = await response.json();
            alert(`Erro: ${erro.erro || 'Falha no cadastro.'}`);
        }
        window.location.href = 'login.html';
    } catch (error) {
        console.error('Erro na conexão:', error);
        alert('Não foi possível conectar ao servidor.');
    }
});