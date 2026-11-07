package br.com.nois4.trabalhoFinal.plataform.error;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.ErroNaConsultaException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ErroNaConsultaMapper implements ExceptionMapper<ErroNaConsultaException> {
    @Override
    public Response toResponse(ErroNaConsultaException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.de("ERRO_NA_CONSULTA", exception.getMessage()))
                .build();
    }
}
