# Integração com HubSpot API - Desafio Técnico 🚀

Este projeto é uma API REST desenvolvida em Java com `Spring Boot` que realiza integração com a API do HubSpot. Implementamos o fluxo de `authorization code` do OAuth 2.0 e endpoints para criação/consulta de contatos, bem como recebimento de notificações via webhooks.

## 📋 Funcionalidades
1. **Geração da URL de autorização OAuth**: Inicia o processo de login com o HubSpot.
2. **Processamento do callback OAuth**: Recupera o token de acesso após a autenticação.
3. **Criação de contatos no HubSpot**: Endpoint para enviar dados dos contatos.
4. **Webhook**: Recebe notificações quando contatos são criados no HubSpot.
5. **Rate Limiting**: Limitação de requisições por cliente implementada usando Bucket4J.

## 🚀 Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot**
- **Bucket4J** (Controle de rate limiting)
- **RestTemplate** (Chamadas HTTP)
- **Jakarta Validation** (Validação de dados)
- **Maven** (Gerenciador de dependências)
- **Docker** (Containerização - Opcional)

---

## ⚙️ Configuração e Execução

Este projeto pode ser executado de duas formas:
- **1. Tradicional**: Baixando e configurando localmente (Maven + Java).
- **2. Usando Docker** (opcional): Para facilitar a execução e evitar dependências manuais.

---

### 1. Fluxo Tradicional (Execução Local)

#### 1.1 Pré-Requisitos
- Java 17 ou superior
- Maven
- Conta no HubSpot ([criar aqui](https://app.hubspot.com/login))

#### 1.2 Etapas de Execução
1. Clone o repositório:
   ```bash
   git clone https://github.com/marcosluan00/case-tecnico-meetime.git
   cd case-tecnico-meetime
   ```

2. Configure os dados necessários no arquivo `application.properties`:
   ```properties
   hubspot.client.id=SEU_CLIENT_ID
   hubspot.client.secret=SEU_CLIENT_SECRET
   hubspot.redirect.uri=http://localhost:8080/oauth/callback
   hubspot.scope=oauth crm.objects.contacts.read crm.objects.contacts.write
   ```
   > Substitua `SEU_CLIENT_ID` e `SEU_CLIENT_SECRET` pelas chaves da sua conta HubSpot - Deixei minhas credenciais de propósito.  
   > Ajuste o `hubspot.scope` caso necessário, mas as permissões listadas já atendem os requisitos básicos.

3. Compile o projeto para garantir que está tudo correto:
   ```bash
   ./mvnw clean install
   ```

4. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

5. A aplicação estará disponível em:  
   **`http://localhost:8080`**

---

### 2. Usando Docker (Opcional)

Se preferir, você pode executar a aplicação usando Docker para evitar configurações adicionais no ambiente local.

#### 2.1 Pré-Requisitos:
- Docker instalado ([guia de instalação](https://docs.docker.com/get-docker/))

#### 2.2 Etapas de Execução com Docker

1. Clone o repositório:
   ```bash
   git clone https://github.com/marcosluan00/case-tecnico-meetime.git
   cd case-tecnico-meetime
   ```

2. Compile o projeto para gerar o arquivo JAR:
   ```bash
   ./mvnw clean package
   ```

3. Construa a imagem Docker:
   ```bash
   docker build -t integracao-hubspot:1.0 .
   ```

4. Execute o contêiner Docker:
   ```bash
   docker run -p 8080:8080 --name integracao-hubspot integracao-hubspot:1.0
   ```

5. A aplicação estará disponível em:  
   **`http://localhost:8080`**

---

## 🛰️ Endpoints Disponíveis

### **Autenticação & Token - OAuth**
1. **Gerar URL de autorização**  
   **GET** `/oauth/authorize`
   - **Descrição**: Gera o link de login no HubSpot.
   - **Exemplo**: Faça a requisição para este endpoint e clique no link gerado para autorizar.

2. **Callback OAuth**  
   **GET** `/oauth/callback?code=<authorization_code>`
   - **Descrição**: Processa o código de autorização e retorna o token de acesso.

---

### **Contatos**
1. **Criar Contato**  
   **POST** `/contacts`
   - **Body do Request**:
     ```json
     {
       "firstName": "João",
       "lastName": "Silva",
       "email": "joao.silva@email.com"
     }
     ```
   - **Descrição**: Cria o contato no HubSpot.

2. - **Listar Contatos**  
     **GET** `/contacts`
- **Descrição**: Retorna todos os contatos armazenados no HubSpot.

---

### **Webhook**
- **Endpoint**: **POST** `/contacts/webhook`
   - Este endpoint recebe notificações de criação de contatos diretamente do HubSpot.
   - Logs dos eventos recebidos podem ser visualizados no console.

---

## 🔍 Melhorias Futuras
1. Persistência de tokens em cache ou banco de dados.
2. Tratativa para o webhook.
3. Melhoria na segurança, como suporte a HTTPS nativo.

---
## 🛠️ Justificativa do Uso de Bibliotecas

Este projeto priorizou o uso de bibliotecas já integradas ao Spring Boot para simplificar a implementação e garantir robustez. A seguir, apresentamos as principais bibliotecas utilizadas e o motivo da escolha:

1. **Spring Boot Starter Web**
   - Justificativa: Biblioteca essencial para construir APIs REST de forma rápida e eficiente. Simplifica toda a configuração de servidores HTTP, roteamentos e integrações com endpoints.
   - **Uso no projeto**:
      - Configuração dos endpoints REST para gerenciamento de contatos e manipulação dos fluxos `GET` e `POST`.

2. **Spring Boot Starter Validation**
   - Justificativa: Fornece suporte para validação de dados em APIs REST, sendo amplamente utilizada junto às anotações de validação do Jakarta Validation (ex.: `@NotNull`, `@Email`, etc.).
   - **Uso no projeto**:
      - Validação de dados de entrada nos endpoints, como o `POST /contacts`, garantindo que informações obrigatórias (ex.: `email`, `firstName`, etc.) sejam corretamente inseridas.

3. **Spring Boot Starter JSON**
   - Justificativa: Responsável por manipular (serializar e desserializar) os objetos JSON de entrada e saída. O Spring Boot usa internamente bibliotecas como Jackson para realizar essa funcionalidade.
   - **Uso no projeto**:
      - Conversão de objetos Java para JSON ao responder aos clientes.
      - Interpretação das respostas recebidas da API do HubSpot.

4. **Bucket4J - Controle de Rate Limiting**
   - Justificativa: Única dependência externa não diretamente fornecida pelo Spring Boot. Foi escolhida por ser uma solução simples, eficiente e de alto desempenho para implementar limites de solicitações (`Rate Limiting`).
   - **Uso no projeto**:
      - Controle de limite de requisições por cliente, protegendo a API contra abuso ou excesso de chamadas, o que é fundamental em integrações com APIs externas como o HubSpot.

5. **Maven**
   - Justificativa: Ferramenta utilizada para gerenciar todas as dependências e a construção da aplicação. Foi escolhida por ser amplamente utilizada em projetos Java e compatível com o Spring Boot.
   - **Uso no projeto**:
      - Compilação, empacotamento da aplicação (`.jar`) e execução.


