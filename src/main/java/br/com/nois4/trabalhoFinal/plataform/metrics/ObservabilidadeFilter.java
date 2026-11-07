package br.com.nois4.trabalhoFinal.plataform.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Provider
@ApplicationScoped
public class ObservabilidadeFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(ObservabilidadeFilter.class);
    private static final String INICIO_PROPERTY = ObservabilidadeFilter.class.getName() + ".inicio";

    @Context
    ResourceInfo resourceInfo;

    private final MeterRegistry registry;
    private final ConcurrentHashMap<String, EndpointMetrics> metricasPorEndpoint = new ConcurrentHashMap<>();

    public ObservabilidadeFilter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(INICIO_PROPERTY, System.nanoTime());
        LOGGER.infof("Requisição recebida: metodo=%s, caminho=%s",
                requestContext.getMethod(), requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        String endpoint = resourceInfo != null && resourceInfo.getResourceMethod() != null
                ? resourceInfo.getResourceMethod().getDeclaringClass().getSimpleName() + "."
                    + resourceInfo.getResourceMethod().getName()
                : "nao_mapeado";
        String metodo = requestContext.getMethod();
        Object inicio = requestContext.getProperty(INICIO_PROPERTY);
        long duracaoNanos = inicio instanceof Long valor ? System.nanoTime() - valor : 0L;
        int status = responseContext.getStatus();

        EndpointMetrics metricas = metricasPorEndpoint.computeIfAbsent(endpoint + "|" + metodo,
                chave -> criarMetricas(endpoint, metodo));
        metricas.requisicoes.increment();
        metricas.tempo.record(duracaoNanos, TimeUnit.NANOSECONDS);
        metricas.total.incrementAndGet();
        metricas.tempoTotalNanos.add(duracaoNanos);
        if (status >= 400) {
            metricas.erros.increment();
            metricas.totalErros.incrementAndGet();
        }

        if (status >= 500) {
            LOGGER.errorf("Requisição concluída com erro: metodo=%s, endpoint=%s, status=%d, duracaoMs=%d",
                    metodo, endpoint, status, TimeUnit.NANOSECONDS.toMillis(duracaoNanos));
        } else if (status >= 400) {
            LOGGER.warnf("Requisição rejeitada: metodo=%s, endpoint=%s, status=%d, duracaoMs=%d",
                    metodo, endpoint, status, TimeUnit.NANOSECONDS.toMillis(duracaoNanos));
        } else {
            LOGGER.infof("Requisição concluída: metodo=%s, endpoint=%s, status=%d, duracaoMs=%d",
                    metodo, endpoint, status, TimeUnit.NANOSECONDS.toMillis(duracaoNanos));
        }
    }

    private EndpointMetrics criarMetricas(String endpoint, String metodo) {
        EndpointMetrics metricas = new EndpointMetrics(
                Counter.builder("api_requisicoes")
                        .description("Quantidade de requisições HTTP por endpoint")
                        .tags("endpoint", endpoint, "metodo", metodo)
                        .register(registry),
                Counter.builder("api_erros")
                        .description("Quantidade de respostas HTTP de erro por endpoint")
                        .tags("endpoint", endpoint, "metodo", metodo)
                        .register(registry),
                Timer.builder("api_tempo_resposta")
                        .description("Tempo de resposta HTTP por endpoint")
                        .tags("endpoint", endpoint, "metodo", metodo)
                        .register(registry));
        Gauge.builder("api_taxa_erros", metricas,
                        dados -> dados.total.get() == 0 ? 0.0 : (double) dados.totalErros.get() / dados.total.get())
                .description("Proporção de respostas HTTP de erro por endpoint")
                .tags("endpoint", endpoint, "metodo", metodo)
                .register(registry);
        Gauge.builder("api_tempo_medio_resposta_seconds", metricas,
                        dados -> dados.total.get() == 0 ? 0.0
                                : (double) dados.tempoTotalNanos.sum() / dados.total.get() / 1_000_000_000)
                .description("Tempo médio de resposta HTTP em segundos por endpoint")
                .tags("endpoint", endpoint, "metodo", metodo)
                .register(registry);
        return metricas;
    }

    private static final class EndpointMetrics {
        private final Counter requisicoes;
        private final Counter erros;
        private final Timer tempo;
        private final AtomicLong total = new AtomicLong();
        private final AtomicLong totalErros = new AtomicLong();
        private final LongAdder tempoTotalNanos = new LongAdder();

        private EndpointMetrics(Counter requisicoes, Counter erros, Timer tempo) {
            this.requisicoes = requisicoes;
            this.erros = erros;
            this.tempo = tempo;
        }
    }
}
