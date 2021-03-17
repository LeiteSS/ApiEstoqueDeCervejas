# Diario de Bordo: API REST de Gerenciamento Estoques de Cerveja

**Objetivos:** Desenvolver uma **API** usando a metodologia ***Test Driven Development*** - **TDD**. Codificar esta **API** usando a linguagem de programação **Java** na versão 11 e ***Maven***.  Documentar todos os procedimentos. 

**Ferramentas Usadas:** **IntelliJ**, **Typora**, **Mozilla Firefox**, **Git**.

---

16 de março de 2020 - 14:00 

- Gerar a estrutura do projeto ***Spring Boot*** usando site [Spring Initializr](https://start.spring.io/).

  - Selecionar: ***Maven Project***, **Java**; e adicionar as dependencias **Lombok**, **Spring Web**, **Spring Data JPA**, **Validation**, **H2 Database**, porém ainda irá faltar as dependência: **mapstruct**, **springfox swagger2**, **springfox swagger ui**.  

  ![](/home/silassl/Documents/workspace/EstoqueDeCervejas/img/Screenshot01.png)
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
  spring.datasource.url=jdbc:h2:mem:EstoqueDeCervejas;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
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
16 de março de 2020 - 15:13

- Dentro do pacote **service** iremos criar a classe `CervejaService`, no qual possuirá os métodos de registro, procura, exclusão, etc. 

- Dentro do pacote **exception** criar a classe com a exceção que será lançada chamada `JaExisteException`.

- Dentro do pacote **controller** será criado duas classes: `CervejaController` e `CervejaControllerDocs`,  que será uma interface. Nessa classes ***controllers*** haverá os mesmo metodos da classe ***service***.

- Na `RestController`, como é chamada a classe ***controller***, é uma boa prática mapea-la da seguinte forma: **"/api/v1/[nome da api]"**. 

---
16 de março de 2020 - 15:46

- Como está sendo usado a metodologia **TDD**, inicialmente adicionaremos o teste no método `registrarCerveja()`, caso dê errado será feito ele dar certo e só então o codigo será refatorado.

- Dentro do pacote **builder**, criada dentro do pacote de testes, será criado a classe `CervejaDTOBuilder`, do qual receberá valores para ser testada o método `registrarCerveja()`. Com mais detalhes, será instanciado um ***Optional*** irá buscar no `CervejaRepository` a tal cerveja. Se essa cerveja já existir então é lançado a exceção. Se não, usamos o metodo `save()` da classe `JpaRepository` para poder retornar essa cerveja dentro do **DTO**.

- Dentro do pacote **controller** será criada a classe `CervejaControllerTest`. Os metodos do tipo `void` que receberão as anotações `@Test` serão: `quandoPOSTRegistrarUmaCerveja()` e `quandoPOSTRegistrarComUmCampoEmBranco()`, para testar o método `registrarCerveja()`.

- Dentro do pacote **service** será criada a classe `CervejaServiceTest`.  Os metodos do tipo `void` que receberão as anotações `@Test` serão: `quandoUmaNovaCervejaForInformadaDeveraSerCriada()` e `quandoACervejaInformadaJaExistirLancarUmaExcecao()` para testar o método `registrarCerveja()`.
---
16 de março de 2020 - 16:24

- Teste Realizados, somente um falhou: `quandoUmaNovaCervejaForInformadaDeveraSerCriada()`, talvez porque já deveria sido registrado a mesma cerveja na classe `CervejaControllerTest`.
- Contudo, ao usar o comando `mvn spring-boot:run` a `build` falhou. Antes estava dando este mesmo problema (Este é o segundo projeto que estou desenvolvendo, dessa vez estou documentando um passo-a-passo), até mesmo usando o projeto do instrutor esta falhando a `build` do projeto. Contudo, pelo menos agora os teste rodam no **IDE**.  
- Usando o terminal do **IntelliJ**, commitar o que fiz até agora e este **README.md** para ficar como ***backup***.
- Detalhar o metodo `registrarCerveja()` na documentação.
- Buscar entender porque `mvn spring-boot:run` dá erro; tentar entender os erros.
---
16 de março de 2020 - 17:00

- O erro está na ao mapear usando a classe `CervejaMapper`. Porém não foi encontrado solução para isso ainda.

- Escrever o metodo `verificaSeEstaRegistrado()` na classe `CervejaService`. Usando um ***Optional*** será visto se o nome está presente na coleção.

- Refatorar o método `registrarCerveja()`. Antes:

  ```java
  public CervejaDTO registrarCerveja(CervejaDTO cervejaDTO) throws JaExisteException {
          Optional<Cerveja> optSavedBeer = cervejaRepository.procurarPeloNome(cervejaDTO.getName());
          if (optSavedBeer.isPresent()) {
              throw new JaExisteException(cervejaDTO.getName());
          }
          Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
          Cerveja cervejaSalva = cervejaRepository.save(cerveja);
          return cervejaMapper.toDTO(cervejaSalva);
      }
  ```

- Agora:

  ```java
  public CervejaDTO registrarCerveja(CervejaDTO cervejaDTO) throws JaExisteException {
          verificaSeEstaRegistrado(cervejaDTO.getName());
          Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
          Cerveja cervejaSalva = cervejaRepository.save(cerveja);
          return cervejaMapper.toDTO(cervejaSalva);
      }
  ```

- Agora a verificação é feita em outro método, pois este é um algoritmo que pode se repetir.

- Escrever o método `procurarPeloNome()`, `listarCervejas()`  na classe `CervejaService`.

- Escrever o método `procurarPeloNome()`, `listarCervejas()`, na classe `CervejaControllerDocs`.

- Sobrescrever os métodos `procurarPeloNome()`, `listarCervejas()` na classe `CervejaController`.

---
16 de março de 2020 - 17:30

- Testar as novas funcionalidades no pacote de testes. Escrever na classe `CervejaControllerTest` os metodos `void` para testes: `quandoGETComONomeValidoRetornOKStatus()` e `quandoGETForChamadoComNomeNaoCadastrado()`, ambos para testar o metodo `procurarPeloNome()`. 
- Escrever ainda na classe `CervejaControllerTest` os métodos `void` para testes: `quandoGETForChamadoParaListar()` e `quandoGETForChamadoParaListarMasEstiverVazio()` para testar o método `listarCervejas()`.
- Escrever na classe `CervejaServiceTest` os métodos `void` para testes: `quandoDadoUmNomeValidoRetornarACerveja()` e `lancarUmaExcecaoQuandoONomeNaoForEncontrado()` ambos são para testar o método `procurarPeloNome()`.
-  Escrever na classe `CervejaServiceTest` os métodos `void` para testes: `quandoChamarAListaDeCervejas()` e `quandoAListaChamadaEstiverVazia()` para testar o método `listarCervejas()`.

16 de março de 2020 - 18:00

- Escrever os métodos `exclusaoPeloId()` e `verificarSeExiste()` na classe `CervejaService`.
- Escrever o método `exclusaoPeloId()`  na classe `CervejaControllerDocs`.
- Sobreescrever o método `exclusaoPeloId()` na classe `CervejaController`

---

16 de março de 2020 - 18:10

- Escrever ainda na classe `CervejaControllerTest` os métodos `void` para testes: `quandoOIdForValidoParaExclusao() ` e `quandoOIdForInvalidoParaExclusao()` para testar o método `exclusaoPeloId()`.

- Escrever na classe `CervejaServiceTest` os métodos `void` para testes: `quandoExclusaoForChamadoComUmIdValidoDeveExcluirACervejaDoSistema()` e `quandoForTentadoFazerExclusaoComIdInvalido()` ambos são para testar o método `procurarPeloNome()`.

- Criar dentro do pacote de teste, o pacote **utils** e fazer a classe `JsonConvertionsUtils`, onde será posto o método `asJsonString` que estava dentro do `CervejaDTOBuilder`.

---

  17 de março de 2020 - 08:00

- Escrever a classe  `EstoqueExcedeuException`  dentro do pacote **exception**, que lança uma exceção para quando o estoque estiver cheio. 
- Escrever o método `incrementoDoEstoque()` na classe `CervejaService`. 
- Dentro do pacote **dto**, criar a classe `QuantidadeDTO`  com apenas o atributo `quantidade`.
- Na classe `CervejaControllerDocs` escrever a operação `incrementarDoEstoque()`.
- Sobreescrever esta operação na classe `CervejaController`. 
---
17 de março de 2020 - 08:30

- Testar na classe `CervejaControllerTest`  o método `incrementoDoEstoque()` usando, para isso, os métodos: `quandoPATCHParaIncrementarForChamadoRetornarOKStatus()`, `quandoPATCHForChamadoParaIncrementarComValorMaiorQueOMaximoPermitido()` e `quandoPATCHForChamadoParaIncrementarComOIDInvalido()`.

- Testar na classe `CervejaServiceTest`  o método `incrementoDoEstoque()` usando, para isso, os métodos: `quandoForIncrementar()`, `quandoIncrementoForMaiorQueOMaximoPermitido()` e `quandoOIdDadoParaIncrementoForInvalido()`.

---

7 de março de 2020 - 09:00

- Escrever o método `decrementoDoEstoque()` na classe `CervejaService`. 
- Escrever o método `decrementoDoEstoque()` na classe `CervejaController`. 
---
7 de março de 2020 - 09:20

- Testar na classe `CervejaControllerTest`  o método `decrementoDoEstoque()` usando, para isso, os métodos: `quandoPATCHForChamadoParaDecremento()`, `quandoPATCHForChamadoParaDecrementoMenorQueZero()` e `quandoPATCHForChamadoParaDecrementarCervejaComIDInvalido()`.

- Testar na classe `CervejaServiceTest`  o método `incrementoDoEstoque()` usando, para isso, os métodos: `quandoDecrementar()`, `quandoOEstoqueEstiverVazioParaDecremento()` e `quandoDecrementoForMenorQueZero()`, `quandoOIDDadoParaDecrementoEstiverInvalido()`.

---
7 de março de 2020 - 09:35

Usando **TDD**, em outras palavras está sendo feitos testes unitarios durante o processo de cada funcionalidade; e somentes detalhes que o programador não esteve atento durante esse processo irão para os ***testers***, obviamente, ***testers*** possuem os seus roteiros, conhecem melhor os procedimentos. Porém, o programador realizando testes unitarios, permite saber se tudo está ocorrendo de forma esperada ao invez de ser feito as cegas. Usando a arquitetura ***REST*** está sendo aplicado três dos oitos metodos disponíveis pelo protocolo **HTTP**: ***PUT***, ***GET*** e ***DELETE***. Usando o programa **Postman** é possivel ver esse processo, inclusive os codigos da `@ApiResponse`  sendo imprimidos no console (200, 400, 201 e 404), do qual representam ***ok status***, ***Not Found Status*** e ***Bad Request Status***. 

> REST é um termo definido por Roy Fielding em sua tese de mestrado no  qual ele descreve sobre um estilo de arquitetura de software sobre um  sistema operado em rede. REST é um acrônimo para "Transferência de  Estado Representacional" (Representational State Transfer).

No **Postman**, é criado uma coleção que irá está conectada ao **localhos:8080/api/1v/[nome da api]**, nele é adicionado os protocolos antes citados, porém como pode ser visto na figura abaixo, com ***GET*** podemos listar e procuras cervejas pelo nome, com ***POST*** registrar as cervejas no banco de dados e por fim, ***DELETE*** que é a exclusao de uma determinada cerveja usando o **id**. Em seu console é possivel observar os codigos junto com as mensagens definidas. Esses codigos e mensagens foram escritos na classe `CervejaControllerDocs`.  

![](/home/silassl/Documents/workspace/EstoqueDeCervejas/img/Screenshot02.png)

Para os testes foi usado o **Mockito** do qual permite trabalhar com objetos; e testa-los. Diferente do **JUnit** que permite apenas variaveis. Ao usar o **Mockito** o programador ou ***tester*** tem que ter em mente esse algoritmo:

```
// given
Object objeto = new Objeto();
Object objetoEsperado = new Objeto();

// when

when(fazer_algo_com_o_objeto).thenReturn(colecação_de_objetos)

// then
assertThat(objeto, objetoEsperado)
```

Sendo ***given*** a instanciação do objeto, ***when*** quando fazer (ou tambem o que fazer com o objeto dado, por exemplo: pegar o id, nome, etc) e por fim, esse tambem entra como "o que fazer com o objeto", pois no ***then** colocamos como queremos que o objeto seja testado (queremos compara-lo a outro objeto, queremos verificar algo no objeto, forçar o resultado passar mesmo quando estar errado; com o **Mockito** é possivel).

Mas use o **Mockito** de forma responsavel, pois como foi dito anteriormente é possivel forçar o algoritmo passar nos testes mesmo quando está errado o resultado (por exempo: 2 + 2 = 4, no **Mockito** podemos fazer com que 2 + 2 seja igual a 5). Por isso, é recomendado usar o **Mockito** quando devemos testar a aplicação usando determinado banco de dados, mas não temos acesso a esse banco de dados ainda.

Aqui no final era para estar o codigo completo da aplicação para ser replicado usando as etapas, porém o diario de bordo está escrito de forma coesa, sendo possivel, ser feito esse **Gerenciador de Estoque de Cervejas** em outras linguagens.

- Commitar e entregar projeto. 

**P.S.:** futuramente, escrever os algoritmos em **pseudo-codigo**. 