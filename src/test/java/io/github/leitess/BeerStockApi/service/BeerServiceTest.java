package io.github.leitess.BeerStockApi.service;

import io.github.leitess.BeerStockApi.builder.CervejaDTOBuilder;
import io.github.leitess.BeerStockApi.entity.Beer;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.exception.StockExceededException;
import io.github.leitess.BeerStockApi.exception.AlreadyExistException;
import io.github.leitess.BeerStockApi.exception.NotFoundException;
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
public class BeerServiceTest {
    private static final long ID_INVALIDO = 1L;

    @Mock
    private CervejaRepository cervejaRepository;

    private CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    @InjectMocks
    private CervejaService cervejaService;

    @Test
    void quandoUmaNovaCervejaForInformadaDeveraSerCriada() throws AlreadyExistException {
        // given
        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerQueEsperaSerSalva = cervejaMapper.toModel(beerDTO);

        // when
        when(cervejaRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());
        when(cervejaRepository.save(beerQueEsperaSerSalva)).thenReturn(beerQueEsperaSerSalva);

        // then
        BeerDTO beerDTOCriada = cervejaService.create(beerDTO);

        assertEquals(beerDTO.getId(), beerDTOCriada.getId());
        assertEquals(beerDTO.getName(), beerDTOCriada.getName());
        Assertions.assertEquals(beerDTO.getType(), beerDTOCriada.getType());
    }

    @Test
    void quandoACervejaInformadaJaExistirLancarUmaExcecao() {
//        given
        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerDuplicada = cervejaMapper.toModel(beerDTO);
//            when
        when(cervejaRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(beerDuplicada));
//        then
        assertThrows(AlreadyExistException.class,() -> cervejaService.create(beerDTO)) ;
    }

    @Test
    void quandoDadoUmNomeValidoRetornarACerveja() throws NotFoundException {
//            given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEncontradaEsperada = cervejaMapper.toModel(beerDTOEsperada);
//            when
        when(cervejaRepository.findByName(beerDTOEsperada.getName())).thenReturn(Optional.of(beerEncontradaEsperada));
//        then
        BeerDTO beerDTOEncontrada = cervejaService.findByName(beerDTOEsperada.getName());

        assertEquals(beerDTOEsperada, beerDTOEncontrada);
    }

    @Test
    void lancarUmaExcecaoQuandoONomeNaoForEncontrado() {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
//        when
        when(cervejaRepository.findByName(beerDTOEsperada.getName())).thenReturn(Optional.empty());
//        then
        assertThrows(NotFoundException.class,() -> cervejaService.findByName(beerDTOEsperada.getName()));
    }

    @Test
    void quandoChamarAListaDeCervejas() {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEncontradaEsperada = cervejaMapper.toModel(beerDTOEsperada);
//            when
        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(beerEncontradaEsperada));
//        then
        List<BeerDTO> beerDTOEncontrada = cervejaService.listAll();

        assertFalse(beerDTOEncontrada.isEmpty());
        assertEquals(beerDTOEsperada, beerDTOEncontrada.get(0));
    }

    @Test
    void quandoAListaChamadaEstiverVazia() {
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<BeerDTO> beerDTOEncontrada = cervejaService.listAll();

        assertTrue(beerDTOEncontrada.isEmpty());
    }

    @Test
    void quandoExclusaoForChamadoComUmIdValidoDeveExcluirACervejaDoSistema() throws NotFoundException {
//        given
        BeerDTO beerDTOEperadaQueSejaExcluida = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer expectedExcludedBeer = cervejaMapper.toModel(beerDTOEperadaQueSejaExcluida);
//        when
        when(cervejaRepository.findById(beerDTOEperadaQueSejaExcluida.getId())).thenReturn(Optional.of(expectedExcludedBeer));
        doNothing().when(cervejaRepository).deleteById(expectedExcludedBeer.getId());
//        then
        cervejaService.deleteById(beerDTOEperadaQueSejaExcluida.getId());

        verify(cervejaRepository, times(1)).findById(beerDTOEperadaQueSejaExcluida.getId());
        verify(cervejaRepository, times(1)).deleteById(beerDTOEperadaQueSejaExcluida.getId());
    }

    @Test
    void quandoForTentadoFazerExclusaoComIdInvalido() {
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> cervejaService.deleteById(ID_INVALIDO));
    }

    @Test
    void quandoForIncrementar() throws NotFoundException, StockExceededException {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEsperada = cervejaMapper.toModel(beerDTOEsperada);
//            when
        when(cervejaRepository.findById(beerDTOEsperada.getId())).thenReturn(Optional.of(beerEsperada));
        when(cervejaRepository.save(beerEsperada)).thenReturn(beerEsperada);
//        then
        int quantidadeParaIncrementar = 10;
        int quantidadeEsperadaDepoisDeIncrementar = beerDTOEsperada.getQuantity() + quantidadeParaIncrementar;
        BeerDTO beerDTOIncrementada = cervejaService.increment(beerDTOEsperada.getId(), quantidadeParaIncrementar);

        assertThat(quantidadeEsperadaDepoisDeIncrementar, equalTo(beerDTOIncrementada.getQuantity()));
        assertThat(quantidadeEsperadaDepoisDeIncrementar, lessThan(beerDTOEsperada.getMax()));
    }

    @Test
    void quandoIncrementoForMaiorQueOMaximoPermitido() {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEsperada = cervejaMapper.toModel(beerDTOEsperada);
//        when
        when(cervejaRepository.findById(beerDTOEsperada.getId())).thenReturn(Optional.of(beerEsperada));
//        then
        int quantidadeParaIncrementar = 80;
        assertThrows(StockExceededException.class, () -> cervejaService.increment(beerDTOEsperada.getId(), quantidadeParaIncrementar));
    }

    @Test
    void quandoOIdDadoParaIncrementoForInvalido() {
//        given
        int quantidadeParaIncrementar = 10;
//            when
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());
//        then
        assertThrows(NotFoundException.class, () -> cervejaService.increment(ID_INVALIDO, quantidadeParaIncrementar));
    }

    @Test
    void quandoDecrementar() throws NotFoundException, StockExceededException {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEsperada = cervejaMapper.toModel(beerDTOEsperada);
//        when
        when(cervejaRepository.findById(beerDTOEsperada.getId())).thenReturn(Optional.of(beerEsperada));
        when(cervejaRepository.save(beerEsperada)).thenReturn(beerEsperada);
//            then
        int quantidadeParaDecrementar = 5;
        int quantidadeEsperadaDepoisDeDecrementar = beerDTOEsperada.getQuantity() - quantidadeParaDecrementar;
        BeerDTO beerDTODecrementada = cervejaService.decrement(beerDTOEsperada.getId(), quantidadeParaDecrementar);

        assertThat(quantidadeEsperadaDepoisDeDecrementar, equalTo(beerDTODecrementada.getQuantity()));
        assertThat(quantidadeEsperadaDepoisDeDecrementar, greaterThan(0));
    }

    @Test
    void quandoOEstoqueEstiverVazioParaDecremento() throws NotFoundException, StockExceededException {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEsperada = cervejaMapper.toModel(beerDTOEsperada);
//            when
        when(cervejaRepository.findById(beerDTOEsperada.getId())).thenReturn(Optional.of(beerEsperada));
        when(cervejaRepository.save(beerEsperada)).thenReturn(beerEsperada);

        int quantidadeParaDecremento = 10;
        int quantidadaEsperadaDepoisDoDecremento = beerDTOEsperada.getQuantity() - quantidadeParaDecremento;
        BeerDTO beerDTODecrementada = cervejaService.decrement(beerDTOEsperada.getId(), quantidadeParaDecremento);
//        then
        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(0));
        assertThat(quantidadaEsperadaDepoisDoDecremento, equalTo(beerDTODecrementada.getQuantity()));
    }

    @Test
    void quandoDecrementoForMenorQueZero() {
//        given
        BeerDTO beerDTOEsperada = CervejaDTOBuilder.builder().build().toBeerDTO();
        Beer beerEsperada = cervejaMapper.toModel(beerDTOEsperada);
//            when
        when(cervejaRepository.findById(beerDTOEsperada.getId())).thenReturn(Optional.of(beerEsperada));
//        then
        int quantidadeParaDecremento = 80;
        assertThrows(StockExceededException.class, () -> cervejaService.decrement(beerDTOEsperada.getId(), quantidadeParaDecremento));
    }

    @Test
    void quandoOIDDadoParaDecrementoEstiverInvalido() {
//        given
        int quantidadeParaDecremento = 10;
//        when
        when(cervejaRepository.findById(ID_INVALIDO)).thenReturn(Optional.empty());
//        then
        assertThrows(NotFoundException.class, () -> cervejaService.decrement(ID_INVALIDO, quantidadeParaDecremento));
    }
}
