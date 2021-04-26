package io.github.leitess.BeerStockApi.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum BeerType {
    LAGER("Lager"),
    MALZBIER("Malzbier"),
    WITBIER("Witbier"),
    WEISS("Weiss"),
    ALE("Ale"),
    IPA("IPA"),
    STOUT("Stout");

    private final String description;

    public static BeerType of(String description) {

        return Stream.of(BeerType.values())
                .filter(it -> it.getDescription().equals(description))
                .findFirst()
                .orElseThrow();
    }
}
