package io.github.leitess.BeerStockApi.mapper;

import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CervejaMapper {
    CervejaMapper INSTANCE = Mappers.getMapper(CervejaMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}
