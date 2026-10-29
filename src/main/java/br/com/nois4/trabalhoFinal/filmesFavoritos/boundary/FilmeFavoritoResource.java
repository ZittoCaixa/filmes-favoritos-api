package br.com.nois4.trabalhoFinal.filmesFavoritos.boundary;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.FilmeFavoritoService;
import br.com.nois4.trabalhoFinal.filmesFavoritos.repository.FilmeFavoritoEntity;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("/filmes-favoritos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FilmeFavoritoResource {
    private final FilmeFavoritoService service;

    public FilmeFavoritoResource(FilmeFavoritoService service) {
        this.service = service;
    }

    @GET
    public List<FilmeFavoritoResponse> listar() {
        return service.listar().stream().map(FilmeFavoritoResource::paraResponse).toList();
    }

    @GET
    @Path("/{id}")
    public FilmeFavoritoResponse buscar(@PathParam("id") Long id) {
        return paraResponse(service.buscar(id));
    }

    @POST
    public Response criar(@Valid FilmeFavoritoRequest request, @Context UriInfo uriInfo) {
        FilmeFavoritoResponse criado = paraResponse(service.criar(request));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(criado.id().toString()).build())
                .entity(criado).build();
    }

    @PUT
    @Path("/{id}")
    public FilmeFavoritoResponse atualizar(@PathParam("id") Long id, @Valid FilmeFavoritoRequest request) {
        return paraResponse(service.atualizar(id, request));
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }

    private static FilmeFavoritoResponse paraResponse(FilmeFavoritoEntity entity) {
        return new FilmeFavoritoResponse(entity.id, entity.titulo, entity.diretor,
                entity.ano, entity.genero, entity.criadoEm);
    }
}
