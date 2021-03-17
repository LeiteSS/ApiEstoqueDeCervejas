package lab.aulaDIO.EstoqueDeCervejas.service;

import lab.aulaDIO.EstoqueDeCervejas.builder.CervejaDTOBuilder;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.entity.Cerveja;
import lab.aulaDIO.EstoqueDeCervejas.exception.EstoqueExcedeuException;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;
import lab.aulaDIO.EstoqueDeCervejas.exception.NaoFoiEncontradoException;
import lab.aulaDIO.EstoqueDeCervejas.mapper.CervejaMapper;
import lab.aulaDIO.EstoqueDeCervejas.repository.CervejaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CervejaServiceTest {
    private static final long ID_INVALIDO = 1L;

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
        when(cervejaRepository.procurarPeloNome(cervejaDTO.getNome())).thenReturn(Optional.empty());
        when(cervejaRepository.save(cervejaQueEsperaSerSalva)).thenReturn(cervejaQueEsperaSerSalva);

        // then
        CervejaDTO cervejaDTOCriada = cervejaService.registrarCerveja(cervejaDTO);

        assertEquals(cervejaDTO.getId(), cervejaDTOCriada.getId());
        assertEquals(cervejaDTO.getNome(), cervejaDTOCriada.getNome());
        assertEquals(cervejaDTO.getTipo(), cervejaDTOCriada.getTipo());
    }

    @Test
    void quandoACervejaInformadaJaExistirLancarUmaExcecao() {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaDuplicada = cervejaMapper.toModel(cervejaDTO);

        when(cervejaRepository.procurarPeloNome(cervejaDTO.getNome())).thenReturn(Optional.of(cervejaDuplicada));

        assertThrows(JaExisteException.class,() -> cervejaService.registrarCerveja(cervejaDTO)) ;
    }

    @Test
    void quandoDadoUmNomeValidoRetornarACerveja() throws NaoFoiEncontradoException {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEncontradaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.procurarPeloNome(cervejaDTOEsperada.getNome())).thenReturn(Optional.of(cervejaEncontradaEsperada));

        CervejaDTO foundBeerDTO = cervejaService.procurarPeloNome(cervejaDTOEsperada.getNome());

        assertEquals(cervejaDTOEsperada, foundBeerDTO);
    }

    @Test
    void lancarUmaExcecaoQuandoONomeNaoForEncontrado() {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();

        when(cervejaRepository.procurarPeloNome(cervejaDTOEsperada.getNome())).thenReturn(Optional.empty());

        assertThrows(NaoFoiEncontradoException.class,() -> cervejaService.procurarPeloNome(cervejaDTOEsperada.getNome()));
    }

    @Test
    void quandoChamarAListaDeCervejas() {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEncontradaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(cervejaEncontradaEsperada));

        List<CervejaDTO> cervejaDTOEncontrada = cervejaService.listarCervejas();

        assertFalse(cervejaDTOEncontrada.isEmpty());
        assertEquals(cervejaDTOEsperada, cervejaDTOEncontrada.get(0));
    }

    @Test
    void quandoAListaChamadaEstiverVazia() {
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<CervejaDTO> cervejaDTOEncontrada = cervejaService.listarCervejas();

        assertTrue(cervejaDTOEncontrada.isEmpty());
    }

    @Test
    void quandoExclusaoForChamadoComUmIdValidoDeveExcluirACervejaDoSistema() throws NaoFoiEncontradoException {
        CervejaDTO cervejaDTOEperadaQueSejaExcluida = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja expectedExcludedBeer = cervejaMapper.toModel(cervejaDTOEperadaQueSejaExcluida);

        when(cervejaRepository.findById(cervejaDTOEperadaQueSejaExcluida.getId())).thenReturn(Optional.of(expectedExcludedBeer));
        doNothing().when(cervejaRepository).deleteById(expectedExcludedBeer.getId());

        cervejaService.exclusaoPeloId(cervejaDTOEperadaQueSejaExcluida.getId());

        verify(cervejaRepository, times(1)).findById(cervejaDTOEperadaQueSejaExcluida.getId());
        verify(cervejaRepository, times(1)).deleteById(cervejaDTOEperadaQueSejaExcluida.getId());
    }

    @Test
    void quandoForTentadoFazerExclusaoComIdInvalido() {
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(NaoFoiEncontradoException.class,() -> cervejaService.exclusaoPeloId(ID_INVALIDO));
    }

    @Test
    void quandoForIncrementar() throws NaoFoiEncontradoException, EstoqueExcedeuException {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeParaIncrementar = 10;
        int quantidadeEsperadaDepoisDeIncrementar = cervejaDTOEsperada.getQuantidade() + quantidadeParaIncrementar;
        CervejaDTO cervejaDTOIncrementada = cervejaService.incrementoDoEstoque(cervejaDTOEsperada.getId(), quantidadeParaIncrementar);

        assertThat(quantidadeEsperadaDepoisDeIncrementar, equalTo(cervejaDTOIncrementada.getQuantidade()));
        assertThat(quantidadeEsperadaDepoisDeIncrementar, lessThan(cervejaDTOEsperada.getMax()));
    }

    @Test
    void quandoIncrementoForMaiorQueOMaximoPermitido() {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));

        int quantidadeParaIncrementar = 80;
        assertThrows(EstoqueExcedeuException.class, () -> cervejaService.incrementoDoEstoque(cervejaDTOEsperada.getId(), quantidadeParaIncrementar));
    }

    @Test
    void quandoOIdDadoParaIncrementoForInvalido() {
        int quantidadeParaIncrementar = 10;

        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(NaoFoiEncontradoException.class, () -> cervejaService.incrementoDoEstoque(ID_INVALIDO, quantidadeParaIncrementar));
    }

    @Test
    void quandoDecrementar() throws NaoFoiEncontradoException, EstoqueExcedeuException {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeParaDecrementar = 5;
        int quantidadeEsperadaDepoisDeDecrementar = cervejaDTOEsperada.getQuantidade() - quantidadeParaDecrementar;
        CervejaDTO cervejaDTODecrementada = cervejaService.decrementaNoEstoque(cervejaDTOEsperada.getId(), quantidadeParaDecrementar);

        assertThat(quantidadeEsperadaDepoisDeDecrementar, equalTo(cervejaDTODecrementada.getQuantidade()));
        assertThat(quantidadeEsperadaDepoisDeDecrementar, greaterThan(0));
    }

    @Test
    void quandoOEstoqueEstiverVazioParaDecremento() throws NaoFoiEncontradoException, EstoqueExcedeuException {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeParaDecremento = 10;
        int quantidadaEsperadaDepoisDoDecremento = cervejaDTOEsperada.getQuantidade() - quantidadeParaDecremento;
        CervejaDTO cervejaDTODecrementada = cervejaService.decrementaNoEstoque(cervejaDTOEsperada.getId(), quantidadeParaDecremento);

        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(0));
        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(cervejaDTODecrementada.getQuantidade()));
    }

    @Test
    void quandoDecrementoForMenorQueZero() {
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);

        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));

        int quantidadeParaDecremento = 80;
        assertThrows(EstoqueExcedeuException.class, () -> cervejaService.decrementaNoEstoque(cervejaDTOEsperada.getId(), quantidadeParaDecremento));
    }

    @Test
    void quandoOIDDadoParaDecrementoEstiverInvalido() {
        int quantidadeParaDecremento = 10;

        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(NaoFoiEncontradoException.class, () -> cervejaService.decrementaNoEstoque(ID_INVALIDO, quantidadeParaDecremento));
    }
}
