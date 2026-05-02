let currentType = 'aluno';

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

    // Lógica de troca
    if (type === 'aluno') {
        inputNome.placeholder = "Nome Completo";
        inputDoc.placeholder = "CPF";
        groupVinculo.style.display = "block"; // Aluno precisa escolher instituição
    } else if (type === 'empresa') {
        inputNome.placeholder = "Nome da Empresa";
        inputDoc.placeholder = "CNPJ";
        groupVinculo.style.display = "none";
    } else if (type === 'instituicao') {
        inputNome.placeholder = "Nome da Instituição";
        inputDoc.placeholder = "CNPJ / Registro MEC";
        groupVinculo.style.display = "none";
    }
}