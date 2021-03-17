package lab.aulaDIO.EstoqueDeCervejas.controller;

import lab.aulaDIO.EstoqueDeCervejas.builder.CervejaDTOBuilder;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.dto.QuantidadeDTO;
import lab.aulaDIO.EstoqueDeCervejas.entity.Cerveja;
import lab.aulaDIO.EstoqueDeCervejas.exception.EstoqueExcedeuException;
import lab.aulaDIO.EstoqueDeCervejas.exception.NaoFoiEncontradoException;
import lab.aulaDIO.EstoqueDeCervejas.mapper.CervejaMapper;
import lab.aulaDIO.EstoqueDeCervejas.service.CervejaService;
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
import java.util.Optional;

import static lab.aulaDIO.EstoqueDeCervejas.builder.CervejaDTOBuilder.*;
import static lab.aulaDIO.EstoqueDeCervejas.utils.JsonConvertionUtils.asJsonString;
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
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();

        when(cervejaService.registrarCerveja(cervejaDTO)).thenReturn(cervejaDTO);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cervejaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoPOSTRegistrarComUmCampoEmBranco() throws Exception {
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();
        cervejaDTO.setNome(null);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cervejaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quandoGETComONomeValidoRetornOKStatus() throws Exception {
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();

        when(cervejaService.procurarPeloNome(cervejaDTO.getNome())).thenReturn(cervejaDTO);

        mockMvc.perform(get(URL_PATH + "/" + cervejaDTO.getNome())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGETForChamadoComNomeNaoCadastrado() throws Exception {
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();

        when(cervejaService.procurarPeloNome(cervejaDTO.getNome())).thenThrow(NaoFoiEncontradoException.class);

        mockMvc.perform(get(URL_PATH + "/" + cervejaDTO.getNome())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoGETForChamadoParaListar() throws Exception {
        CervejaDTO cervejaDTO = builder().build().toBeerDTO();

        when(cervejaService.listarCervejas()).thenReturn(Collections.singletonList(cervejaDTO));

        mockMvc.perform(get(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$[0].marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$[0].tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGETForChamadoParaListarMasEstiverVazio() throws Exception {
        when(cervejaService.listarCervejas()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void quandoOIdForValidoParaExclusao() throws Exception {
        doNothing().when(cervejaService).exclusaoPeloId(ID_INVALIDO);

        mockMvc.perform(delete(URL_PATH + "/" + ID_VALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cervejaService, times(1)).exclusaoPeloId(ID_VALIDO);
    }

    @Test
    void quandoOIdForInvalidoParaExclusao() throws Exception {
        doThrow(NaoFoiEncontradoException.class).when(cervejaService).exclusaoPeloId(ID_INVALIDO);

        mockMvc.perform(delete(URL_PATH + "/" + ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHParaIncrementarForChamadoRetornarOKStatus() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(10)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.incrementoDoEstoque(ID_VALIDO, quantidadeDTO.getQuantidade())).thenReturn(cervejaDTO);

        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())))
                .andExpect(jsonPath("$.quantidade", is(cervejaDTO.getQuantidade())));

    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComValorMaiorQueOMaximoPermitido() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        CervejaDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantidade(beerDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.incrementoDoEstoque(ID_VALIDO, quantidadeDTO.getQuantidade())).thenThrow(EstoqueExcedeuException.class);

        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaIncrementarComOIDInvalido() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        when(cervejaService.incrementoDoEstoque(ID_INVALIDO, quantidadeDTO.getQuantidade())).thenThrow(NaoFoiEncontradoException.class);
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + SUBPATH_INCREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoPATCHForChamadoParaDecremento() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(5)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.decrementaNoEstoque(ID_VALIDO, quantidadeDTO.getQuantidade())).thenReturn(cervejaDTO);

        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getTipo().toString())))
                .andExpect(jsonPath("$.quantity", is(cervejaDTO.getQuantidade())));
    }

    @Test
    void quandoPATCHForChamadoParaDecrementoMenorQueZero() throws Exception {
//        given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(60)
                .build();

        CervejaDTO beerDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantidade(beerDTO.getQuantidade() + quantidadeDTO.getQuantidade());
//         when
        when(cervejaService.decrementaNoEstoque(ID_VALIDO, quantidadeDTO.getQuantidade())).thenThrow(EstoqueExcedeuException.class);
//          then
        mockMvc.perform(patch(URL_PATH + "/" + ID_VALIDO + SUBPATH_DECREMENTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoPATCHForChamadoParaDecrementarCervejaComIDInvalido() throws Exception {
        // given
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(5)
                .build();
        // when
        when(cervejaService.decrementaNoEstoque(ID_INVALIDO, quantidadeDTO.getQuantidade())).thenThrow(NaoFoiEncontradoException.class);
        // then
        mockMvc.perform(patch(URL_PATH + "/" + ID_INVALIDO + URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO)))
                .andExpect(status().isNotFound());
    }
}
