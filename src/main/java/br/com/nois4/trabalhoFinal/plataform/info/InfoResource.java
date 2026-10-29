package br.com.nois4.trabalhoFinal.plataform.info;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
public class InfoResource {
    @ConfigProperty(name = "app.nome")
    String nome;

    @GET
    public InfoResponse info() {
        return new InfoResponse(nome);
    }

    public record InfoResponse(String nome) {
    }
}
