# Sistema Hospitalar

Sistema de informação para gestão de um hospital de médio porte, desenvolvido como Trabalho Prático da disciplina **Programação Modular** do curso de **Engenharia de Software** da PUC Minas.

O sistema centraliza o cadastro de pacientes, profissionais de saúde e quartos, além do controle de consultas, internações e do histórico de atendimentos de cada paciente.

## Integrantes

- Julia Rodrigues da Silva
- Giovanna Marques Freire Barbosa
- Nubia Torres de Oliveira
- Pedro Miguel Souza Dias

## Sprint 1

Esta sprint contempla a modelagem inicial e o front-end **sem funcionalidade**.

| Entrega | Descrição |
|---|---|
| Front-end | Telas em HTML e CSS puros, sem framework e sem JavaScript |
| Diagrama de Classes UML | [UML](doc/UML-TP1-PM.png) |
| Cartões CRC | [Responsabilidades e colaborações de cada classe](doc/Cartoes_CRC_Sistema_Hospitalar.pdf) |
| Wireframe | [Protótipo no Figma](https://www.figma.com/design/lbml8jFXtkNnZKFsCsCHJQ) (também em [Wireframe PDF](doc/Wireframe_Sprint1.pdf)) |

### Convenções

- Cada módulo tem sua própria pasta em `pages/`, com `index.html` para a listagem e as demais telas ao lado.
- Estilos reutilizáveis ficam em `css/style.css`. Estilos usados por uma única tela ficam na pasta dessa tela.
- Nenhum HTML usa `style` inline.
- Ícones e imagens ficam em `assets/`.

## Como executar o front-end

Não é necessário instalar nada.

1. Clone o repositório.
2. Abra o arquivo `src/index.html` no navegador.

Opcionalmente, use a extensão **Live Server** do VS Code para recarregar as páginas automaticamente.

## Decisões técnicas

- **Sem framework e sem JavaScript.** Os comportamentos desta sprint foram feitos só com CSS: o menu lateral que abre e fecha usa um checkbox, e a troca entre Consulta e Internação usa radio buttons.
- **Responsivo.** As telas se adaptam ao celular, e o menu lateral passa para o topo da página.
- **Fonte.** Public Sans, carregada pelo Google Fonts.

## Modelagem

As principais classes do sistema são:

- **Pessoa:** superclasse de `Paciente` e `Profissional`.
- **Atendimento:** superclasse de `Consulta` e `Internacao`.
- **Historico:** registra os atendimentos de cada paciente.
- **Quarto:** controla capacidade e situação (`DISPONIVEL` ou `OCUPADO`).

### Regras de negócio

1. Um paciente pode ter diversas consultas e internações ao longo do tempo.
2. Consultas são associadas a um profissional responsável e a um paciente.
3. Um profissional não pode ter dois atendimentos agendados no mesmo horário.
4. Uma internação é associada a um paciente e a um quarto disponível.
5. Um quarto não pode ultrapassar sua capacidade máxima.
6. O sistema mantém o histórico de consultas e internações dos pacientes.

## Sprint 2

Classes de domínio, regras de negócio e API REST com Spring Boot, na pasta [`api/`](api).

| Entrega | Onde está |
|---|---|
| 1. Diagrama de Classes atualizado | [UML](doc/UML-TP1-PM.png) |
| 2. Classes de domínio | [`model`](api/src/main/java/br/pucminas/hospital/model) |
| 3. Endpoints | [`controller`](api/src/main/java/br/pucminas/hospital/controller) |
| 4. Regras de negócio | Nas classes de `model` e nos `service` |

### Como executar a API

O build é feito pelo VS Code. Requer o JDK 17 ou mais novo.

1. Instale no VS Code a extensão **Extension Pack for Java** (da Microsoft).
2. Abra a pasta `api` em **File → Open Folder** e espere a extensão carregar o projeto Maven e baixar as dependências.
3. Abra `src/main/java/br/pucminas/hospital/SistemaHospitalarApplication.java` e clique em **Run** acima do método `main`.

A API sobe em `http://localhost:8080`. O banco desta sprint é o H2 em memória (console em `/h2-console`, JDBC URL `jdbc:h2:mem:hospital`, usuário `sa`).

### Estrutura

```
api/src/main/java/br/pucminas/hospital
├── controller   → endpoints REST
├── service      → casos de uso
├── repository   → acesso aos dados (Spring Data JPA)
└── model        → classes do diagrama e enums
```

### Endpoints

| Recurso | Operações |
|---|---|
| `/pacientes` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `GET /{id}/historico` |
| `/profissionais` | `GET` (filtros `?especialidade=` e, para ver quem está livre, `&data=&horario=`), `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `GET /{id}/disponibilidade?data=&horario=`, `GET` e `POST /{id}/procedimentos`, `PATCH /{id}/procedimentos/{procedimentoId}/iniciar`, `/concluir`, `/cancelar` |
| `/consultas` | `GET` (filtro `?status=`), `GET /{id}`, `POST`, `PATCH /{id}/confirmar`, `/iniciar`, `/observacoes`, `/concluir`, `/cancelar`, `/nao-compareceu`, `/encaminhar?especialidade=`; emergências em `GET` e `POST /emergencias`, `PATCH /emergencias/{id}/iniciar`, `/concluir` |
| `/internacoes` | `GET` (filtro `?ativas=`), `GET /{id}`, `POST`, `PATCH /{id}/alta` |
| `/quartos` | `GET` (filtro `?situacao=`), `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |

No `POST /profissionais`, o campo `categoria` define o tipo: `CLINICO_GERAL`, `ESPECIALISTA` (com `especialidade`) ou `CIRURGIAO` (com `areaCirurgica`). No `POST /consultas`, o campo `tipo` é `ROTINA` ou `ESPECIALIZADA`. Violações de regra retornam `400` com a mensagem do motivo; registros inexistentes retornam `404`.

### Regras de negócio

**Categorias de profissionais**

1. Consulta de rotina só com clínico geral; consulta especializada só com especialista da mesma área; procedimento cirúrgico só com cirurgião.
2. O clínico geral encaminha o paciente que está em consulta de rotina com ele para uma especialidade.
3. Profissionais podem ser consultados por área de atuação.

**Modalidades de atendimento**

4. Cada modalidade sobrescreve `validarCondicoes()` com suas próprias verificações, aproveitando as comuns de `Atendimento`.
5. Um profissional não pode ter dois atendimentos no mesmo horário (consultas canceladas ou sem comparecimento e procedimentos cancelados liberam o horário).
6. Não é possível agendar consultas ou procedimentos no passado. Um procedimento que já aconteceu é registrado com a data e hora efetivas e entra direto como `CONCLUIDO`.
7. Emergências exigem prioridade (`BAIXA`, `MODERADA`, `ALTA`, `CRITICA`), seguem `AGUARDANDO_AVALIACAO → EM_ATENDIMENTO → CONCLUIDO` e são listadas da maior para a menor prioridade.

**Situação das consultas**

8. Transições permitidas: `AGENDADA → CONFIRMADA → EM_ATENDIMENTO → CONCLUIDA`, e `AGENDADA`/`CONFIRMADA → CANCELADA` ou `NAO_COMPARECEU`. Qualquer outra é recusada; uma consulta cancelada só volta a acontecer com um novo agendamento.
9. Observações só podem ser registradas durante ou depois da consulta.

**Procedimentos cirúrgicos**

10. Situações: `AGENDADO → EM_ANDAMENTO → CONCLUIDO`, ou `AGENDADO → CANCELADO`. Não é possível concluir um procedimento que não foi iniciado. Ao iniciar, a data e hora efetivas são registradas.

**Tipos de quarto e internações**

11. Leitos por tipo: `ENFERMARIA` de 2 a 6, `APARTAMENTO` de 1 a 2, `UTI` 1.
12. A internação só ocorre em quarto com vaga; a situação do quarto é atualizada a cada entrada e alta.
13. Um paciente não pode ter duas internações ativas, e as datas de alta não podem ser anteriores à entrada.

## Próximas sprints

- Persistência em banco de dados relacional (MySQL/PostgreSQL)
- Ampliação dos testes automatizados
- Integração do front-end com a API
