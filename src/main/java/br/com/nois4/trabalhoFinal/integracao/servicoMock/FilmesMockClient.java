package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mock/catalogo/filmes")
@RegisterRestClient(configKey = "filmes-mock")
@Produces(MediaType.APPLICATION_JSON)
public interface FilmesMockClient {
    @GET
    FilmeCatalogoResponse consultar(@QueryParam("titulo") String titulo);
}
