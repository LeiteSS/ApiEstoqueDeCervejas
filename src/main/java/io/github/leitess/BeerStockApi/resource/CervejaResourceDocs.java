package io.github.leitess.BeerStockApi.resource;

import io.github.leitess.BeerStockApi.exception.EstoqueExcedeuException;
import io.github.leitess.BeerStockApi.exception.NaoFoiEncontradoException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.resource.dto.QuantidadeDTO;
import io.github.leitess.BeerStockApi.exception.JaExisteException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Gerencia Estoque de Cervejas")
public interface CervejaResourceDocs {

    @ApiOperation(value = "Registrar a Cerveja")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Cerveja registrada com sucesso."),
            @ApiResponse(code = 400, message = "Campos Obrigatorios em branco ou valores fora do escopo")
    })
    BeerDTO create(BeerDTO beerDTO) throws JaExisteException;

    @ApiOperation(value = "Retorna a cerveja encontrada usando o nome.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso ao encontrar a cerveja no sistema."),
            @ApiResponse(code = 404, message = "Cerveja não encontrada.")
    })
    BeerDTO findByName(@PathVariable String name) throws NaoFoiEncontradoException;

    @ApiOperation(value = "Retorna uma lista com todas as cervejas no sistema.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all beers registered in the system"),
    })
    List<BeerDTO> listAll();

    @ApiOperation(value = "Deleta a cerveja usando o id dado.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Sucesso na exclusão da cerveja do sistema."),
            @ApiResponse(code = 404, message = "Cerveja com o id dado não foi encontrada.")
    })
    void deleteById(@PathVariable Long id) throws NaoFoiEncontradoException;

    @ApiOperation(value = "Incrementar uma cerveja usando o Id passado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cerveja Incrementada no estoque com sucesso."),
            @ApiResponse(code = 400, message = "Falha ao incrementar a cerveja no estoque."),
            @ApiResponse(code = 404, message = "Não foi encontrado cerveja com esse id.")
    })
    BeerDTO increment(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws NaoFoiEncontradoException, EstoqueExcedeuException;

}