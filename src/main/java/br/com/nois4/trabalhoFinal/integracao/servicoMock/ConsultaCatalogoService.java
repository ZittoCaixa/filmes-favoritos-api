package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import br.com.nois4.trabalhoFinal.filmesFavoritos.domain.ErroNaConsultaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConsultaCatalogoService {
    private static final Logger LOGGER = Logger.getLogger(ConsultaCatalogoService.class);
    @Inject
    @RestClient
    FilmesMockClient client;

    @Retry(maxRetries = 2, delay = 100)
    @Fallback(fallbackMethod = "responderIndisponivel")
    public FilmeCatalogoResponse consultar(String titulo) {
        return client.consultar(titulo);
    }

    public FilmeCatalogoResponse responderIndisponivel(String titulo, Throwable causa) {
        ErroNaConsultaException erro = new ErroNaConsultaException(
                "Falha ao consultar o catálogo externo para o título '" + titulo + "'.", causa);
        LOGGER.warn("Consulta ao catálogo falhou; será utilizado o fallback local.", erro);
        return new FilmeCatalogoResponse(titulo, false, "fallback-local", true,
                "Catálogo externo temporariamente indisponível.");
    }
}
