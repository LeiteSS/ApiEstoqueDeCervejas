package io.github.leitess.BeerStockApi.service;

import io.github.leitess.BeerStockApi.exception.EstoqueExcedeuException;
import io.github.leitess.BeerStockApi.exception.NaoFoiEncontradoException;
import io.github.leitess.BeerStockApi.mapper.CervejaMapper;
import io.github.leitess.BeerStockApi.dto.CervejaDTO;
import io.github.leitess.BeerStockApi.entity.Cerveja;
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

    public CervejaDTO create(CervejaDTO cervejaDTO) throws JaExisteException {
        verificaSeEstaRegistrado(cervejaDTO.getName());
        Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
        Cerveja cervejaSalva = cervejaRepository.save(cerveja);
        return cervejaMapper.toDTO(cervejaSalva);
    }

    public CervejaDTO findByName(String name) throws NaoFoiEncontradoException {
        Cerveja cervejaEncontrada = cervejaRepository.findByName(name)
                .orElseThrow(() -> new NaoFoiEncontradoException(name));
        return cervejaMapper.toDTO(cervejaEncontrada);
    }

    public List<CervejaDTO> listAll() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws NaoFoiEncontradoException {
        verificaSeExiste(id);
        cervejaRepository.deleteById(id);
    }

    public CervejaDTO increment(Long id, int quantidadeParaIncrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Cerveja cervejaParaIncrementar = verificaSeExiste(id);
        int cervejaAposIncremento = quantidadeParaIncrementar + cervejaParaIncrementar.getQuantity();
        if (cervejaAposIncremento <= cervejaParaIncrementar.getMax()) {
            cervejaParaIncrementar.setQuantity(cervejaAposIncremento);
            Cerveja cervejaIncrementada = cervejaRepository.save(cervejaParaIncrementar);
            return cervejaMapper.toDTO(cervejaIncrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaIncrementar);
    }

    public CervejaDTO decrement(Long id, int quantidadeParaDecrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Cerveja cervejaParaDecrementar = verificaSeExiste(id);
        int cervejaAposDecremento = cervejaParaDecrementar.getQuantity() - quantidadeParaDecrementar;
        if (cervejaAposDecremento >= 0) {
            cervejaParaDecrementar.setQuantity(cervejaAposDecremento);
            Cerveja cervejaDecrementada = cervejaRepository.save(cervejaParaDecrementar);
            return cervejaMapper.toDTO(cervejaDecrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaDecrementar);
    }

    private void verificaSeEstaRegistrado(String name) throws JaExisteException {
        Optional<Cerveja> optSavedBeer = cervejaRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new JaExisteException(name);
        }
    }

    private Cerveja verificaSeExiste(Long id) throws NaoFoiEncontradoException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new NaoFoiEncontradoException(id));
    }
}
