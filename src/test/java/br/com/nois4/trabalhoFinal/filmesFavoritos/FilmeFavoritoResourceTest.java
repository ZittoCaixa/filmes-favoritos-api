package br.com.nois4.trabalhoFinal.filmesFavoritos;

import br.com.nois4.trabalhoFinal.integracao.servicoMock.ControleEstadoRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class FilmeFavoritoResourceTest {
    private static Map<String, Object> filme(String titulo) {
        return Map.of("titulo", titulo, "diretor", "Diretora Teste", "ano", 2020, "genero", "Drama");
    }

    @Test
    void executaCrudEImpedeDuplicidadeSemDiferenciarAcento() {
        int id = given().contentType(ContentType.JSON).body(filme("Amélie Teste"))
                .when().post("/filmes-favoritos").then()
                .statusCode(201)
                .header("Location", containsString("/filmes-favoritos/"))
                .body("titulo", is("Amélie Teste"))
                .extract().path("id");

        try {
            given().when().get("/filmes-favoritos").then().statusCode(200)
                    .body("id", hasItem(id));
            given().when().get("/filmes-favoritos/" + id).then().statusCode(200)
                    .body("diretor", is("Diretora Teste"));

            given().contentType(ContentType.JSON).body(filme("amelie teste"))
                    .when().post("/filmes-favoritos").then().statusCode(409)
                    .body("codigo", is("FILME_DUPLICADO"))
                    .body("timestamp", notNullValue());

            given().contentType(ContentType.JSON).body(Map.of(
                            "titulo", "Amélie Teste", "diretor", "Diretora Atualizada", "ano", 2001, "genero", "Comédia"))
                    .when().put("/filmes-favoritos/" + id).then().statusCode(200)
                    .body("diretor", is("Diretora Atualizada"));
            given().when().get("/filmes-favoritos/" + id).then().statusCode(200)
                    .body("diretor", is("Diretora Atualizada"));
        } finally {
            given().when().delete("/filmes-favoritos/" + id).then().statusCode(204);
        }
        given().when().get("/filmes-favoritos/" + id).then().statusCode(404)
                .body("codigo", is("FILME_NAO_ENCONTRADO"))
                .body("timestamp", notNullValue());
    }

    @Test
    void rejeitaDadosInvalidosComErroPadronizado() {
        given().contentType(ContentType.JSON)
                .body(Map.of("titulo", "", "diretor", "X", "ano", 1800, "genero", "Drama"))
                .when().post("/filmes-favoritos").then().statusCode(400)
                .body("codigo", is("DADOS_INVALIDOS"))
                .body("mensagem", not(isEmptyOrNullString()))
                .body("timestamp", notNullValue());
    }

    @Test
    void usaFallbackQuandoCatalogoMockEstaIndisponivel() {
        int id = given().contentType(ContentType.JSON).body(filme("Filme Integração Teste"))
                .when().post("/filmes-favoritos").then().statusCode(201).extract().path("id");
        try {
            given().contentType(ContentType.JSON).body(Map.of("disponivel", false))
                    .when().put("/mock/catalogo/disponibilidade").then().statusCode(200);
            given().when().get("/filmes-favoritos/" + id + "/catalogo").then().statusCode(200)
                    .body("fallback", is(true))
                    .body("origem", is("fallback-local"));
        } finally {
            given().contentType(ContentType.JSON).body(Map.of("disponivel", true))
                    .when().put("/mock/catalogo/disponibilidade").then().statusCode(200);
            given().when().delete("/filmes-favoritos/" + id).then().statusCode(204);
        }
    }

    @Test
    void verificaHealthChecksEContadorDeNegocio() {
        given().when().get("/q/health/live").then().statusCode(200).body("status", is("UP"));
        given().when().get("/q/health/ready").then().statusCode(200).body("status", is("UP"));
        given().contentType(ContentType.JSON).body(Map.of("disponivel", false))
                .when().put("/admin/readiness").then().statusCode(200);
        given().when().get("/q/health/ready").then().statusCode(503).body("status", is("DOWN"));
        given().contentType(ContentType.JSON).body(Map.of("disponivel", true))
                .when().put("/admin/readiness").then().statusCode(200);

        int id = given().contentType(ContentType.JSON).body(filme("Métrica de negócio teste"))
                .when().post("/filmes-favoritos").then().statusCode(201).extract().path("id");
        given().when().get("/q/metrics").then().statusCode(200)
                .body(containsString("filmes_favoritos_criados_total"));
        given().when().delete("/filmes-favoritos/" + id).then().statusCode(204);
    }
}
