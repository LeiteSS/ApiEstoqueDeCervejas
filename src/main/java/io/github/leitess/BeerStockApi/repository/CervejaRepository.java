package io.github.leitess.BeerStockApi.repository;

import io.github.leitess.BeerStockApi.entity.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CervejaRepository extends JpaRepository<Beer, Long> {

    Optional<Beer> findByName(String name);

}
