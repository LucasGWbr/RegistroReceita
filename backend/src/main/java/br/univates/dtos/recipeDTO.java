package br.univates.dtos;

import java.math.BigDecimal;

public record recipeDTO(String name, String description, BigDecimal price, String recipeType) {
}
