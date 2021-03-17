package lab.aulaDIO.EstoqueDeCervejas.service;

import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.entity.Cerveja;
import lab.aulaDIO.EstoqueDeCervejas.exception.EstoqueExcedeuException;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;
import lab.aulaDIO.EstoqueDeCervejas.exception.NaoFoiEncontradoException;
import lab.aulaDIO.EstoqueDeCervejas.mapper.CervejaMapper;
import lab.aulaDIO.EstoqueDeCervejas.repository.CervejaRepository;
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

    public CervejaDTO registrarCerveja(CervejaDTO cervejaDTO) throws JaExisteException {
        verificaSeEstaRegistrado(cervejaDTO.getNome());
        Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
        Cerveja cervejaSalva = cervejaRepository.save(cerveja);
        return cervejaMapper.toDTO(cervejaSalva);
    }

    public CervejaDTO procurarPeloNome(String name) throws NaoFoiEncontradoException {
        Cerveja cervejaEncontrada = cervejaRepository.procurarPeloNome(name)
                .orElseThrow(() -> new NaoFoiEncontradoException(name));
        return cervejaMapper.toDTO(cervejaEncontrada);
    }

    public List<CervejaDTO> listarCervejas() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void exclusaoPeloId(Long id) throws NaoFoiEncontradoException {
        verificaSeExiste(id);
        cervejaRepository.deleteById(id);
    }

    public CervejaDTO incrementoDoEstoque(Long id, int quantidadeParaIncrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Cerveja cervejaParaIncrementar = verificaSeExiste(id);
        int cervejaAposIncremento = quantidadeParaIncrementar + cervejaParaIncrementar.getQuantidade();
        if (cervejaAposIncremento <= cervejaParaIncrementar.getMax()) {
            cervejaParaIncrementar.setQuantidade(cervejaAposIncremento);
            Cerveja cervejaIncrementada = cervejaRepository.save(cervejaParaIncrementar);
            return cervejaMapper.toDTO(cervejaIncrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaIncrementar);
    }

    public CervejaDTO decrementaNoEstoque(Long id, int quantidadeParaDecrementar) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        Cerveja cervejaParaDecrementar = verificaSeExiste(id);
        int cervejaAposDecremento = cervejaParaDecrementar.getQuantidade() - quantidadeParaDecrementar;
        if (cervejaAposDecremento >= 0) {
            cervejaParaDecrementar.setQuantidade(cervejaAposDecremento);
            Cerveja cervejaDecrementada = cervejaRepository.save(cervejaParaDecrementar);
            return cervejaMapper.toDTO(cervejaDecrementada);
        }
        throw new EstoqueExcedeuException(id, quantidadeParaDecrementar);
    }

    private void verificaSeEstaRegistrado(String name) throws JaExisteException {
        Optional<Cerveja> optSavedBeer = cervejaRepository.procurarPeloNome(name);
        if (optSavedBeer.isPresent()) {
            throw new JaExisteException(name);
        }
    }

    private Cerveja verificaSeExiste(Long id) throws NaoFoiEncontradoException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new NaoFoiEncontradoException(id));
    }
}
