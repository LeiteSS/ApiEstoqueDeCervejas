package io.github.leitess.BeerStockApi.controller;

import io.github.leitess.BeerStockApi.dto.QuantidadeDTO;
import io.github.leitess.BeerStockApi.exception.EstoqueExcedeuException;
import io.github.leitess.BeerStockApi.exception.NaoFoiEncontradoException;
import io.github.leitess.BeerStockApi.service.CervejaService;
import io.github.leitess.BeerStockApi.builder.CervejaDTOBuilder;
import io.github.leitess.BeerStockApi.dto.CervejaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static io.github.leitess.BeerStockApi.builder.CervejaDTOBuilder.*;
import static io.github.leitess.BeerStockApi.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CervejaControllerTest {
    private static final String URL_PATH = "/api/v1/cervejas";
    private static final long ID_VALIDO = 1L;
    private static final long ID_INVALIDO = 2l;
    private static final String SUBPATH_INCREMENTO = "/incremento";
    private static final String SUBPATH_DECREMENTO = "/decremento";

    private MockMvc mockMvc;

    @Mock
    private CervejaService cervejaService;

    @InjectMocks
    private CervejaController cervejaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cervejaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void quandoPOSTRegistrarUmaCerveja() throws Exception {
//        given
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
//        when
        when(cervejaService.create(cervejaDTO)).thenReturn(cervejaDTO);
//        then
        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cervejaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getType().toString())));
    }

    @Test
    void quandoPOSTRegistrarComUmCampoEmBranco() throws Exception {
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
        cervejaDTO.setName(null);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cervejaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quandoGETComOnameValidoRetornOKStatus() throws Exception {
//        given
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
//        when
        when(cervejaService.findByName(cervejaDTO.getName())).thenReturn(cervejaDTO);
//        then
        mockMvc.perform(get(URL_PATH + "/" + cervejaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getType().toString())));
    }

    @Test
    void quandoGETForChamadoComnameNaoCadastrado() throws Exception {
//        given
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
//      when
        when(cervejaService.findByName(cervejaDTO.getName())).thenThrow(NaoFoiEncontradoException.class);
//        then
        mockMvc.perform(get(URL_PATH + "/" + cervejaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoGETForChamadoParaListar() throws Exception {
//        given
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
//      when
        when(cervejaService.listAll()).thenReturn(Collections.singletonList(cervejaDTO));
//        then
        mockMvc.perform(get(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(cervejaDTO.getType().toString())));
    }

    @Test
    void quandoGETForChamadoParaListarMasEstiverVazio() throws Exception {
        when(cervejaService.listAll()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void quandoOIdForValidoParaExclusao() throws Exception {
//        given
        doNothing().when(cervejaService).deleteById(ID_INVALIDO);
//        when
        mockMvc.perform(delete(URL_PATH + "/" + ID_VALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
//        then
        verify(cervejaService, times(1)).deleteById(ID_VALIDO);
    }

    @Test
    void quandoOIdForInvalidoParaExclusao() throws Exception {
//        given && when
        doThrow(NaoFoiEncontradoException.class).when(cervejaService).deleteById(ID_INVALIDO);
//        then
        mockMvc.perform(delete(URL_PATH + "/" + ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHParaIncrementarForChamadoRetornarOKStatus() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(10)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setQuantity(cervejaDTO.getQuantity() + quantidadeDTO.getQuantity());
//      when
        when(cervejaService.increment(ID_VALIDO, quantidadeDTO.getQuantity())).thenReturn(cervejaDTO);
//        then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(cervejaDTO.getQuantity())));

    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComValorMaiorQueOMaximoPermitido() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(30)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setQuantity(cervejaDTO.getQuantity() + quantidadeDTO.getQuantity());
//        when
        when(cervejaService.increment(ID_VALIDO, quantidadeDTO.getQuantity())).thenThrow(EstoqueExcedeuException.class);
//            then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComOIDInvalido() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(30)
                .build();
//         when
        when(cervejaService.increment(ID_INVALIDO, quantidadeDTO.getQuantity())).thenThrow(NaoFoiEncontradoException.class);
//        then
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHForChamadoParaDecremento() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(5)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setQuantity(cervejaDTO.getQuantity() + quantidadeDTO.getQuantity());
//      when
        when(cervejaService.decrement(ID_VALIDO, quantidadeDTO.getQuantity())).thenReturn(cervejaDTO);
//      then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(cervejaDTO.getQuantity())));
    }

    @Test
    void quandoPATCHForChamadoParaDecrementoMenorQueZero() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(60)
                .build();

        CervejaDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantidadeDTO.getQuantity());
//         when
        when(cervejaService.decrement(ID_VALIDO, quantidadeDTO.getQuantity())).thenThrow(EstoqueExcedeuException.class);
//          then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaDecrementarCervejaComIDInvalido() throws Exception {
        // given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantity(5)
                .build();
        // when
        when(cervejaService.decrement(ID_INVALIDO, quantidadeDTO.getQuantity())).thenThrow(NaoFoiEncontradoException.class);
        // then
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }
}
