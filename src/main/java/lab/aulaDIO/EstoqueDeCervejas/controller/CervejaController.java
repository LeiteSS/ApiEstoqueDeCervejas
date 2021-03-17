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
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaController implements CervejaControllerDocs {
    private final CervejaService cervejaService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CervejaDTO create(@RequestBody @Valid CervejaDTO cervejaDTO) throws JaExisteException {
        return cervejaService.create(cervejaDTO);
    }

    @Override
    @GetMapping("/{name}")
    public CervejaDTO findByName(@PathVariable String name) throws NaoFoiEncontradoException {
        return cervejaService.findByName(name);
    }

    @Override
    @GetMapping
    public List<CervejaDTO> listAll() { return cervejaService.listAll(); }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(Long id) throws NaoFoiEncontradoException { cervejaService.deleteById(id); }

    @Override
    @PatchMapping("/{id}/increment")
    public CervejaDTO increment(Long id, @Valid QuantidadeDTO quantidadeDTO) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        return cervejaService.increment(id, quantidadeDTO.getQuantity());
    }
    @PatchMapping("/{id}/decrement")
    public CervejaDTO decrementoDoEstoque(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws NaoFoiEncontradoException, EstoqueExcedeuException {
        return cervejaService.decrement(id, quantidadeDTO.getQuantity());
    }

}
