package io.github.leitess.BeerStockApi.resource;

import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.resource.dto.QuantityDTO;
import io.github.leitess.BeerStockApi.exception.StockExceededException;
import io.github.leitess.BeerStockApi.exception.NotFoundException;
import io.github.leitess.BeerStockApi.service.CervejaService;
import io.github.leitess.BeerStockApi.builder.CervejaDTOBuilder;
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
public class BeerResourceTest {
    private static final String URL_PATH = "/api/v1/cervejas";
    private static final long ID_VALIDO = 1L;
    private static final long ID_INVALIDO = 2l;
    private static final String SUBPATH_INCREMENTO = "/incremento";
    private static final String SUBPATH_DECREMENTO = "/decremento";

    private MockMvc mockMvc;

    @Mock
    private CervejaService cervejaService;

    @InjectMocks
    private CervejaResource cervejaResource;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cervejaResource)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void quandoPOSTRegistrarUmaCerveja() throws Exception {
//        given
        BeerDTO beerDTO = builder().build().toBeerDTO();
//        when
        when(cervejaService.create(beerDTO)).thenReturn(beerDTO);
//        then
        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void quandoPOSTRegistrarComUmCampoEmBranco() throws Exception {
        BeerDTO beerDTO = builder().build().toBeerDTO();
        beerDTO.setName(null);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quandoGETComOnameValidoRetornOKStatus() throws Exception {
//        given
        BeerDTO beerDTO = builder().build().toBeerDTO();
//        when
        when(cervejaService.findByName(beerDTO.getName())).thenReturn(beerDTO);
//        then
        mockMvc.perform(get(URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void quandoGETForChamadoComnameNaoCadastrado() throws Exception {
//        given
        BeerDTO beerDTO = builder().build().toBeerDTO();
//      when
        when(cervejaService.findByName(beerDTO.getName())).thenThrow(NotFoundException.class);
//        then
        mockMvc.perform(get(URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoGETForChamadoParaListar() throws Exception {
//        given
        BeerDTO beerDTO = builder().build().toBeerDTO();
//      when
        when(cervejaService.listAll()).thenReturn(Collections.singletonList(beerDTO));
//        then
        mockMvc.perform(get(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
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
        doThrow(NotFoundException.class).when(cervejaService).deleteById(ID_INVALIDO);
//        then
        mockMvc.perform(delete(URL_PATH + "/" + ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHParaIncrementarForChamadoRetornarOKStatus() throws Exception {
//        given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//      when
        when(cervejaService.increment(ID_VALIDO, quantityDTO.getQuantity())).thenReturn(beerDTO);
//        then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));

    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComValorMaiorQueOMaximoPermitido() throws Exception {
//        given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//        when
        when(cervejaService.increment(ID_VALIDO, quantityDTO.getQuantity())).thenThrow(StockExceededException.class);
//            then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComOIDInvalido() throws Exception {
//        given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();
//         when
        when(cervejaService.increment(ID_INVALIDO, quantityDTO.getQuantity())).thenThrow(NotFoundException.class);
//        then
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHForChamadoParaDecremento() throws Exception {
//        given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//      when
        when(cervejaService.decrement(ID_VALIDO, quantityDTO.getQuantity())).thenReturn(beerDTO);
//      then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

    @Test
    void quandoPATCHForChamadoParaDecrementoMenorQueZero() throws Exception {
//        given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        BeerDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//         when
        when(cervejaService.decrement(ID_VALIDO, quantityDTO.getQuantity())).thenThrow(StockExceededException.class);
//          then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaDecrementarCervejaComIDInvalido() throws Exception {
        // given
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();
        // when
        when(cervejaService.decrement(ID_INVALIDO, quantityDTO.getQuantity())).thenThrow(NotFoundException.class);
        // then
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}
