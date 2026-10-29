package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class MockServicoState {
    private final AtomicBoolean disponivel = new AtomicBoolean(true);

    public boolean isDisponivel() {
        return disponivel.get();
    }

    public void setDisponivel(boolean valor) {
        disponivel.set(valor);
    }
}
