package br.com.nois4.trabalhoFinal.filmesFavoritos.domain;

import br.com.nois4.trabalhoFinal.filmesFavoritos.boundary.FilmeFavoritoRequest;
import br.com.nois4.trabalhoFinal.filmesFavoritos.repository.FilmeFavoritoEntity;
import br.com.nois4.trabalhoFinal.filmesFavoritos.repository.FilmeFavoritoRepository;
import br.com.nois4.trabalhoFinal.plataform.metrics.MetricasNegocio;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class FilmeFavoritoService {
    private static final Logger LOGGER = Logger.getLogger(FilmeFavoritoService.class);
    private final FilmeFavoritoRepository repository;
    private final MetricasNegocio metricas;

    public FilmeFavoritoService(FilmeFavoritoRepository repository, MetricasNegocio metricas) {
        this.repository = repository;
        this.metricas = metricas;
    }

    public List<FilmeFavoritoEntity> listar() {
        try {
            PanacheQuery<FilmeFavoritoEntity> query = repository.findAll(Sort.by("id"));
            List<FilmeFavoritoEntity> filmes = query.list();
            LOGGER.debugf("Consulta de filmes favoritos concluída: %d registro(s).", filmes.size());
            return filmes;
        } catch (PersistenceException exception) {
            LOGGER.error("Falha ao consultar filmes favoritos.", exception);
            throw new ErroNaConsultaException("Não foi possível consultar os filmes favoritos.", exception);
        }
    }

    public FilmeFavoritoEntity buscar(Long id) {
        try {
            return repository.findByIdOptional(id).orElseThrow(() -> new FilmeNaoEncontradoException(id));
        } catch (PersistenceException exception) {
            LOGGER.errorf(exception, "Falha ao consultar filme favorito com id %d.", id);
            throw new ErroNaConsultaException("Não foi possível consultar o filme favorito.", exception);
        }
    }

    @Transactional
    public FilmeFavoritoEntity criar(FilmeFavoritoRequest request) {
        String tituloNormalizado = normalizarTitulo(request.titulo());
        if (repository.findByTituloNormalizado(tituloNormalizado).isPresent()) {
            throw new FilmeDuplicadoException(request.titulo().trim());
        }
        FilmeFavoritoEntity entity = new FilmeFavoritoEntity();
        entity.titulo = request.titulo().trim();
        entity.tituloNormalizado = tituloNormalizado;
        entity.diretor = request.diretor().trim();
        entity.ano = request.ano();
        entity.genero = request.genero().trim();
        entity.criadoEm = Instant.now();
        repository.persist(entity);
        metricas.registrarFilmeCriado();
        LOGGER.infof("Filme favorito criado: id=%d, titulo='%s'.", entity.id, entity.titulo);
        return entity;
    }

    @Transactional
    public FilmeFavoritoEntity atualizar(Long id, FilmeFavoritoRequest request) {
        FilmeFavoritoEntity entity = buscar(id);
        String tituloNormalizado = normalizarTitulo(request.titulo());
        repository.findByTituloNormalizado(tituloNormalizado)
                .filter(outro -> !outro.id.equals(id))
                .ifPresent(outro -> {
                    throw new FilmeDuplicadoException(request.titulo().trim());
                });
        entity.titulo = request.titulo().trim();
        entity.tituloNormalizado = tituloNormalizado;
        entity.diretor = request.diretor().trim();
        entity.ano = request.ano();
        entity.genero = request.genero().trim();
        LOGGER.infof("Filme favorito atualizado: id=%d.", entity.id);
        return entity;
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.deleteById(id)) {
            throw new FilmeNaoEncontradoException(id);
        }
        LOGGER.infof("Filme favorito excluído: id=%d.", id);
    }

    private static String normalizarTitulo(String titulo) {
        String semAcentos = Normalizer.normalize(titulo.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }
}
