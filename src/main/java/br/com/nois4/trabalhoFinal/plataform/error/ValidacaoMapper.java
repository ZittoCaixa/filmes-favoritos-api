package br.com.nois4.trabalhoFinal.plataform.error;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidacaoMapper implements ExceptionMapper<ResteasyReactiveViolationException> {
    @Override
    public Response toResponse(ResteasyReactiveViolationException exception) {
        String mensagem = exception.getConstraintViolations().stream()
                .map(violacao -> violacao.getPropertyPath() + ": " + violacao.getMessage())
                .sorted()
                .findFirst()
                .orElse("Verifique os dados enviados.");
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.de("DADOS_INVALIDOS", mensagem))
                .build();
    }
}
