package br.com.nois4.trabalhoFinal.plataform.health;

import br.com.nois4.trabalhoFinal.integracao.servicoMock.ControleEstadoRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/admin/readiness")
@Produces(MediaType.APPLICATION_JSON)
public class ReadinessResource {
    private final ReadinessState state;

    public ReadinessResource(ReadinessState state) {
        this.state = state;
    }

    @PUT
    public EstadoReadinessResponse configurar(@Valid ControleEstadoRequest request) {
        state.setPronta(request.getDisponivel());
        return new EstadoReadinessResponse(state.isPronta());
    }

    public record EstadoReadinessResponse(boolean pronta) {
    }
}
