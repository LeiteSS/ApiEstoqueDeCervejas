package lab.aulaDIO.EstoqueDeCervejas.service;

import lab.aulaDIO.EstoqueDeCervejas.builder.CervejaDTOBuilder;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.entity.Cerveja;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;
import lab.aulaDIO.EstoqueDeCervejas.mapper.CervejaMapper;
import lab.aulaDIO.EstoqueDeCervejas.repository.CervejaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CervejaServiceTest {
    @Mock
    private CervejaRepository cervejaRepository;

    private CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    @InjectMocks
    private CervejaService cervejaService;

    @Test
    void quandoUmaNovaCervejaForInformadaDeveraSerCriada() throws JaExisteException {
        // given
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaQueEsperaSerSalva = cervejaMapper.toModel(cervejaDTO);

        // when
        when(cervejaRepository.procurarPeloNome(cervejaDTO.getName())).thenReturn(Optional.empty());
        when(cervejaRepository.save(cervejaQueEsperaSerSalva)).thenReturn(cervejaQueEsperaSerSalva);

        // then
        CervejaDTO cervejaDTOCriada = cervejaService.registrarCerveja(cervejaDTO);

        assertEquals(cervejaDTO.getId(), cervejaDTOCriada.getId());
        assertEquals(cervejaDTO.getName(), cervejaDTOCriada.getName());
        assertEquals(cervejaDTO.getType(), cervejaDTOCriada.getType());
    }

    @Test
    void quandoACervejaInformadaJaExistirLancarUmaExcecao() {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaDuplicada = cervejaMapper.toModel(cervejaDTO);

        when(cervejaRepository.procurarPeloNome(cervejaDTO.getName())).thenReturn(Optional.of(cervejaDuplicada));

        assertThrows(JaExisteException.class,() -> cervejaService.registrarCerveja(cervejaDTO)) ;
    }
}
