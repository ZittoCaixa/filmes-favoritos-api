package br.com.nois4.trabalhoFinal.plataform.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class ReadinessState {
    private final AtomicBoolean pronta;

    public ReadinessState(@ConfigProperty(name = "app.readiness.inicialmente-pronta") boolean inicialmentePronta) {
        pronta = new AtomicBoolean(inicialmentePronta);
    }

    public boolean isPronta() {
        return pronta.get();
    }

    public void setPronta(boolean valor) {
        pronta.set(valor);
    }
}
