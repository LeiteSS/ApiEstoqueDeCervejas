package io.github.leitess.BeerStockApi.builder;

import io.github.leitess.BeerStockApi.dto.CervejaDTO;
import io.github.leitess.BeerStockApi.enums.TipoDaCerveja;
import lombok.Builder;

@Builder
public class CervejaDTOBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private TipoDaCerveja type = TipoDaCerveja.LAGER;

    public CervejaDTO toBeerDTO() {
        return new CervejaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
