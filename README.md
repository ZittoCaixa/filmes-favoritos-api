#️⃣ Aluno: RAMON MARQUES COUTINHO
#️⃣ Matrícula CAIXA: C078969-0
#️⃣ Assistente Pleno
#️⃣ CESOB


# Filmes Favoritos API

API REST de gestão de filmes favoritos, desenvolvida com Java 21, Quarkus 3 e Maven. O projeto demonstra CRUD transacional com Hibernate ORM/Panache e H2, validação, regra contra filmes duplicados, exceções específicas, integração REST com mock local, resiliência, health checks, métricas Micrometer por endpoint e logging de requisições. A aplicação de referência `catalogo-api` foi mantida intacta.

## Executar

Requisitos: JDK 21 e Maven 3.9 ou superior.

```powershell
cd filmes-favoritos-api
mvn quarkus:dev
```

O serviço fica em `http://localhost:8080`. O profile `dev` usa H2 em arquivo (`data/filmes`) e atualiza o schema sem apagar os dados ao reiniciar. O profile `prod` usa outro arquivo (`data/filmes-prod`) e permite sobrescrever o JDBC URL pelo ambiente. Para executar os testes:

```powershell
mvn test
```

O profile `prod` também usa H2 por padrão para facilitar a demonstração, mas permite apontar para outro JDBC URL. A configuração `app.nome` é sobrescrita com a variável `APP_NOME`; o endereço do mock usa `FILMES_MOCK_URL`, e a porta HTTP usa `PORT`.

```powershell
$env:APP_NOME = "Minha API de Filmes (dev)"
mvn quarkus:dev
# Encerre o modo dev antes de iniciar a configuração prod:
$env:PORT = "8082"
$env:APP_NOME = "Minha API de Filmes (prod)"
$env:QUARKUS_PROFILE = "prod"
mvn package
java -jar target\quarkus-app\quarkus-run.jar
```

Para usar H2 em outro arquivo, defina `DB_URL`, por exemplo `jdbc:h2:file:./data/filmes-local;AUTO_SERVER=TRUE`. O profile `prod` usa estratégia `update`; em produção real, configure um banco gerenciado e migrações.

## API principal

Recurso: `/filmes-favoritos`

| Método | Caminho | Resposta |
|---|---|---|
| GET | `/filmes-favoritos` | 200 e lista |
| GET | `/filmes-favoritos/{id}` | 200 ou 404 |
| POST | `/filmes-favoritos` | 201, corpo e `Location` |
| PUT | `/filmes-favoritos/{id}` | 200 com o recurso atualizado |
| DELETE | `/filmes-favoritos/{id}` | 204 ou 404 |

O JSON de criação/atualização exige `titulo`, `diretor`, `ano` (entre 1888 e 2100) e `genero`. A regra de negócio considera duplicados títulos iguais após remoção de acentos e comparação sem distinção entre maiúsculas/minúsculas; uma tentativa duplicada retorna 409. Os erros usam o formato `codigo`, `mensagem` e `timestamp`: validação e consulta inválida (400), filme ausente (404), duplicidade (409) e falha de consulta ao banco (500).

| Exceção | Código | HTTP | Exemplo |
|---|---|---:|---|
| `ConsultaInvalidaException` | `CONSULTA_INVALIDA` | 400 | `GET /mock/catalogo/filmes?titulo=` |
| `FilmeNaoEncontradoException` | `FILME_NAO_ENCONTRADO` | 404 | `GET /filmes-favoritos/999999` |
| `FilmeDuplicadoException` | `FILME_DUPLICADO` | 409 | Criar novamente um título já cadastrado |
| `ErroNaConsultaException` | `ERRO_NA_CONSULTA` | 500 | Falha de persistência ao listar ou buscar filmes |

Uma indisponibilidade do catálogo externo continua atendida pelo fallback local (HTTP 200); a causa é registrada no log como falha de consulta.

## Integração mock e resiliência

    `FilmesMockClient` consome o endpoint mock local `/mock/catalogo/filmes`. Para consultar o catálogo relacionado a um favorito, use `GET /filmes-favoritos/{id}/catalogo`. O serviço de integração tenta novamente duas vezes e, se persistir a falha HTTP/conexão, aplica fallback JSON controlado com `fallback: true`.

```powershell
# Criar o filme e guardar o id retornado
$filme = Invoke-RestMethod -Method Post -Uri http://localhost:8080/filmes-favoritos `
  -ContentType application/json `
  -Body '{"titulo":"Central do Brasil","diretor":"Walter Salles","ano":1998,"genero":"Drama"}'

# Mock UP: integração retorna origem "mock-local"
Invoke-RestMethod http://localhost:8080/filmes-favoritos/$($filme.id)/catalogo

# Simular DOWN e demonstrar o fallback; depois restaurar UP
Invoke-RestMethod -Method Put -Uri http://localhost:8080/mock/catalogo/disponibilidade `
  -ContentType application/json -Body '{"disponivel":false}'
Invoke-RestMethod http://localhost:8080/filmes-favoritos/$($filme.id)/catalogo
Invoke-RestMethod -Method Put -Uri http://localhost:8080/mock/catalogo/disponibilidade `
  -ContentType application/json -Body '{"disponivel":true}'
```

## Saúde e métricas

- `GET /q/health/live`: verifica se o processo está vivo; deve ficar DOWN quando a instância não consegue operar.
- `GET /q/health/ready`: verifica se o banco está acessível e se a aplicação está habilitada para receber tráfego. É possível controlar essa condição na demonstração: `PUT /admin/readiness` com `{"disponivel":false}` e depois `true`.
- `GET /q/metrics`: scrape Prometheus. A série `filmes_favoritos_criados_total` aumenta após cada criação bem-sucedida.
- Para cada endpoint REST, `api_requisicoes_total` conta as chamadas, `api_tempo_medio_resposta_seconds` expõe o tempo médio, `api_tempo_resposta_seconds_sum` e `api_tempo_resposta_seconds_count` permitem calcular a média em consultas Prometheus, `api_erros_total` conta respostas HTTP 4xx/5xx e `api_taxa_erros` expõe a proporção de erros. As séries são identificadas pelas tags `endpoint` e `metodo`.
- Exemplos de consulta Prometheus: `api_tempo_medio_resposta_seconds{endpoint="FilmeFavoritoResource.listar",metodo="GET"}` para duração média; `api_taxa_erros{endpoint="FilmeFavoritoResource.buscar",metodo="GET"}` para a proporção de erros desse endpoint.
- `GET /info`: mostra `app.nome`, configurável por `APP_NOME`.

As requisições e seus resultados são registrados com método, endpoint, status e duração; respostas 4xx são registradas como avisos e 5xx como erros. Corpos de requisição não são registrados.

Exemplo de requisição inválida: `POST /filmes-favoritos` com `{"titulo":"","diretor":"X","ano":1800,"genero":"Drama"}` retorna 400 padronizado. `GET /mock/catalogo/filmes?titulo=` retorna `CONSULTA_INVALIDA` (400), e `GET /filmes-favoritos/999999` retorna `FILME_NAO_ENCONTRADO` (404).

## Lombok e arquitetura

`ControleEstadoRequest` usa `@Getter`, `@Setter` e `@NoArgsConstructor`: o Lombok gera os métodos de acesso e o construtor padrão usado na desserialização JSON. A entidade JPA não usa `@Data`, evitando `equals`, `hashCode` e `toString` automáticos sobre estado persistente.

```text
src/main/java/br/com/nois4/trabalhoFinal/
├── filmesFavoritos/{boundary,domain,repository}
├── integracao/servicoMock
└── plataform/{error,health,info,metrics}
```

## Postman

Importe `postman/filmes-favoritos.postman_collection.json`. A coleção inclui exemplos de CRUD, validação/404, conflito de duplicidade, estado UP/DOWN da integração, health, métrica e configuração.
