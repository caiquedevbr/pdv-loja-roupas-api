# PROMPT: Auditoria, Implementação e Documentação Completa da API Java Spring Boot

## 1. Objetivo geral

Realize uma análise completa da aplicação existente e implemente todos os requisitos técnicos apresentados abaixo, respeitando integralmente a arquitetura, os padrões de desenvolvimento e as regras de negócio já estabelecidas no projeto.

**O objetivo não é reconstruir a API, mas evoluir a implementação atual, preservando tudo o que já funciona.**

A aplicação deve ficar funcional, organizada, segura, testada e amplamente documentada utilizando Swagger/OpenAPI.

**Diretrizes obrigatórias:**

- Analise todo o código-fonte antes de realizar alterações.
- Identifique quais requisitos já foram implementados, quais estão parcialmente implementados e quais ainda não existem.
- Preserve todas as funcionalidades existentes que estejam corretas.
- Não refatore, renomeie, exclua ou reestruture componentes desnecessariamente.
- Reutilize as entidades, DTOs, services, repositories, controllers, interceptors, exceptions e configurações existentes.
- Siga os padrões de nomenclatura, organização de pacotes, arquitetura e estilo de código do projeto.
- Não crie funcionalidades duplicadas.
- Não altere regras de negócio já implementadas sem necessidade técnica comprovada.
- Preserve a compatibilidade dos endpoints existentes, especialmente da V1.
- Implemente as novas funcionalidades preferencialmente na V2, quando relacionadas aos recursos avançados.
- Não introduza novas dependências sem necessidade.
- Utilize soluções adequadas ao ecossistema Spring Boot.
- Evite implementações temporárias, simuladas ou incompletas.

---

## 2. Etapa obrigatória: auditoria da aplicação

Antes de implementar qualquer funcionalidade, examine:

1. Estrutura de pacotes e arquitetura da aplicação.
2. Configuração do Maven (`pom.xml`).
3. Versão do Java e Spring Boot.
4. Entidades JPA, enums e relacionamentos.
5. Controllers e endpoints REST.
6. Services e regras de negócio.
7. Repositories e consultas.
8. DTOs e validações.
9. Tratamento global de exceções.
10. Configurações de H2 e persistência.
11. Implementação de HATEOAS.
12. Versionamento da API.
13. Autenticação por API Key.
14. Configuração de interceptors e filters.
15. Configuração de CORS.
16. Documentação Swagger/OpenAPI.
17. Testes automatizados.

Após a análise, elabore uma matriz de conformidade:

| Requisito | Situação atual | Ação necessária |
|---|---|---|
| Entidades e relacionamentos | Implementado / Parcial / Ausente | Descrever |
| CRUD | Implementado / Parcial / Ausente | Descrever |
| Paginação | Implementado / Parcial / Ausente | Descrever |
| HATEOAS | Implementado / Parcial / Ausente | Descrever |
| Idempotência | Implementado / Parcial / Ausente | Descrever |
| API Key | Implementado / Parcial / Ausente | Descrever |
| Rate Limiting | Implementado / Parcial / Ausente | Descrever |
| CORS | Implementado / Parcial / Ausente | Descrever |
| Versionamento | Implementado / Parcial / Ausente | Descrever |
| Swagger | Implementado / Parcial / Ausente | Descrever |

**Importante:** Não reimplemente requisitos que já estejam funcionando corretamente. Apenas corrija problemas reais e desenvolva os itens faltantes.

---

# 3. Requisitos técnicos: Parte 1

## 3.1. Estrutura do projeto

A aplicação deverá:

- Utilizar Spring Boot, preferencialmente em versão estável e compatível com o projeto.
- Utilizar Java 17 ou superior.
- Utilizar Maven para gerenciamento de dependências, caso já seja o padrão existente.
- Utilizar H2 para persistência de dados.
- Utilizar Spring Data JPA para o mapeamento objeto-relacional.

Verifique as dependências existentes antes de adicionar ou atualizar qualquer biblioteca.

Não atualize versões apenas por serem mais recentes se isso introduzir incompatibilidades desnecessárias.

## 3.2. Entidades e relacionamentos

A aplicação deve possuir:

- No mínimo cinco entidades JPA.
- Pelo menos um relacionamento `@OneToOne`.
- Pelo menos um relacionamento `@OneToMany`.
- Pelo menos um relacionamento `@ManyToMany`.
- Validações com Jakarta Bean Validation.
- Pelo menos um enum utilizado em uma entidade.
- Relacionamentos consistentes com as regras de negócio.

Verifique inicialmente se esses relacionamentos já existem.

Caso algum esteja ausente, implemente-o de forma coerente com o domínio do sistema PDV, evitando entidades artificiais criadas apenas para cumprir requisitos acadêmicos.

Considere integridade referencial, operações em cascata, carregamento de entidades e prevenção de referências circulares na serialização JSON.

## 3.3. Endpoints REST e operações CRUD

Cada entidade de negócio deverá disponibilizar, no mínimo:

- `POST`: criação.
- `GET`: consulta por identificador.
- `GET`: listagem paginada.
- `PUT`: atualização.
- `DELETE`: exclusão, quando permitida pelas regras de negócio.

Também deverá existir ao menos uma consulta personalizada por entidade.

**Requisitos adicionais:**

- Utilizar `Pageable` em todas as listagens.
- Respeitar o limite máximo de paginação definido na aplicação.
- Implementar filtros e consultas personalizadas utilizando os repositories existentes.
- Preservar as regras de negócio das operações.
- Impedir operações que comprometam a integridade dos dados.

Utilize os códigos HTTP adequados:

- `200 OK`: consulta ou operação concluída.
- `201 Created`: recurso criado.
- `204 No Content`: exclusão sem conteúdo de retorno.
- `400 Bad Request`: dados inválidos.
- `401 Unauthorized`: autenticação ausente ou inválida.
- `403 Forbidden`: acesso não autorizado para o usuário autenticado, quando aplicável.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: conflito de negócio ou idempotência.
- `429 Too Many Requests`: limite de requisições excedido.
- `500 Internal Server Error`: erro interno inesperado.

Não exponha stack traces ou informações sensíveis nas respostas.

## 3.4. HATEOAS

Utilize Spring HATEOAS para garantir a navegabilidade dos recursos.

As respostas deverão possuir links relevantes, conforme a operação e as regras de negócio:

- `self`
- `update`
- `delete`
- links para recursos relacionados.

Utilize as estruturas apropriadas:

- `EntityModel`
- `CollectionModel`
- `PagedModel`

Reutilize os assemblers existentes e evite alterar os contratos de resposta da V1.

---

# 4. Requisitos técnicos: Parte 2

## 4.1. Idempotência

Implemente idempotência nas operações POST apropriadas da V2, principalmente nas operações de venda, para evitar processamento duplicado.

Utilize o header:

`X-Idempotency-Key`

**Comportamento esperado:**

1. O cliente envia uma requisição POST com uma chave única.
2. A API verifica se a chave já foi utilizada.
3. Caso seja uma chave nova, executa a operação e registra o resultado.
4. Caso a mesma chave seja enviada novamente com a mesma requisição, retorna o resultado original sem executar a operação novamente.
5. Caso a mesma chave seja reutilizada com uma requisição diferente, retorna `409 Conflict`.
6. Requisições simultâneas com a mesma chave não podem executar a operação duas vezes.

A implementação deve considerar:

- Persistência das chaves de idempotência.
- Identificação da operação e do cliente.
- Hash ou fingerprint do conteúdo da requisição.
- Armazenamento do código HTTP e da resposta original.
- Estados de processamento.
- Controle de concorrência.
- Transações.
- Validade e eventual expiração das chaves.
- Tratamento de falhas e tentativas repetidas.

**Regra essencial:** Uma venda não pode ser registrada duas vezes nem descontar o estoque novamente quando a mesma operação for reenviada.

Preserve o comportamento da V1.

## 4.2. Autenticação por API Key

Verifique e preserve a autenticação por chave de API já existente no projeto, caso esteja corretamente implementada.

Utilize o header:

`X-API-Key`

A autenticação deverá:

- Permitir emissão de chaves mediante autenticação válida.
- Associar as chaves aos usuários.
- Permitir revogação das chaves.
- Validar as chaves nos endpoints sensíveis da V2.
- Impedir a utilização de chaves inexistentes, inválidas ou revogadas.
- Proteger as credenciais de acesso.

Requisitos de segurança:

- Armazenar senhas com BCrypt.
- Não armazenar chaves de API em texto puro.
- Armazenar somente o hash seguro da chave.
- Retornar a chave original apenas no momento de sua emissão.
- Não registrar chaves ou senhas nos logs.
- Restringir operações sensíveis por meio de autenticação.
- Evitar a exposição de informações sobre usuários internos.

Verifique especialmente os endpoints:

`POST /api/v2/auth/login`

`POST /api/v2/auth/logout`

Preserve suas implementações existentes caso estejam corretas.

Não modifique a proteção da V1 sem necessidade.

## 4.3. Rate Limiting

Implemente limitação de requisições na V2.

A solução deverá:

- Controlar requisições por cliente autenticado e/ou endereço IP.
- Utilizar limites configuráveis.
- Garantir controle consistente de requisições.
- Retornar `429 Too Many Requests` quando o limite for excedido.
- Informar o tempo necessário para uma nova tentativa.

Documente e utilize os headers pertinentes:

- `X-RateLimit-Limit`
- `X-RateLimit-Remaining`
- `X-RateLimit-Reset`
- `Retry-After`

Considere o comportamento dos filtros e interceptors existentes.

Evite soluções que dependam exclusivamente de estruturas em memória quando isso prejudicar os requisitos de persistência ou concorrência.

Documente as limitações da estratégia escolhida, especialmente em ambientes com múltiplas instâncias.

## 4.4. CORS

Verifique se já existe configuração de CORS.

Caso seja necessário implementar ou corrigir:

- Permita apenas origens configuradas.
- Utilize propriedades externas para definir as origens.
- Configure os métodos HTTP permitidos.
- Configure os headers permitidos, incluindo os headers personalizados da API.
- Configure adequadamente as requisições preflight `OPTIONS`.
- Evite utilizar `*` indiscriminadamente.
- Garanta compatibilidade com aplicações front-end.

A configuração não deve comprometer a autenticação ou as regras de acesso.

## 4.5. Versionamento da API

Preserve o versionamento já existente.

A API deverá demonstrar pelo menos duas versões de um endpoint.

Utilize o header personalizado:

`X-API-Version`

Valores esperados:

- Versão 1: `X-API-Version: 1`, quando aplicável ao contrato existente.
- Versão 2: `X-API-Version: 2`.

Caso a API já utilize caminhos como `/api/v2/**`, preserve essa estrutura e sua compatibilidade, garantindo que a validação do header seja consistente com o mecanismo atual.

Não crie uma terceira estratégia de versionamento desnecessariamente.

**Regras:**

- Preserve os endpoints existentes da V1.
- Mantenha os recursos avançados na V2.
- Não altere contratos antigos sem necessidade.
- Responda claramente quando a versão solicitada for inválida ou incompatível.
- Documente as diferenças entre as versões.

---

# 5. Validações e tratamento global de exceções

Utilize Bean Validation nos dados de entrada e preserve as validações existentes.

Verifique e reutilize o tratamento global de exceções com `@RestControllerAdvice` ou `@ControllerAdvice`.

A aplicação deverá tratar:

- Campos obrigatórios ausentes.
- Valores inválidos.
- Recursos não encontrados.
- Dados duplicados.
- Conflitos de regras de negócio.
- Erros de autenticação.
- Chaves de API inválidas.
- Chaves de idempotência em conflito.
- Excesso de requisições.
- Problemas de integridade referencial.
- Erros inesperados.

Padronize as respostas de erro respeitando o contrato atual.

Sempre que possível, inclua:

- Código HTTP.
- Mensagem clara.
- Identificação do erro.
- Timestamp.
- Endpoint solicitado.
- Detalhes de validação, quando aplicável.

Não crie novos exception handlers quando já existir uma solução adequada no projeto.

---

# 6. Documentação Swagger/OpenAPI

**Esta é uma das prioridades mais importantes da implementação.**

A API deverá possuir uma documentação completa, profissional, clara e fácil de entender por alguém que nunca utilizou o projeto.

Utilize Springdoc OpenAPI, respeitando a configuração existente.

## 6.1. Cabeçalho principal do Swagger

Melhore a descrição geral apresentada no topo do Swagger UI.

Ela deverá conter:

### Apresentação da API
- Nome e finalidade do sistema PDV.
- Objetivo da aplicação.
- Principais funcionalidades disponíveis.
- Tecnologias utilizadas.

### Guia de utilização
- Como iniciar a aplicação.
- Como acessar o Swagger.
- Como realizar a primeira requisição.
- Como cadastrar e consultar recursos.
- Como utilizar paginação, ordenação e filtros.
- Como interpretar as respostas da API.

### Versionamento
- Diferenças entre V1 e V2.
- Como utilizar o header `X-API-Version`.
- Exemplos de requisições para cada versão.

### Autenticação
- Como realizar login.
- Como obter uma API Key.
- Como informar `X-API-Key` no Swagger.
- Quais endpoints exigem autenticação.
- Como revogar uma chave.

### Idempotência
- O que significa idempotência.
- Quando utilizar `X-Idempotency-Key`.
- Como gerar uma chave única.
- O que acontece quando uma requisição é repetida.
- Exemplos de sucesso e conflito.

### Rate Limiting
- Limites de requisição configurados.
- Significado dos headers de limite.
- Como interpretar `429`.
- Como utilizar `Retry-After`.

### Códigos de resposta
Explique os principais códigos HTTP retornados pela aplicação.

**Objetivo:** O usuário deve conseguir entender como utilizar a API apenas lendo o Swagger.

## 6.2. Documentação individual dos endpoints

Todos os endpoints devem conter:

- `@Operation` com resumo claro.
- Descrição detalhada da operação.
- `@ApiResponses` com os códigos possíveis.
- Exemplos de sucesso.
- Exemplos de erro.
- Documentação dos parâmetros de entrada.
- Documentação dos headers obrigatórios.
- Descrição dos campos do request body.
- Descrição dos campos do response body.
- Indicação de autenticação quando necessária.
- Indicação de regras de negócio importantes.

Utilize `@Schema`, `@Parameter`, `@ExampleObject` e demais anotações adequadas quando necessário.

Evite descrições genéricas como "Cria recurso" ou "Busca dados".

As descrições devem explicar efetivamente o comportamento das operações.

## 6.3. Organização visual do Swagger

Organize os endpoints por grupos funcionais, utilizando tags adequadas, por exemplo:

- Autenticação
- Clientes
- Categorias
- Produtos
- Estoque
- Vendas
- Outros recursos existentes

Caso a aplicação já possua uma estrutura de tags adequada, preserve-a.

Configure corretamente os esquemas de segurança para permitir o uso do botão **Authorize** nos endpoints que exigirem API Key.

Documente também os headers personalizados e suas obrigatoriedades, sem apresentar como obrigatórios os headers que não são exigidos pela operação.

Mantenha o contrato OpenAPI coerente com o comportamento real da aplicação.

---

# 7. Testes e garantia de qualidade

Implemente ou complemente os testes necessários.

Priorize testes que comprovem:

1. Funcionamento das operações CRUD.
2. Paginação e filtros.
3. Validações Bean Validation.
4. Regras de negócio existentes.
5. Persistência e relacionamentos JPA.
6. Navegação HATEOAS.
7. Autenticação válida e inválida.
8. Revogação de API Key.
9. Controle de idempotência.
10. Requisições simultâneas com a mesma chave de idempotência.
11. Comportamento correto do estoque nas vendas.
12. Rate Limiting e resposta HTTP 429.
13. Headers de versionamento.
14. Configuração CORS.
15. Tratamento de erros.
16. Compatibilidade dos endpoints da V1.

Utilize as bibliotecas e padrões de testes já existentes na aplicação.

Execute os testes e verifique a compilação do projeto.

Não declare que uma funcionalidade foi validada sem realmente executar os testes correspondentes.

---

# 8. Documentação de utilização da API

Além do Swagger, crie ou atualize a documentação do projeto, preferencialmente no `README.md`, sem remover informações relevantes.

Inclua:

- Visão geral da aplicação.
- Tecnologias utilizadas.
- Pré-requisitos.
- Como instalar dependências.
- Como configurar o banco H2.
- Como executar a aplicação.
- Como acessar o Swagger.
- Como utilizar cada versão da API.
- Como realizar autenticação.
- Como utilizar uma API Key.
- Como gerar e utilizar chaves de idempotência.
- Como funcionam os limites de requisição.
- Exemplos de requisições e respostas.
- Principais códigos HTTP.
- Descrição resumida das regras de negócio.
- Como executar os testes.

Forneça exemplos práticos de requisições utilizando cURL, com headers e JSONs compatíveis com os endpoints reais da aplicação.

Todos os exemplos deverão ser executáveis, sem depender de endpoints ou campos inexistentes.

---

# 9. Padrões de desenvolvimento obrigatórios

Durante a implementação:

- Siga a arquitetura atual da aplicação.
- Preserve a separação de responsabilidades.
- Evite regras de negócio nos controllers.
- Utilize services para a lógica de negócio.
- Utilize repositories para acesso a dados.
- Reutilize DTOs existentes.
- Utilize transações quando necessário.
- Evite consultas desnecessárias ao banco.
- Evite código duplicado.
- Preserve os padrões existentes de tratamento de erros.
- Não utilize valores sensíveis fixos no código.
- Utilize configurações externas quando apropriado.
- Não quebre a compatibilidade dos consumidores existentes.
- Não deixe código de exemplo ou implementações fictícias em produção.
- Não altere funcionalidades fora do escopo sem justificativa.

Caso seja necessário criar novas classes, siga os padrões de organização e nomenclatura já utilizados no projeto.

---

# 10. Ordem de execução

Siga obrigatoriamente esta sequência:

**Etapa 1: Diagnóstico**

Analise a aplicação e identifique o que já existe e o que falta.

**Etapa 2: Planejamento**

Defina as mudanças necessárias, os arquivos envolvidos e os possíveis impactos.

**Etapa 3: Implementação**

Implemente apenas os requisitos ausentes ou incompletos, preservando o código correto.

**Etapa 4: Integração**

Garanta que as novas funcionalidades funcionem com as existentes, especialmente autenticação, idempotência, versionamento e tratamento de exceções.

**Etapa 5: Swagger**

Documente todas as funcionalidades implementadas e revise a documentação existente, incluindo o cabeçalho principal.

**Etapa 6: Testes**

Execute os testes automatizados, verifique a compilação e corrija os problemas encontrados.

**Etapa 7: Documentação final**

Atualize o README e forneça exemplos completos de utilização.

**Etapa 8: Relatório de implementação**

Ao finalizar, apresente um relatório contendo:

- Funcionalidades que já existiam e foram preservadas.
- Funcionalidades novas implementadas.
- Funcionalidades corrigidas ou complementadas.
- Classes e arquivos criados.
- Classes e arquivos modificados.
- Endpoints adicionados ou alterados.
- Dependências adicionadas.
- Testes executados e respectivos resultados.
- Eventuais limitações ou pendências.
- Orientações de utilização da API.

---

# 11. Critérios de aceitação

A implementação será considerada concluída quando:

- Todos os requisitos obrigatórios das Partes 1 e 2 estiverem atendidos.
- As funcionalidades existentes continuarem funcionando.
- A V1 permanecer compatível com seu contrato anterior.
- A V2 incorporar os recursos avançados necessários.
- A autenticação por API Key funcionar corretamente.
- A idempotência impedir operações duplicadas.
- O Rate Limiting retornar as respostas e os headers adequados.
- O CORS estiver configurado corretamente.
- O versionamento estiver funcional e documentado.
- As entidades e relacionamentos estiverem corretos.
- Os endpoints possuírem validações e tratamento de erros.
- O HATEOAS permitir navegação entre recursos.
- O Swagger documentar corretamente todas as operações, headers, exemplos e respostas.
- O README explicar como instalar, executar e utilizar a aplicação.
- A aplicação compilar e os testes relevantes passarem.

**Orientação final:**

Não recrie a aplicação do zero e não substitua implementações corretas por soluções diferentes apenas por preferência técnica.

Sua prioridade é realizar uma evolução incremental, consistente e segura do projeto existente.

Faça as alterações diretamente no código, quando houver acesso ao repositório, e não apenas forneça sugestões de implementação.

Não invente funcionalidades, resultados de testes ou endpoints. Documente exclusivamente o comportamento real da aplicação.

**O resultado esperado é uma API Spring Boot completa, funcional, segura, bem estruturada e com documentação Swagger de qualidade profissional, pronta para demonstração e avaliação acadêmica.**

Como você já está trabalhando com V1 e V2, vale estabelecer uma separação clara de responsabilidades:

| Funcionalidade         | V1                           | V2                            |
| ---------------------- | ---------------------------- | ----------------------------- |
| CRUD das entidades     | Preservar                    | Manter conforme implementação |
| Validações             | Preservar                    | Preservar/complementar        |
| HATEOAS                | Preservar                    | Preservar                     |
| Tratamento de exceções | Preservar                    | Complementar                  |
| API Key                | Sem alterar a proteção atual | Validar e aperfeiçoar         |
| Idempotência           | Sem alteração                | Implementar                   |
| Rate Limiting          | Sem alteração                | Implementar                   |
| Versionamento          | Compatibilidade              | Validar header                |
| Swagger                | Documentar                   | Documentação detalhada        |