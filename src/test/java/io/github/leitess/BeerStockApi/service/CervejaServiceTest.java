package io.github.leitess.BeerStockApi.service;

import io.github.leitess.BeerStockApi.builder.CervejaDTOBuilder;
import io.github.leitess.BeerStockApi.dto.CervejaDTO;
import io.github.leitess.BeerStockApi.entity.Cerveja;
import io.github.leitess.BeerStockApi.exception.EstoqueExcedeuException;
import io.github.leitess.BeerStockApi.exception.JaExisteException;
import io.github.leitess.BeerStockApi.exception.NaoFoiEncontradoException;
import io.github.leitess.BeerStockApi.mapper.CervejaMapper;
import io.github.leitess.BeerStockApi.repository.CervejaRepository;
import org.junit.jupiter.api.Assertions;
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
        when(cervejaRepository.findByName(cervejaDTO.getName())).thenReturn(Optional.empty());
        when(cervejaRepository.save(cervejaQueEsperaSerSalva)).thenReturn(cervejaQueEsperaSerSalva);

        // then
        CervejaDTO cervejaDTOCriada = cervejaService.create(cervejaDTO);

        assertEquals(cervejaDTO.getId(), cervejaDTOCriada.getId());
        assertEquals(cervejaDTO.getName(), cervejaDTOCriada.getName());
        Assertions.assertEquals(cervejaDTO.getType(), cervejaDTOCriada.getType());
    }

    @Test
    void quandoACervejaInformadaJaExistirLancarUmaExcecao() {
//        given
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaDuplicada = cervejaMapper.toModel(cervejaDTO);
//            when
        when(cervejaRepository.findByName(cervejaDTO.getName())).thenReturn(Optional.of(cervejaDuplicada));
//        then
        assertThrows(JaExisteException.class,() -> cervejaService.create(cervejaDTO)) ;
    }

    @Test
    void quandoDadoUmNomeValidoRetornarACerveja() throws NaoFoiEncontradoException {
//            given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEncontradaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//            when
        when(cervejaRepository.findByName(cervejaDTOEsperada.getName())).thenReturn(Optional.of(cervejaEncontradaEsperada));
//        then
        CervejaDTO cervejaDTOEncontrada = cervejaService.findByName(cervejaDTOEsperada.getName());

        assertEquals(cervejaDTOEsperada, cervejaDTOEncontrada);
    }

    @Test
    void lancarUmaExcecaoQuandoONomeNaoForEncontrado() {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
//        when
        when(cervejaRepository.findByName(cervejaDTOEsperada.getName())).thenReturn(Optional.empty());
//        then
        assertThrows(NaoFoiEncontradoException.class,() -> cervejaService.findByName(cervejaDTOEsperada.getName()));
    }

    @Test
    void quandoChamarAListaDeCervejas() {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEncontradaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//            when
        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(cervejaEncontradaEsperada));
//        then
        List<CervejaDTO> cervejaDTOEncontrada = cervejaService.listAll();

        assertFalse(cervejaDTOEncontrada.isEmpty());
        assertEquals(cervejaDTOEsperada, cervejaDTOEncontrada.get(0));
    }

    @Test
    void quandoAListaChamadaEstiverVazia() {
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<CervejaDTO> cervejaDTOEncontrada = cervejaService.listAll();

        assertTrue(cervejaDTOEncontrada.isEmpty());
    }

    @Test
    void quandoExclusaoForChamadoComUmIdValidoDeveExcluirACervejaDoSistema() throws NaoFoiEncontradoException {
//        given
        CervejaDTO cervejaDTOEperadaQueSejaExcluida = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja expectedExcludedBeer = cervejaMapper.toModel(cervejaDTOEperadaQueSejaExcluida);
//        when
        when(cervejaRepository.findById(cervejaDTOEperadaQueSejaExcluida.getId())).thenReturn(Optional.of(expectedExcludedBeer));
        doNothing().when(cervejaRepository).deleteById(expectedExcludedBeer.getId());
//        then
        cervejaService.deleteById(cervejaDTOEperadaQueSejaExcluida.getId());

        verify(cervejaRepository, times(1)).findById(cervejaDTOEperadaQueSejaExcluida.getId());
        verify(cervejaRepository, times(1)).deleteById(cervejaDTOEperadaQueSejaExcluida.getId());
    }

    @Test
    void quandoForTentadoFazerExclusaoComIdInvalido() {
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(NaoFoiEncontradoException.class,() -> cervejaService.deleteById(ID_INVALIDO));
    }

    @Test
    void quandoForIncrementar() throws NaoFoiEncontradoException, EstoqueExcedeuException {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//            when
        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);
//        then
        int quantidadeParaIncrementar = 10;
        int quantidadeEsperadaDepoisDeIncrementar = cervejaDTOEsperada.getQuantity() + quantidadeParaIncrementar;
        CervejaDTO cervejaDTOIncrementada = cervejaService.increment(cervejaDTOEsperada.getId(), quantidadeParaIncrementar);

        assertThat(quantidadeEsperadaDepoisDeIncrementar, equalTo(cervejaDTOIncrementada.getQuantity()));
        assertThat(quantidadeEsperadaDepoisDeIncrementar, lessThan(cervejaDTOEsperada.getMax()));
    }

    @Test
    void quandoIncrementoForMaiorQueOMaximoPermitido() {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//        when
        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
//        then
        int quantidadeParaIncrementar = 80;
        assertThrows(EstoqueExcedeuException.class, () -> cervejaService.increment(cervejaDTOEsperada.getId(), quantidadeParaIncrementar));
    }

    @Test
    void quandoOIdDadoParaIncrementoForInvalido() {
//        given
        int quantidadeParaIncrementar = 10;
//            when
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());
//        then
        assertThrows(NaoFoiEncontradoException.class, () -> cervejaService.increment(ID_INVALIDO, quantidadeParaIncrementar));
    }

    @Test
    void quandoDecrementar() throws NaoFoiEncontradoException, EstoqueExcedeuException {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//        when
        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);
//            then
        int quantidadeParaDecrementar = 5;
        int quantidadeEsperadaDepoisDeDecrementar = cervejaDTOEsperada.getQuantity() - quantidadeParaDecrementar;
        CervejaDTO cervejaDTODecrementada = cervejaService.decrement(cervejaDTOEsperada.getId(), quantidadeParaDecrementar);

        assertThat(quantidadeEsperadaDepoisDeDecrementar, equalTo(cervejaDTODecrementada.getQuantity()));
        assertThat(quantidadeEsperadaDepoisDeDecrementar, greaterThan(0));
    }

    @Test
    void quandoOEstoqueEstiverVazioParaDecremento() throws NaoFoiEncontradoException, EstoqueExcedeuException {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//            when
        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
        when(cervejaRepository.save(cervejaEsperada)).thenReturn(cervejaEsperada);

        int quantidadeParaDecremento = 10;
        int quantidadaEsperadaDepoisDoDecremento = cervejaDTOEsperada.getQuantity() - quantidadeParaDecremento;
        CervejaDTO cervejaDTODecrementada = cervejaService.decrement(cervejaDTOEsperada.getId(), quantidadeParaDecremento);
//        then
        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(0));
        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(cervejaDTODecrementada.getQuantity()));
    }

    @Test
    void quandoDecrementoForMenorQueZero() {
//        given
        CervejaDTO cervejaDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Cerveja cervejaEsperada = cervejaMapper.toModel(cervejaDTOEsperada);
//            when
        when(cervejaRepository.findById(cervejaDTOEsperada.getId())).thenReturn(Optional.of(cervejaEsperada));
//        then
        int quantidadeParaDecremento = 80;
        assertThrows(EstoqueExcedeuException.class, () -> cervejaService.decrement(cervejaDTOEsperada.getId(), quantidadeParaDecremento));
    }

    @Test
    void quandoOIDDadoParaDecrementoEstiverInvalido() {
//        given
        int quantidadeParaDecremento = 10;
//        when
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());
//        then
        assertThrows(NaoFoiEncontradoException.class, () -> cervejaService.decrement(ID_INVALIDO, quantidadeParaDecremento));
    }
}
