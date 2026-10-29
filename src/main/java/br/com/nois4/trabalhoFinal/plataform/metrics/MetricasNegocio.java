package br.com.nois4.trabalhoFinal.plataform.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricasNegocio {
    private final Counter filmesFavoritosCriados;

    public MetricasNegocio(MeterRegistry registry) {
        filmesFavoritosCriados = Counter.builder("filmes_favoritos_criados")
                .description("Quantidade de filmes favoritos cadastrados com sucesso")
                .register(registry);
    }

    public void registrarFilmeCriado() {
        filmesFavoritosCriados.increment();
    }
}
