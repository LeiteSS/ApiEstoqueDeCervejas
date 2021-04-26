package io.github.leitess.BeerStockApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistException extends Exception{

    public AlreadyExistException(String beerName) {
        super(String.format("The beer %s already exists.", beerName));
    }
}
