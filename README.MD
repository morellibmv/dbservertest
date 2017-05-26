# Teste DB Server - por Bruno Morelli Vargas

Implementei toda a parte de serviços responsáveis por definir e gerenciar a votação, através de chamadas REST.

Infelizmente não consegui terminar a tempo a parte da interface e nem implementar os testes unitários.

## Considerações

- Para a votação, considerei que todas as votações são aceitas apenas até o meio-dia. Entretanto, implementei uma chamada para ativar/desativar a validação de horário. A restrição de horário para votação, por padrão, está habilitada. Se necessário, deve ser desativada.
- Implementei um script SQL que cria, além da estrutura de banco utilizada, registros para o dia anterior a qualquer validação que você pode fazer. O restaurante Sashiburi será o restaurante selecionado do dia anterior ao seu, portanto, não permitindo votar nele, caso esteja habilitada a limitação para escolher restaurantes repetidos na semana
- Por padrão, o sistema permite votar em um restaurante que já foi o mais votado da semana

## Instruções para validação
### Requisitos
- Gradle 3.5
- Docker (utilizei a versão 17.04.0-ce)

### Passos
- No diretório raíz deste projeto, executar a task *build* do Gradle
- Após, será necessário baixar as imagens docker e criar os respectivos containers com os comandos abaixo:

```
docker-compose build
```

```
docker-compose up
```

- A aplicação estará disponível no IP do daemon docker na porta 8080. Para descobrir o IP do Daemon, executar o comando:

```
docker-machine ip
```

- É possível validar os services utilizando qualquer cliente REST, como por exemplo o Postman

### Votação, configuração e resultado

#### Votação
- URL http://IP_DOCKER:8080/rest/vote/
- Verb: POST
- Parâmetros:
 - Request Body, application/JSON
   - user.email: email do usuário. Não se faz nenhuma validação quanto a formato de email. O objetivo desta informação é apenas distinguir os diferentes usuários neste teste.
   - restaurant.name: nome do restaurante desejado.
   - Exemplo: {"user":{"email": "brunomv@gmail.com"}, "restaurant":{"name":"Sashiburi"} }
 - Retornos
   - HTTP 200: o voto foi processado com sucesso
   - HTTP 400: o restaurante escolhido já foi o mais votado na semana
     - Response body: "Este restaurante não pode ser escolhido."
   - HTTP 405: as votações não são mais permitidas
     - Response body: "Votações Encerradas"
- Informações adicionais
  - Um usuário pode votar mais de uma vez, mas não é cumulativo. Se um usuário já votou no dia, seu novo voto substituirá o antigo.
  - O nome do restaurante é case insensitive, portanto, por exemplo, votando nas opções "Speed" e "SPeeD" contabilizam voto para o mesmo restaurante.

#### Resultado Atual da Votação
  - URL http://IP_DOCKER:8080/rest/vote/
  - Verb: GET
  - Parâmetros: N/A
   - Retornos
     - HTTP 200: sucesso na contagem dos votos
        - Response body: String com o nome do restaurante escolhido
  - Informações adicionais
    - Solicitar o atual resultado da votação não interfere na continuidade da votação. Ou seja, ainda é possível inserir mais votos, caso seja permitido pelas outras restrições.

#### Ativar/Desativar restaurante repetido
- URL: http://IP_DOCKER:8080/rest/vote/config
- Verb: POST
- Parâmetros: N/A
- Retorno:
  - HTTP 200
    - Response body: um booleano informando o estado que foi alterado. Caso retorne *true*, a votação permitirá votos de restaurantes repetidos. Se *false*, não permitirá.
- Informações Adicionais:
  - Caso esta configuração seja aplicada em meio a uma votação, e existem votos para um restaurante já sendo o mais votado em outros dias da semana, não são reiniciados os votos, ou seja, um restaurante repetido pode ser o mais votado

#### Verificar restaurante repetido
- URL: http://IP_DOCKER:8080/rest/vote/config
- Verb: GET
- Parâmetros: N/A
- Retorno:
  - HTTP 200
    - Response body: um booleano informando o estado que está a flag de restaurante repetido. Caso retorne *true*, a votação permitirá votos de restaurantes repetidos. Se *false*, não permitirá.

#### Ativar/Desativar restrição de horário para votação até o meio-dia
- URL: http://IP_DOCKER:8080/rest/vote/restaurant/config
- Verb: POST
- Parâmetros: N/A
- Retorno:
  - HTTP 200
    - Response body: um booleano informando o estado que foi alterado. Caso retorne *true*, significa que os votos terminam ao meio-dia. Se *false*, votos após o meio-dia serão aceitos.

#### Verificar restrição de horário de votação

- URL: http://IP_DOCKER:8080/rest/vote/restaurant/config
- Verb: GET
- Parâmetros: N/A
- Retorno:
  - HTTP 200
    - Response body: um booleano informando o estado que está a flag de restaurante repetido. Caso retorne *true*, significa que os votos terminam ao meio-dia. Se *false*, votos após o meio-dia serão aceitos.
