# Capital Gain

Este projeto é uma solução para o desafio de cálculo de taxas sobre operações de compra e venda de ações. Ele foi desenvolvido em Java e utiliza a biblioteca Gson para manipulação de JSON.

# Decisões Técnicas e Arquiteturais

## Separação de Responsabilidades

O código foi dividido em classes com responsabilidades específicas, como JsonProcessor (processamento de JSON), StockTaxCalculatorService (cálculo de taxas) e GsonFactory (configuração do Gson). Dessa forma podemos desacoplar a solução e seguir o princípio da Single Responsibility Principle (SRP) para garantir que cada classe tenha uma única responsabilidade, facilitando a manutenção e a testabilidade.

## Uso de testes unitários e de integração

Foram implementados alguns testes unitários e de integração visando cobrir vários cenários, descrito no documento enviado para este desafio. Os testes visam principalmente garantir que os cálculo de taxas estejam endo calculados corretamente.

Além disso os testes ajudaram a refatorar a aplicação melhorando o design das classes.

## Utilização do Design Pattern Strategy

O **design pattern Strategy** foi utilizado na classe `StockTaxCalculatorService` para reduzir a necessidade de estruturas complexas de controle com `if-else`, permitindo o desacoplamento da lógica de cálculo para cada tipo de operação (como **BUY** e **SELL**) ou regra de negócio específica. Essa abordagem traz diversos benefícios:

### 1. Separação de Responsabilidades
Cada estratégia de cálculo é encapsulada em uma classe separada, seguindo o **princípio da responsabilidade única (SRP)** do SOLID, facilitando a manutenção, permitindo que novas regras ou alterações nas existentes sejam introduzidas sem modificar a lógica central do serviço.

### 2. Facilidade de Extensão
Ao invés de concentrar diferentes regras em um único método complexo, novas estratégias podem ser adicionadas sem impactar o código existente, respeitando o **princípio aberto/fechado (OCP)** do SOLID.

### 3. Flexibilidade em Tempo de Execução
Com o padrão Strategy, a classe `StockTaxCalculatorService` pode selecionar dinamicamente a estratégia adequada para o cálculo de taxas, dependendo do tipo de operação (compra ou venda).# Como Compilar e Executar o Projeto

## Pré-requisitos
- **Java Development Kit (JDK)** 11 ou superior.
- **Maven** (para gerenciamento de dependências).

## Passos para executar a aplicação usando Docker
### 1. Construir a imagem Docker:
```
docker build -t capital-gain
```
### 1. Executar o cantainer:
```
docker run -it --rm -p 8080:8080 capital-gain
```

## Passos para Compilar e Executar (sem uso do Docker)

### 1. Compile o projeto:
```
mvn clean compile
```

### 2. Execute o projeto:
```
mvn exec:java -Dexec.mainClass="com.nubank.Main"
```

### 3. Insira o JSON:
```
[{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]
```
Pressione Enter para ver o resultado.

### 4. Para sair:
Digite **exit** e pressione Enter.

Caso queira executar os testes utilize o comando:
```
mvn test
```