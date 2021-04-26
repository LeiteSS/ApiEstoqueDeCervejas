package io.github.leitess.BeerStockApi.mapper;

import io.github.leitess.BeerStockApi.dto.CervejaDTO;
import io.github.leitess.BeerStockApi.entity.Cerveja;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CervejaMapper {
    CervejaMapper INSTANCE = Mappers.getMapper(CervejaMapper.class);

    Cerveja toModel(CervejaDTO cervejaDTO);

    CervejaDTO toDTO(Cerveja cerveja);
}
