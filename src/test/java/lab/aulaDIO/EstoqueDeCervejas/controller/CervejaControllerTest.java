package lab.aulaDIO.EstoqueDeCervejas.controller;

import lab.aulaDIO.EstoqueDeCervejas.builder.CervejaDTOBuilder;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
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

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CervejaControllerTest {
    private static final String URL_PATH = "/api/v1/cervejas";

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
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();

        when(cervejaService.registrarCerveja(cervejaDTO)).thenReturn(cervejaDTO);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CervejaDTOBuilder.asJsonString(cervejaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(cervejaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cervejaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cervejaDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithouRequiredFieldThenAnErrorIsReturned() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toBeerDTO();
        cervejaDTO.setName(null);

        mockMvc.perform(post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CervejaDTOBuilder.asJsonString(cervejaDTO)))
                .andExpect(status().isBadRequest());
    }
}
