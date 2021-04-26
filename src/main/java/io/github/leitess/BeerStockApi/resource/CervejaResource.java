package io.github.leitess.BeerStockApi.resource;

import io.github.leitess.BeerStockApi.exception.StockExceededException;
import io.github.leitess.BeerStockApi.exception.AlreadyExistException;
import io.github.leitess.BeerStockApi.exception.NotFoundException;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.service.CervejaService;
import io.github.leitess.BeerStockApi.resource.dto.QuantityDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaResource implements CervejaResourceDocs {
    private final CervejaService cervejaService;

    @Override
    @PostMapping("/beers")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO create(@RequestBody @Valid BeerDTO beerDTO) throws AlreadyExistException {
        return cervejaService.create(beerDTO);
    }

    @Override
    @GetMapping("/beers/{name}")
    public BeerDTO findByName(@PathVariable String name) throws NotFoundException {
        return cervejaService.findByName(name);
    }

    @Override
    @GetMapping("/beers")
    public List<BeerDTO> listAll() { return cervejaService.listAll(); }

    @Override
    @PutMapping("/beers/{id}")
    public BeerDTO updateById(@PathVariable Long id, @RequestBody @Valid BeerDTO beerDTO) throws NotFoundException {
        return cervejaService.updateById(id, beerDTO);
    }

    @Override
    @DeleteMapping("/beers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(Long id) throws NotFoundException { cervejaService.deleteById(id); }

    @Override
    @PatchMapping("/beers/{id}/increment")
    public BeerDTO increment(Long id, @Valid QuantityDTO quantityDTO) throws NotFoundException, StockExceededException {
        return cervejaService.increment(id, quantityDTO.getQuantity());
    }

    @Override
    @PatchMapping("/beers/{id}/decrement")
    public BeerDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws NotFoundException, StockExceededException {
        return cervejaService.decrement(id, quantityDTO.getQuantity());
    }

}
