package br.com.nois4.trabalhoFinal.plataform.error;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.FilmeNaoEncontradoException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilmeNaoEncontradoMapper implements ExceptionMapper<FilmeNaoEncontradoException> {
    @Override
    public Response toResponse(FilmeNaoEncontradoException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.de("FILME_NAO_ENCONTRADO", exception.getMessage()))
                .build();
    }
}
