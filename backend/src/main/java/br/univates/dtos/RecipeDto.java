package br.univates.dtos;

import java.math.BigDecimal;

public record RecipeDto(String name, String description, BigDecimal price, String recipeType) {
}
