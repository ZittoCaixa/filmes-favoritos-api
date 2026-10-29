package br.com.nois4.trabalhoFinal.filmesFavoritos.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class FilmeFavoritoRepository implements PanacheRepository<FilmeFavoritoEntity> {
    public Optional<FilmeFavoritoEntity> findByTituloNormalizado(String tituloNormalizado) {
        return find("tituloNormalizado", tituloNormalizado).firstResultOptional();
    }
}
