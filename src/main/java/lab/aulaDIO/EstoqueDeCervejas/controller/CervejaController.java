package lab.aulaDIO.EstoqueDeCervejas.controller;

import lab.aulaDIO.EstoqueDeCervejas.dto.CervejaDTO;
import lab.aulaDIO.EstoqueDeCervejas.exception.JaExisteException;
import lab.aulaDIO.EstoqueDeCervejas.service.CervejaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
