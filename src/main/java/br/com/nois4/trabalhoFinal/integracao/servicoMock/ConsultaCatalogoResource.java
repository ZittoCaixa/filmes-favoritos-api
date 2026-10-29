package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.FilmeFavoritoService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/filmes-favoritos/{id}/catalogo")
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaCatalogoResource {
    private final FilmeFavoritoService filmes;
    private final ConsultaCatalogoService consultaCatalogo;

    public ConsultaCatalogoResource(FilmeFavoritoService filmes, ConsultaCatalogoService consultaCatalogo) {
        this.filmes = filmes;
        this.consultaCatalogo = consultaCatalogo;
    }

    @GET
    public FilmeCatalogoResponse consultar(@PathParam("id") Long id) {
        return consultaCatalogo.consultar(filmes.buscar(id).titulo);
    }
}
