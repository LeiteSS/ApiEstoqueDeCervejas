package lab.aulaDIO.EstoqueDeCervejas.controller;

import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.dto.QuantidadeDTO;
import lab.aulaDIO.EstoqueDeCervejas.exception.EstoqueExcedeuException;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;
import lab.aulaDIO.EstoqueDeCervejas.exception.NaoFoiEncontradoException;
import lab.aulaDIO.EstoqueDeCervejas.service.CervejaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cervejas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaController implements CervejaControllerDocs {
    private final CervejaService cervejaService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CervejaDTO registrarCerveja(@RequestBody @Valid CervejaDTO cervejaDTO) throws JaExisteException {
        return cervejaService.registrarCerveja(cervejaDTO);
    }

    @Override
    @GetMapping("/{name}")
    public CervejaDTO procurarPeloNome(@PathVariable String nome) throws NaoFoiEncontradoException {
        return cervejaService.procurarPeloNome(nome);
    }

    @Override
    @GetMapping
    public List<CervejaDTO> listarCerveja() {
        return cervejaService.listarCervejas();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exclusaoPeloId(Long id) throws NaoFoiEncontradoException {
        cervejaService.exclusaoPeloId(id);
    }

    @Override
    @PatchMapping("/{id}/incremento")
    public CervejaDTO incrementoDoEstoque(Long id, @Valid QuantidadeDTO quantidadeDTO) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        return cervejaService.incrementoDoEstoque(id, quantidadeDTO.getQuantidade());
    }

    @PatchMapping("/{id}/decremento")
    public CervejaDTO decrementoDoEstoque(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        return cervejaService.decrementaNoEstoque(id, quantidadeDTO.getQuantidade());
    }

}
