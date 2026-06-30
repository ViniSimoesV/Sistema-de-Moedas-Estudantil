# 📘 Relatório de Análise Crítica do Projeto 👨‍💻

## 1. Informações do grupo
- **🎓 Curso:** Engenharia de Software
- **📘 Disciplina:** Laboratório de Desenvolvimento de Software
- **🗓 Período:** 4° Período
- **👨‍🏫 Professor(a):** Prof. Dr. João Paulo Carneiro Aramuni
- **👥 Membros do Grupo:** Isabella Luiza Dias dos Santos, Maria Clara Gomes Silva de Oliveira e Sofia Vasconcelos Moreira e Silva

---

## 📌 2. Identificação do Projeto
- **Nome do projeto:** Sistema de Moeda Estudantil (UniRewards)
- **Integrantes do outro grupo:** Vinícius Simões, Luiz Arthur
- **Link do repositório:** https://github.com/ViniSimoesV/Sistema-de-Moedas-Estudantil
- **Pull requests submetidos pelo seu grupo:**
  
  | 👤 Integrante | 🔧 Refatoração | 🔗 Link do PR |
  |--------------|---------------|----------------|
  | :octocat: Seu Nome Aqui | Centralização de Exceções em Controllers | [Link do seu PR] |
  | :octocat: Seu Nome Aqui | Extract Method no Envio de E-mails | [Link do seu PR] |
  | :octocat: Seu Nome Aqui | Extract Method de Parsing CSV | [Link do seu PR] |

*(**Nota**: Os links acima devem ser atualizados quando você subir os commits e abrir os PRs)*

---

## 🧱 3. Arquitetura e Tecnologias Utilizadas

O projeto utiliza uma arquitetura dividida entre backend e frontend, promovendo modularidade e separação de responsabilidades. Diferente de projetos padrão MVC full-stack monolítico, o sistema usa uma API REST no backend e um frontend consumindo esses serviços.

### 🏗️ Backend — Spring Boot
O backend foi desenvolvido utilizando **Spring Boot 3.x** e **Java 21**, com as seguintes características:
- **Controllers:** recebem requisições HTTP REST (Retornam JSON, não views).
- **Services:** concentram as regras de negócio.
- **Repositories:** utilizam Spring Data JPA para acesso ao PostgreSQL.
- **DTOs:** Utilizados para evitar vazamento de entidades diretamente nas respostas HTTP.
- **RabbitMQ:** Usado de forma assíncrona para envio de e-mails, reduzindo a latência do cliente no resgate e transferência de moedas.

Tecnologias empregadas:
- Spring Boot & Spring Web
- Spring Data JPA
- PostgreSQL
- RabbitMQ (Spring AMQP)
- JavaMailSender

### 🌐 Frontend — Vanilla (HTML, CSS, JS)
Ao contrário de muitos projetos modernos que usam Next.js, React ou Angular, este projeto optou por uma abordagem **Vanilla**:
- Sem framework ou *build tools* (como Webpack, Vite ou Node.js).
- Utiliza **HTML, CSS e JavaScript puros** para construir as views e consumir a API.
- Requer apenas um servidor web estático simples (ex: extensão Live Server do VSCode) para rodar a aplicação web, o que reduz drasticamente a curva de aprendizado inicial e a complexidade de deploy (foi feito o deploy na Vercel).

### 🔄 Integração entre Camadas
- O backend funciona como uma API isolada.
- O frontend consome essas APIs de forma assíncrona utilizando `fetch` no JavaScript.
- O uso de CORS foi habilitado no backend para permitir chamadas do frontend em domínios/portas distintos.

---

## 🗂️ 4. Organização do GitHub e Fluxo de Trabalho Colaborativo

### 4.1. Estrutura do Repositório e Documentação
* **Estrutura de Pastas:** A organização está muito bem delimitada. Há uma pasta `frontend` para o código estático e a estrutura clássica Maven `src/main/java` para o backend. O diretório `documentos` centraliza toda a base de conhecimento (diagramas UML, etc).
* **Documentação Essencial:** O `README.md` é excelente. Ele é visual, contém escudos (badges), descrições claras da arquitetura, imagens de demonstração, explicação de variáveis de ambiente e a estrutura de pastas detalhada.

### 4.2. Gerenciamento de Tarefas e Commits
* A organização dos commits poderia ser padronizada utilizando **Conventional Commits** (ex: `feat:`, `fix:`) para facilitar a geração de changelog.
* A documentação (README) relata os autores com clareza, mas não evidencia de forma direta como as *Issues* e os *Milestones* guiaram os Sprints do projeto.

---

## 🖥️ 5. Dificuldade para Configuração do Ambiente

### 5.1. Requisitos de Linguagem e Ferramentas de Build
* O projeto exige **Java 21**, mas o `pom.xml` está configurado corretamente para o Maven compilar nessa versão. Não houve dificuldades com dependências, pois o Maven resolveu tudo.

### 5.2. Configuração de Persistência e Variáveis de Ambiente
* O `docker-compose.yml` inclui **apenas** o RabbitMQ. Seria interessante incluir também a imagem do **PostgreSQL** para facilitar a inicialização local do banco de dados, sem que o desenvolvedor precisasse ter o Postgres instalado fisicamente.
* O arquivo `application.properties` possui dados hardcoded de um banco em produção/nuvem (Supabase) e de servidor de email (SMTP). O ideal é que as senhas e os endpoints estivessem exclusivamente como variáveis de ambiente `spring.datasource.password=${DB_PASSWORD}`, sem exibir senhas padrão de fallback publicamente.

### 5.3. Soluções Aplicadas
Para rodar a aplicação adequadamente em modo de desenvolvimento local:
1. Iniciamos o RabbitMQ através do comando `docker-compose up -d`.
2. O Maven baixou as dependências e o backend subiu com sucesso apontando para o banco remoto configurado no `.properties`.
3. Para o frontend, foi necessário abrir a pasta `frontend/` com a extensão *Live Server*, já que não há scripts de inicialização (ex: `npm start`).

---

## 🔎 6. Análise de Qualidade do Código e Testes

### 6.1. Design e Princípios SOLID
* **Coesão:** No geral, o projeto adota o padrão Facade e Services corretamente. Entretanto, algumas violações menores de *Single Responsibility Principle (SRP)* foram identificadas. Por exemplo: O `ProfessorService` lia e dividia as linhas de um arquivo CSV, misturando regras de parsing de arquivo com lógica de persistência.
* **Code Smells (Long Method):** O `TransacaoService.processarTransferencia` possuía blocos imensos para orquestrar RabbitMQ e transferências, além de possuir duplicação de lógica no despacho de e-mails para Remetente e Destinatário.

### 6.2. Testabilidade e Cobertura
* O `pom.xml` possui as dependências do `spring-boot-starter-test`, porém, observou-se uma carência de testes automatizados unitários no pacote `src/test/java`, tornando a evolução das regras de negócio (como limite de carteira) mais arriscada a regressões.

### 6.3. Segurança e Tratamento de Erros
* O projeto conta com proteção de rotas com Spring Security e senhas encriptadas com BCrypt, o que é um ponto altíssimo de segurança.
* A API possuía blocos `try-catch` espalhados nos Controllers, mas havia um `GlobalExceptionHandler` que poderia ser melhor reaproveitado.

---

## 🚀 7. Sugestões de Melhorias

1. **Dockerização do PostgreSQL:** Adicionar o serviço do Postgres no `docker-compose.yml` para facilitar testes puramente locais e offline, em conjunto com o RabbitMQ.
2. **Uso Exclusivo de Variáveis de Ambiente:** Limpar chaves e senhas hardcoded do `application.properties` (ex. senhas de e-mail e credenciais SMTP), passando a injetar `Environment Variables` pelo Docker ou IDE.
3. **Padrão de Commits:** Adotar *Conventional Commits* para melhor rastreabilidade.
4. **Implementação de Testes Automatizados:** Cobrir as regras da `Carteira` e do `TransacaoService` com testes unitários usando JUnit 5 e Mockito, visando ao menos 70% de cobertura de código.
5. **Automação (CI):** Incluir um *GitHub Actions* para compilar e testar o projeto a cada Pull Request na branch `main`.

---

## 🔧 8. Refatorações Propostas (3 partes do código)

Abaixo seguem as 3 refatorações propostas. Todas as alterações foram feitas localmente e geraram as seguintes mudanças:

---

### 1️⃣ Refatoração 1 – Remoção de try-catch redundante nos Controllers

**Arquivo:** `src/main/java/br/com/lumens/unirewards/controller/TransacaoController.java`  
**Pull Request:** *[Link do PR]*  

#### 🔴 Antes
```java
    @PostMapping
    public ResponseEntity<?> efetuarTransferencia(@RequestBody TransacaoRequestDTO dto) {
        try {
            transacaoService.processarTransferencia(dto);
            return ResponseEntity.ok(Map.of("mensagem", "Transferência processada com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("mensagem", "Erro crítico ao processar transação no servidor."));
        }
    }
```

#### 🟢 Depois
```java
    @PostMapping
    public ResponseEntity<?> efetuarTransferencia(@RequestBody TransacaoRequestDTO dto) {
        transacaoService.processarTransferencia(dto);
        return ResponseEntity.ok(Map.of("mensagem", "Transferência processada com sucesso!"));
    }
```

#### ✔ Tipo de refatoração aplicada
- **Remove Control Flag / Delegate Exception Handling** (Delegação para o `GlobalExceptionHandler` já existente).

#### 📝 Justificativa
O sistema já conta com a classe `GlobalExceptionHandler` que intercepta `IllegalArgumentException` e devolve um 400 (Bad Request). Espalhar `try-catch` nos controllers gera código poluído, repetitivo e acoplado. Delegar o tratamento de exceções torna o Controller limpo e coeso.

---

### 2️⃣ Refatoração 2 – Extração de Método (Extract Method) para Envio de Emails

**Arquivo:** `src/main/java/br/com/lumens/unirewards/service/TransacaoService.java`  
**Pull Request:** *[Link do PR]*  

#### 🔴 Antes
```java
            // 1. Despacha e-mail para o Aluno (Recebeu)
            EmailTransacaoDTO emailAluno = new EmailTransacaoDTO();
            emailAluno.setEmailDestino(alunoDestinatario.getEmail());
            emailAluno.setNomeDestino(alunoDestinatario.getNome());
            emailAluno.setNomeOutraParte("Prof. " + professorRemetente.getNome());
            emailAluno.setValor(dto.getValor());
            emailAluno.setMotivo(dto.getMotivo());
            emailAluno.setTipo("RECEBIDO");
            publicarEmailTransacao(emailAluno);

            // 2. Despacha e-mail para o Professor (Enviou)
            EmailTransacaoDTO emailProf = new EmailTransacaoDTO();
            emailProf.setEmailDestino(professorRemetente.getEmail());
            emailProf.setNomeDestino(professorRemetente.getNome());
            emailProf.setNomeOutraParte(alunoDestinatario.getNome());
            emailProf.setValor(dto.getValor());
            emailProf.setMotivo(dto.getMotivo());
            emailProf.setTipo("ENVIADO");
            publicarEmailTransacao(emailProf);
```

#### 🟢 Depois
```java
    // O bloco de código original foi substituído por uma única chamada de método (reaproveitada nos dois fluxos)
    dispararEmailsTransacao(alunoDestinatario, professorRemetente, "Prof. " + professorRemetente.getNome(), dto.getValor(), dto.getMotivo());

    // Novo Método Adicionado na classe
    private void dispararEmailsTransacao(UsuarioAcademico destinatario, UsuarioAcademico remetente, String nomeRemetenteExibicao, Double valor, String motivo) {
        EmailTransacaoDTO emailRecebedor = new EmailTransacaoDTO();
        emailRecebedor.setEmailDestino(destinatario.getEmail());
        emailRecebedor.setNomeDestino(destinatario.getNome());
        emailRecebedor.setNomeOutraParte(nomeRemetenteExibicao);
        emailRecebedor.setValor(valor);
        emailRecebedor.setMotivo(motivo);
        emailRecebedor.setTipo("RECEBIDO");
        publicarEmailTransacao(emailRecebedor);

        EmailTransacaoDTO emailRemetente = new EmailTransacaoDTO();
        emailRemetente.setEmailDestino(remetente.getEmail());
        emailRemetente.setNomeDestino(remetente.getNome());
        emailRemetente.setNomeOutraParte(destinatario.getNome());
        emailRemetente.setValor(valor);
        emailRemetente.setMotivo(motivo);
        emailRemetente.setTipo("ENVIADO");
        publicarEmailTransacao(emailRemetente);
    }
```

#### ✔ Tipo de refatoração aplicada
- **Extract Method**  

#### 📝 Justificativa
Reduz a duplicação severa no `TransacaoService`, facilitando a leitura de `processarTransferencia` e evitando a violação do princípio DRY (Don't Repeat Yourself), visto que a mesma lógica existia separadamente nos laços Professor->Aluno e Aluno->Aluno.

---

### 3️⃣ Refatoração 3 – Extração de Parsing (SRP)

**Arquivo:** `src/main/java/br/com/lumens/unirewards/service/ProfessorService.java`  
**Pull Request:** *[Link do PR]*  

#### 🔴 Antes
```java
                // Divide as colunas por vírgula ou ponto-e-vírgula
                String[] colunas = linha.split("[,;]");
                
                if (colunas.length >= 4) {
                    ProfessorDTO dto = new ProfessorDTO();
                    dto.setNome(colunas[0].trim());
                    dto.setCpf(colunas[1].trim());
                    dto.setEmail(colunas[2].trim());
                    dto.setDepartamento(colunas[3].trim());
                    dto.setInstituicaoId(instituicaoId);
                    
                    try {
                        salvos.add(this.salvar(dto));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Linha ignorada (duplicado): " + dto.getNome());
                    }
                }
```

#### 🟢 Depois
```java
                // Divide as colunas por vírgula ou ponto-e-vírgula
                String[] colunas = linha.split("[,;]");
                ProfessorDTO dto = parseLinhaCsvParaDto(colunas, instituicaoId);
                
                if (dto != null) {
                    try {
                        salvos.add(this.salvar(dto));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Linha ignorada (duplicado): " + dto.getNome());
                    }
                }

    // Novo Método Adicionado na classe
    private ProfessorDTO parseLinhaCsvParaDto(String[] colunas, Long instituicaoId) {
        if (colunas.length < 4) return null;
        ProfessorDTO dto = new ProfessorDTO();
        dto.setNome(colunas[0].trim());
        dto.setCpf(colunas[1].trim());
        dto.setEmail(colunas[2].trim());
        dto.setDepartamento(colunas[3].trim());
        dto.setInstituicaoId(instituicaoId);
        return dto;
    }
```

#### ✔ Tipo de refatoração aplicada
- **Extract Method / SRP (Single Responsibility Principle)**

#### 📝 Justificativa
Isolar o parsing (transformação de Array de Strings para DTO) de dentro do laço principal. Mantém o método pai preocupado apenas com iteração de IO e delega a criação estruturada do DTO para outra função menor e focada, melhorando a testabilidade desse bloco.

---

## 9. 📄 Conclusão

A análise crítica permitiu identificar aspectos excelentes no projeto, como o uso de uma arquitetura robusta baseada no **Spring Boot** aliada ao assincronismo do **RabbitMQ**, essencial para fluxos que exigem velocidade. A aplicação adotou um modelo de frontend em **Vanilla JS**, mantendo as coisas simples e garantindo que o backend se comportasse de forma impecável como API REST pura.

As refatorações propostas tiveram impacto direto na **melhoria da legibilidade**, **redução de duplicidade**, **aumento da coesão** e **clareza das responsabilidades**. As mudanças realizadas ajudaram a limpar as classes de serviço (removendo a verbosidade do envio de e-mails) e as classes de acesso à Web (reaproveitando o ControllerAdvice para as Exceptions).

Por fim, o processo reforçou a importância de separar dados sensíveis do arquivo de properties e o uso contínuo de recursos como testes automatizados e dockers completos para a saúde escalável do software.

---
