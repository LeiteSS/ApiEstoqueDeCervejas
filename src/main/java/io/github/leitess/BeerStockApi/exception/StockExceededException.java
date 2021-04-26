package io.github.leitess.BeerStockApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockExceededException extends Exception {

    public StockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Beer ID number %s to increment using the quantity informed exceed the maximum stock beer: %s", id, quantityToIncrement));
    }
}
