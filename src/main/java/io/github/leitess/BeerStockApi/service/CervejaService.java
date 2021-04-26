package io.github.leitess.BeerStockApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.leitess.BeerStockApi.entity.Beer;
import io.github.leitess.BeerStockApi.exception.EstoqueExcedeuException;
import io.github.leitess.BeerStockApi.exception.NaoFoiEncontradoException;
import io.github.leitess.BeerStockApi.mapper.CervejaMapper;
import io.github.leitess.BeerStockApi.resource.dto.BeerDTO;
import io.github.leitess.BeerStockApi.exception.JaExisteException;
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

    public BeerDTO create(BeerDTO beerDTO) throws JaExisteException {
        verificaSeEstaRegistrado(beerDTO.getName());
        Beer beer = objectMapper.convertValue(beerDTO, Beer.class);
        Beer beerSalva = cervejaRepository.save(beer);
        return cervejaMapper.toDTO(beerSalva);
    }

    public BeerDTO findByName(String name) throws NaoFoiEncontradoException {
        Beer beerEncontrada = cervejaRepository.findByName(name)
                .orElseThrow(() -> new NaoFoiEncontradoException(name));
        return cervejaMapper.toDTO(beerEncontrada);
    }

    public List<BeerDTO> listAll() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws NaoFoiEncontradoException {
        verificaSeExiste(id);
        cervejaRepository.deleteById(id);
    }

    public BeerDTO increment(Long id, int quantidadeParaIncrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Beer beerParaIncrementar = verificaSeExiste(id);
        int cervejaAposIncremento = quantidadeParaIncrementar + beerParaIncrementar.getQuantity();
        if (cervejaAposIncremento <= beerParaIncrementar.getMax()) {
            beerParaIncrementar.setQuantity(cervejaAposIncremento);
            Beer beerIncrementada = cervejaRepository.save(beerParaIncrementar);
            return cervejaMapper.toDTO(beerIncrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaIncrementar);
    }

    public BeerDTO decrement(Long id, int quantidadeParaDecrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Beer beerParaDecrementar = verificaSeExiste(id);
        int cervejaAposDecremento = beerParaDecrementar.getQuantity() - quantidadeParaDecrementar;
        if (cervejaAposDecremento >= 0) {
            beerParaDecrementar.setQuantity(cervejaAposDecremento);
            Beer beerDecrementada = cervejaRepository.save(beerParaDecrementar);
            return cervejaMapper.toDTO(beerDecrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaDecrementar);
    }

    private void verificaSeEstaRegistrado(String name) throws JaExisteException {
        Optional<Beer> optSavedBeer = cervejaRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new JaExisteException(name);
        }
    }

    private Beer verificaSeExiste(Long id) throws NaoFoiEncontradoException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new NaoFoiEncontradoException(id));
    }
}
