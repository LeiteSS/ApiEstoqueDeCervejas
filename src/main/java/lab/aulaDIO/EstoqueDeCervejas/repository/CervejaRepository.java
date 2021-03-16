package lab.aulaDIO.EstoqueDeCervejas.repository;

import lab.aulaDIO.EstoqueDeCervejas.entity.Cerveja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CervejaRepository extends JpaRepository<Cerveja, Long> {

    Optional<Cerveja> procurarPeloNome(String nome);

}
