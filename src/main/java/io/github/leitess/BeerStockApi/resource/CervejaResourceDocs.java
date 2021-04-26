package io.github.leitess.BeerStockApi.resource;

import io.github.leitess.BeerStockApi.exception.StockExceededException;
import io.github.leitess.BeerStockApi.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.resource.dto.QuantityDTO;
import io.github.leitess.BeerStockApi.exception.AlreadyExistException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manage Beer Stock")
public interface CervejaResourceDocs {

    @ApiOperation(value = "Register a Beer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Beer Created successfully."),
            @ApiResponse(code = 400, message = "Field empty or invalid")
    })
    BeerDTO create(BeerDTO beerDTO) throws AlreadyExistException;

    @ApiOperation(value = "Find a Beer using its name.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Beer found."),
            @ApiResponse(code = 404, message = "Beer NOT Found.")
    })
    BeerDTO findByName(@PathVariable String name) throws NotFoundException;

    @ApiOperation(value = "List all beers.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all beers registered in the system"),
    })
    List<BeerDTO> listAll();

    @ApiOperation(value = "Update a Beer By Id")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Beer updated successfully."),
            @ApiResponse(code = 400, message = "Fields empty or invalid")
    })
    BeerDTO updateById(@PathVariable Long id, BeerDTO beerDTO) throws NotFoundException;

    @ApiOperation(value = "Delete a beer using its ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Beer deleted successfully from the system."),
            @ApiResponse(code = 404, message = "Beer not found.")
    })
    void deleteById(@PathVariable Long id) throws NotFoundException;

    @ApiOperation(value = "Increment the stock.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Beer incremented successfully."),
            @ApiResponse(code = 400, message = "Fail to incremented stock."),
            @ApiResponse(code = 404, message = "Beer not found.")
    })
    BeerDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws NotFoundException, StockExceededException;

    @ApiOperation(value = "Decrement the stock.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Beer decremented successfully."),
            @ApiResponse(code = 400, message = "Fail to decremented stock."),
            @ApiResponse(code = 404, message = "Beer not found.")
    })
    BeerDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws NotFoundException, StockExceededException;
}
