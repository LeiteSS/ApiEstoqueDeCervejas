# Beer Stock

[![License: FDL 1.3](https://img.shields.io/badge/License-FDL%20v1.3-blue.svg)](https://www.gnu.org/licenses/fdl-1.3)

A company wants a application to manager its beer stock. Where it is possible to create, update, delete and view all beers registered, otherwise must be possible to decrement and increment the stock until the maximum quantity. 

## Features

- Create a beer.
- Update a beer using the Id.
- Delete a beer using the Id.
- Get all beer created. 
- Increment the beer's quantity until the maximum quantity.
- Decrement the beer's quantity.

## Usage

Firstly, clone this repository:

```bash
git clone https://github.com/LeiteSS/ApiEstoqueDeCervejas.git
```

With everything cloned, navigate to the folder with the projects `cd ApiEstoqueDeCervejas`. Inside, you can run the **API** using the command `./mvnw spring-boot:run`. After the installation of the dependencies and build, the application must start in http://localhost:8080. If you have **Postman** use the [api's collection](https://documenter.getpostman.com/view/14979428/TzJydFvx). However, navigate to the **front-end** using `cd front-end` and run `ng serve` for a dev server. In the browser, navigate to http://localhost:4200/ and the front-end it is ready to communicate with the back-end. 

The back-end has some tests, to run them use:

```bash
# BeerControllerTest
./mvnw -Dtest=BeerControllerTest test

# BeerServiceTest
./mvnw -Dtest=BeerControllerTest test
```

***The commands are for operational systems using Linux, but in Windows the commands for Maven are the same*

### Attribution

This projects uses [**Maven**](https://maven.apache.org/what-is-maven.html), [**Lombok**](https://projectlombok.org/), [**Mapstruct**](https://mapstruct.org/),  [**H2 Database**](https://www.h2database.com/html/main.html), **Spring Boot Data JPA**, **Spring Boot Validation**, **Spring Boot Web**, [**Swagger 2**](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api), [**Jackson JSON**](https://github.com/FasterXML/jackson), [**Node Js**](https://nodejs.org/en/docs/), **Angular** e [**Angular Material**](https://material.angular.io/).  

## Credits

PELEIAS, Rodrigo. "[**Digital Innovation: Expert class - Desenvolvimento de testes unitários para  validar uma API REST de gerenciamento de estoques de cerveja**](https://github.com/rpeleias/beer_api_digital_innovation_one)" **Digital Innovation One**, 2020.

SANTOS, Kamilah. "[**Live Coding - Crie seu gerenciador de salas de reuniões com Java e Angular**](https://github.com/Kamilahsantos/Client-Angular-Live-Coding-Dio)". **Digital Innovation One**, 2020.

BARBOZA, Glauber. "[**Como Jackson e Binder são utilizados / Criando Aplicações Web Com Spring Web MVC**](https://github.com/glauber-barboza/digitalinnovation-jackson)". **Digital Innovation One**, 2019.

## License

Copyright (C)  2021  Silas Leite.
Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.3 or any later version published by the Free Software Foundation; with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the license is included in the section entitled "GNU Free Documentation License".


