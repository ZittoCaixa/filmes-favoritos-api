package br.com.nois4.trabalhoFinal.plataform.error;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.ConsultaInvalidaException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConsultaInvalidaMapper implements ExceptionMapper<ConsultaInvalidaException> {
    @Override
    public Response toResponse(ConsultaInvalidaException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.de("CONSULTA_INVALIDA", exception.getMessage()))
                .build();
    }
}
