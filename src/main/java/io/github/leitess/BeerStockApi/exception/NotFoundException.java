package io.github.leitess.BeerStockApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {

    public NotFoundException(String beerName) {
        super(String.format("The Beer %s was not found.", beerName));
    }

    public NotFoundException(Long id) {
        super(String.format("The beer number %s was not found.", id));
    }

}
