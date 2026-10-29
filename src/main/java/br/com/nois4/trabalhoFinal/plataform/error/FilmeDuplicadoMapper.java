package br.com.nois4.trabalhoFinal.plataform.error;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.FilmeDuplicadoException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilmeDuplicadoMapper implements ExceptionMapper<FilmeDuplicadoException> {
    @Override
    public Response toResponse(FilmeDuplicadoException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.de("FILME_DUPLICADO", exception.getMessage()))
                .build();
    }
}
