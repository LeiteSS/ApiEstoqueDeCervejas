package lab.aulaDIO.EstoqueDeCervejas.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;

@Api("Gerencia Estoque de Cervejas")
public interface CervejaControllerDocs {

    @ApiOperation(value = "Registrar a Cerveja")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Cerveja registrada com sucesso."),
            @ApiResponse(code = 400, message = "Campos Obrigatorios em branco ou valores fora do escopo")
    })
    CervejaDTO registrarCerveja(CervejaDTO cervejaDTO) throws JaExisteException;
}
