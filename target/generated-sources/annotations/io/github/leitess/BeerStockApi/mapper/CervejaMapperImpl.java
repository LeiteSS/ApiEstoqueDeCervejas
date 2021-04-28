package io.github.leitess.BeerStockApi.mapper;

import io.github.leitess.BeerStockApi.entity.Beer;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-04-26T16:54:41-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 16.0.1 (Oracle Corporation)"
)
public class CervejaMapperImpl implements CervejaMapper {

    @Override
    public Beer toModel(BeerDTO beerDTO) {
        if ( beerDTO == null ) {
            return null;
        }

        Beer beer = new Beer();

        beer.setId( beerDTO.getId() );
        beer.setName( beerDTO.getName() );
        beer.setBrand( beerDTO.getBrand() );
        if ( beerDTO.getMax() != null ) {
            beer.setMax( beerDTO.getMax() );
        }
        if ( beerDTO.getQuantity() != null ) {
            beer.setQuantity( beerDTO.getQuantity() );
        }
        beer.setType( beerDTO.getType() );

        return beer;
    }

    @Override
    public BeerDTO toDTO(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDTO beerDTO = new BeerDTO();

        beerDTO.setId( beer.getId() );
        beerDTO.setName( beer.getName() );
        beerDTO.setBrand( beer.getBrand() );
        beerDTO.setMax( beer.getMax() );
        beerDTO.setQuantity( beer.getQuantity() );
        beerDTO.setType( beer.getType() );

        return beerDTO;
    }
}
