package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/mock/catalogo")
@Produces(MediaType.APPLICATION_JSON)
public class MockCatalogoResource {
    private final MockServicoState state;

    public MockCatalogoResource(MockServicoState state) {
        this.state = state;
    }

    @GET
    @Path("/filmes")
    public Response consultar(@QueryParam("titulo") @NotBlank String titulo) {
        if (!state.isDisponivel()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new FilmeCatalogoResponse(titulo, false, "mock-local", false,
                            "Mock do catálogo configurado como indisponível."))
                    .build();
        }
        return Response.ok(new FilmeCatalogoResponse(titulo, true, "mock-local", false,
                "Filme encontrado no catálogo mock.")).build();
    }

    @PUT
    @Path("/disponibilidade")
    public Response configurarDisponibilidade(@Valid ControleEstadoRequest request) {
        state.setDisponivel(request.getDisponivel());
        return Response.ok(new EstadoServicoResponse(state.isDisponivel())).build();
    }

    public record EstadoServicoResponse(boolean disponivel) {
    }
}
