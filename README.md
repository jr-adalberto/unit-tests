🧪 unit-tests

Bem-vindo ao repositório unit-tests! Este projeto é um ambiente dedicado ao estudo aprofundado e à aplicação prática de testes unitários em Java, utilizando as poderosas bibliotecas JUnit 5 e Mockito.




📋 Índice

•
Visão Geral

•
Funcionalidades

•
Tecnologias Utilizadas

•
Pré-requisitos

•
Como Executar o Projeto

•
Estrutura do Projeto

•
Contribuição

•
Licença

•
Autor




🌟 Visão Geral

Este projeto foi desenvolvido com o propósito de explorar e demonstrar as melhores práticas na escrita de testes unitários robustos e eficientes em Java. Através de exemplos práticos, o objetivo é solidificar o conhecimento em JUnit 5 para a criação de testes e em Mockito para a simulação de dependências, garantindo o isolamento e a confiabilidade dos testes.




🛠 Funcionalidades

•
Testes Unitários com JUnit 5: Exemplos de como escrever testes eficazes para diferentes cenários e componentes de software.

•
Mocking de Dependências com Mockito: Demonstrações de como utilizar mocks, stubs e spies para isolar a unidade de código sob teste, controlando o comportamento de suas dependências.

•
Cobertura de Testes: Foco na importância da cobertura de testes para garantir a qualidade do código.

•
Organização de Testes: Estrutura de projeto que facilita a manutenção e escalabilidade dos testes.




🚀 Tecnologias Utilizadas

•
Linguagem: Java

•
Gerenciador de Dependências: Maven

•
Framework de Testes: JUnit 5

•
Biblioteca de Mocking: Mockito




📋 Pré-requisitos

Antes de executar o projeto, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

•
Java Development Kit (JDK): Versão 8 ou superior (recomenda-se a versão 17 para compatibilidade com as últimas features).

•
Apache Maven: Versão 3.6.3 ou superior.

•
Um ambiente de desenvolvimento integrado (IDE) como IntelliJ IDEA, Eclipse ou VS Code com suporte a Java e Maven.




🚀 Como Executar o Projeto

Siga os passos abaixo para clonar o repositório e executar os testes unitários localmente:

1.
Clone o repositório:

2.
Navegue até o diretório do projeto:

3.
Execute os testes com Maven:




📂 Estrutura do Projeto

A organização do projeto segue a convenção padrão do Maven para projetos Java:

Plain Text


unit-tests/
├── .idea/                 # Arquivos de configuração específicos do IntelliJ IDEA
├── src/
│   ├── main/
│   │   └── java/          # Código-fonte principal da aplicação (classes a serem testadas)
│   │       └── com/example/yourproject/
│   │           └── ...
│   └── test/
│       └── java/          # Código-fonte dos testes unitários
│           └── com/example/yourproject/
│               └── ...
├── .gitignore             # Arquivo que especifica arquivos e diretórios a serem ignorados pelo Git
└── pom.xml                # Arquivo de configuração do Maven, contendo dependências e configurações de build


•
src/main/java: Contém as classes da aplicação que são o foco dos testes unitários.

•
src/test/java: Contém as classes de teste, que utilizam JUnit 5 e Mockito para verificar o comportamento das classes em src/main/java.

•
pom.xml: Gerencia as dependências do projeto, incluindo JUnit 5 e Mockito, e define o ciclo de vida da construção.




🤝 Contribuição

Sua contribuição é muito valorizada! Se você tem ideias para novos exemplos de testes, melhorias no código, correções de bugs ou qualquer outra sugestão, sinta-se à vontade para contribuir. Por favor, siga os passos abaixo:

1.
Faça um fork deste repositório.

2.
Crie uma nova branch para sua feature ou correção (git checkout -b feature/nome-da-feature ou bugfix/nome-da-correcao).

3.
Faça suas alterações e adicione commits claros e descritivos.

4.
Envie suas alterações para o seu fork (git push origin feature/nome-da-feature).

5.
Abra um Pull Request para este repositório, descrevendo suas alterações.




📄 Licença

Este projeto está licenciado sob a licença MIT. Para detalhes completos, consulte o arquivo LICENSE na raiz deste repositório.




👤 Autor

jr-adalberto

