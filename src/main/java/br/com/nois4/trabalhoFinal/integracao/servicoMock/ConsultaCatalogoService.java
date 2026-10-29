package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ConsultaCatalogoService {
    @Inject
    @RestClient
    FilmesMockClient client;

    @Retry(maxRetries = 2, delay = 100)
    @Fallback(fallbackMethod = "responderIndisponivel")
    public FilmeCatalogoResponse consultar(String titulo) {
        return client.consultar(titulo);
    }

    public FilmeCatalogoResponse responderIndisponivel(String titulo, Throwable causa) {
        return new FilmeCatalogoResponse(titulo, false, "fallback-local", true,
                "Catálogo externo temporariamente indisponível.");
    }
}
