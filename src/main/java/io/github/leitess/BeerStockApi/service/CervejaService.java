package io.github.leitess.BeerStockApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.leitess.BeerStockApi.entity.Beer;
import io.github.leitess.BeerStockApi.exception.StockExceededException;
import io.github.leitess.BeerStockApi.exception.NotFoundException;
import io.github.leitess.BeerStockApi.mapper.CervejaMapper;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.exception.AlreadyExistException;
import io.github.leitess.BeerStockApi.repository.CervejaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaService {
    private final CervejaRepository cervejaRepository;
    private final CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;
    private ObjectMapper objectMapper;

    public BeerDTO create(BeerDTO beerDTO) throws AlreadyExistException {
        verifyIfAlreadyWasCreated(beerDTO.getName());

        Beer beer = objectMapper.convertValue(beerDTO, Beer.class);
        Beer beerSalva = cervejaRepository.save(beer);

        return cervejaMapper.toDTO(beerSalva);
    }

    public BeerDTO findByName(String name) throws NotFoundException {
        Beer beerEncontrada = findBeerByName(name);
        return cervejaMapper.toDTO(beerEncontrada);
    }

    public List<BeerDTO> listAll() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BeerDTO updateById(Long id, BeerDTO beerDTO) throws NotFoundException {
        verifyIfExists(id);

        Beer beerToUpdate = objectMapper.convertValue(beerDTO, Beer.class);
        beerToUpdate.setId(id);
        Beer updatedBeer = cervejaRepository.save(beerToUpdate);

        return cervejaMapper.toDTO(updatedBeer);
    }

    public void deleteById(Long id) throws NotFoundException {
        verifyIfExists(id);
        cervejaRepository.deleteById(id);
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws NotFoundException, StockExceededException {
        Beer beerToIncrement = verifyIfExists(id);
        int beerAfterIncrement = quantityToIncrement + beerToIncrement.getQuantity();
        if (beerAfterIncrement <= beerToIncrement.getMax()) {
            beerToIncrement.setQuantity(beerAfterIncrement);
            Beer beerIncremented = cervejaRepository.save(beerToIncrement);
            return cervejaMapper.toDTO(beerIncremented);
        }
        throw new StockExceededException(id, quantityToIncrement);
    }

    public BeerDTO decrement(Long id, int quantityToDecrement) throws NotFoundException, StockExceededException {
        Beer beerToDecrement = verifyIfExists(id);
        int beerAfterDecrement = beerToDecrement.getQuantity() - quantityToDecrement;
        if (beerAfterDecrement >= 0) {
            beerToDecrement.setQuantity(beerAfterDecrement);
            Beer beerDecremented = cervejaRepository.save(beerToDecrement);
            return cervejaMapper.toDTO(beerDecremented);
        }
        throw new StockExceededException(id, quantityToDecrement);
    }

    private void verifyIfAlreadyWasCreated(String name) throws AlreadyExistException {
        Optional<Beer> optSavedBeer = cervejaRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new AlreadyExistException(name);
        }
    }

    private Beer verifyIfExists(Long id) throws NotFoundException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    private Beer findBeerByName(String name) throws NotFoundException {
        return cervejaRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException(name));
    }
}
