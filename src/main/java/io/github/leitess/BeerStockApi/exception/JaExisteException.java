package io.github.leitess.BeerStockApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JaExisteException extends Exception{

    public JaExisteException(String nomeCerveja) {
        super(String.format("Cerveja de nome %s já existe no sistema.", nomeCerveja));
    }
}
