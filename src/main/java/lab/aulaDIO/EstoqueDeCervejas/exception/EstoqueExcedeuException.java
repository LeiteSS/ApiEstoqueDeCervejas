package lab.aulaDIO.EstoqueDeCervejas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EstoqueExcedeuException extends Exception {

    public EstoqueExcedeuException(Long id, int quantidadeParaIncrementar) {
        super(String.format("Cerveja de ID numero %s para incrementar com a quantidade informada excedeu a capacidade máxima do estoque: %s", id, quantidadeParaIncrementar));
    }
}
