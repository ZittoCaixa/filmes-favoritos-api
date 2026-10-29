package br.com.nois4.trabalhoFinal.plataform.health;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.sql.SQLException;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
    private final ReadinessState state;
    private final AgroalDataSource dataSource;

    public ReadinessCheck(ReadinessState state, AgroalDataSource dataSource) {
        this.state = state;
        this.dataSource = dataSource;
    }

    @Override
    public HealthCheckResponse call() {
        if (!state.isPronta()) {
            return HealthCheckResponse.named("filmes-favoritos-pronto")
                    .down().withData("motivo", "Readiness desabilitada manualmente.").build();
        }
        try (var connection = dataSource.getConnection()) {
            boolean conexaoValida = connection.isValid(1);
            return HealthCheckResponse.named("filmes-favoritos-pronto")
                    .status(conexaoValida)
                    .withData("banco", conexaoValida ? "conectado" : "indisponivel")
                    .build();
        } catch (SQLException exception) {
            return HealthCheckResponse.named("filmes-favoritos-pronto")
                    .down().withData("banco", "indisponivel").build();
        }
    }
}
