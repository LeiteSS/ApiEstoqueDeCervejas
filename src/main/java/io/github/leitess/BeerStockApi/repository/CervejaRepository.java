package io.github.leitess.BeerStockApi.repository;

import io.github.leitess.BeerStockApi.entity.Cerveja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CervejaRepository extends JpaRepository<Cerveja, Long> {

    Optional<Cerveja> findByName(String name);

}
