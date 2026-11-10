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

## Como executar

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

## Próximas sprints

- API REST com Spring Boot, em arquitetura de camadas (Controller, Service, Repository e Model)
- Persistência em banco de dados
- Tratamento de exceções
- Testes automatizados
- Integração do front-end com a API
