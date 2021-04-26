package io.github.leitess.BeerStockApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NaoFoiEncontradoException extends Exception {

    public NaoFoiEncontradoException(String nomeCerveja) {
        super(String.format("Cerveja de nome %s não foi encontrada no sistema.", nomeCerveja));
    }

    public NaoFoiEncontradoException(Long id) {
        super(String.format("Cerveja de id %s nao foi encontrada no sistema.", id));
    }

}
