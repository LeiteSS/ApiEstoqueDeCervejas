# Diario de Bordo: API REST de Gerenciamento Estoques de Cerveja

**Objetivos:** Desenvolver uma **API** usando a metodologia ***Test Driven Development*** - **TDD**. Codificar esta **API** usando a linguagem de programação **Java** na versão 11. Realizar simulações de cadastro, procura, listagem e exclusão dos dados usando o programa **Postman**. Documentar todos os procedimentos. 

**Ferramentas Usadas:** **IntelliJ**, **Postman**, **Typora**, **Mozilla Firefox**, **Terminal do Debian**. 

---

16 de março de 2020 - 14:00 

- Gerar a estrutura do projeto ***Spring Boot*** usando site [Spring Initializr](https://start.spring.io/).

  - Selecionar: ***Maven Project***, **Java**; e adicionar as dependencias **Lombok**, **Spring Web**, **Spring Data JPA**, **Validation**, **H2 Database**, porém ainda irá faltar as dependência: **mapstruct**, **springfox swagger2**, **springfox swagger ui**.  

  ![](/home/silassl/Documents/workspace/EstoqueDeCervejas/img/Screenshot from 2021-03-16 13-47-07.png)
  - Adicionar manualmente as dependências restantes no documento **pom.xml**.

  ```xml
  		<dependency>
  			<groupId>org.mapstruct</groupId>
  			<artifactId>mapstruct</artifactId>
  			<version>1.3.1.Final</version>
  		</dependency>
  		<dependency>
  			<groupId>io.springfox</groupId>
  			<artifactId>springfox-swagger2</artifactId>
  			<version>2.9.2</version>
  		</dependency>
  		<dependency>
  			<groupId>io.springfox</groupId>
  			<artifactId>springfox-swagger-ui</artifactId>
  			<version>2.9.2</version>
  		</dependency>
  ```
  - Adicionar também o seguinte trecho no documento **pom.xml**.

  ```xml
  			<plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <configuration>
                      <source>${java.version}</source>
                      <target>${java.version}</target>
                      <annotationProcessorPaths>
                          <path>
                              <groupId>org.projectlombok</groupId>
                              <artifactId>lombok</artifactId>
                              <version>${lombok.version}</version>
                          </path>
                          <path>
                              <groupId>org.mapstruct</groupId>
                              <artifactId>mapstruct-processor</artifactId>
                              <version>1.3.1.Final</version>
                          </path>
                      </annotationProcessorPaths>
                  </configuration>
              </plugin>
  ```

  ---

  16 de março de 2020 - 14:19

- Dentro do pacote **EstoqueDeCervejas** que está no pacote **main** criar os seguintes pacotes: **config**, **controller**,**dto**, **entity**, **enums**, **exception**, **mapper**, **repository** e **service**. 

- Dentro do pacote **EstoqueDeCervejas**, agora no pacote **test**, criar os seguintes pacotes: **builder**, **controller** e **service**.

  ---

  16 de março de 2020 - 14:28 

- Dentro pacote **resources**, que está no pacote **main** será escrito as seguintes linhas:

  ```properties
  spring.datasource.url=jdbc:h2:mem:beerstock;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  ```

  ---

  16 de março de 2020 - 14:33
  
- Dentro pacote antes criado chamado **config** será escrito um métodos para construir a **API** com as seguintes caracteristicas: `BASE_PACKAGE`, `API_TITLE`, `API_DESCRIPTION`, `CONTACT_NAME`, `CONTACT_GITHUB` e `CONTACT_EMAIL` nesta classe terá também um método que usa a classe `Docket` como tipo. **Ainda não sei como esse método trabalha e conheço o Docket apenas de "vista"**. 

  ---

  16 de março de 2020 - 14:45

- Dentro do pacote **entity** será criado uma classe `Cerveja`  com os atributo: **id**, **nome**, **marca**, **max**, **quantidade** e um `enum` que conterá os tipos de cervejas. Esta é a entidade e espelho do banco de dados.

- A anotação `@Data` serve para gerar os `getters ` e `setter` automaticamente.

- Dentro do pacote **enum** criar a classe `TipoDaCerveja`, que como já foi dito é um `enum` com os seguintes tipos: **LAGER**, **MALZBIER**, **WITBIER**, **WEISS**, **ALE**, **IPA** e **STOUT**. Cada tipo segue com a sua descrição, que será a `String` que será salva no Banco de Dados.

  ---
  
  16 de março de 2020 - 15:05
  
- Dentro do pacote **dto** será criado a classe `CervejaDTO`, onde possuirá os mesmo atributos da entidade `Cerveja`.

  > ***Data Transfer Object*** (DTO) ou simplesmente ***Transfer Object\*** é um padrão de projetos bastante usado em Java para o transporte de  dados entre diferentes componentes de um sistema, diferentes instâncias  ou processos de um sistema distribuído ou diferentes sistemas via  serialização.
  >
  > A ideia consiste basicamente em agrupar um conjunto de atributos numa classe simples de forma a otimizar a comunicação. fonte: [***Stackoverflow***](https://pt.stackoverflow.com/questions/31362/o-que-%C3%A9-um-dto)

---

16 de março de 2020 - 15:11

- Dentro do pacote **repository** será criado a classe `CervejaRepository`, no qual estende a classe `JpaRepository`, do qual irá permitir que encontramos as cervejas pelos seus nomes.

---
16 de março de 2020 - 15:11

- Dentro do pacote **service** iremos criar a classe `CervejaService`, no qual possuirá os métodos de registro, procura, exclusão, etc. 

- Dentro do pacote **exception** criar a classe com a exceção que será lançada chamada `JaExisteException`.

- Dentro do pacote **controller** será criado duas classes: `CervejaController` e `CervejaControllerDocs`,  que será uma interface. Nessa classes ***controllers*** haverá os mesmo metodos da classe ***service***.

- Na `RestController`, como é chamada a classe ***controller***, é uma boa prática mapea-la da seguinte forma: **"/api/v1/[nome da api]"**. 

---
16 de março de 2020 - 15:46

- Como está sendo usado a metodologia **TDD**, inicialmente adicionaremos o teste no método `registrarCerveja()`, caso dê errado será feito ele dar certo e só então o codigo será refatorado.

- Dentro do pacote **builder**, criada dentro do pacote de testes, será criado a classe `CervejaDTOBuilder`, do qual receberá valores para ser testada o método `registrarCerveja()`.

- Dentro do pacote **controller** será criada a classe `CervejaControllerTest`.

- Dentro do pacote **service** será criada a classe `CervejaServiceTest`. 
---
16 de março de 2020 - 16:24

- Teste Realizados, somente um falhou: `quandoUmaNovaCervejaForInformadaDeveraSerCriada()`, talvez porque já deveria sido registrado a mesma cerveja na classe `CervejaControllerTest`.
- Contudo, ao usar o comando `mvn spring-boot:run` a `build` falhou. Antes estava dando este mesmo problema (Este é o segundo projeto que estou desenvolvendo, dessa vez estou documentando um passo-a-passo), até mesmo usando o projeto do instrutor esta falhando a `build` do projeto. Contudo, pelo menos agora os teste rodam no **IDE**.  
- Usando o terminal do IntelliJ, commitar o que fiz até agora e este **README.md** para ficar como ***backup***.