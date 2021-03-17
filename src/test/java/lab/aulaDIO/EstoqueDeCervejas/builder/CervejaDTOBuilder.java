package lab.aulaDIO.EstoqueDeCervejas.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.enums.TipoDaCerveja;
import lombok.Builder;

@Builder
public class CervejaDTOBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String nome = "Brahma";

    @Builder.Default
    private String marca = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantidade = 10;

    @Builder.Default
    private TipoDaCerveja tipo = TipoDaCerveja.LAGER;

    public CervejaDTO toBeerDTO() {
        return new CervejaDTO(id,
                nome,
                marca,
                max,
                quantidade,
                tipo);
    }
}