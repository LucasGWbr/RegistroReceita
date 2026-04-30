package br.univates.repository;

import br.univates.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByRecipeType(String recipeType);

    List<Recipe> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Recipe> findByRecipeTypeAndCreatedAtBetween(String recipeType, LocalDateTime start, LocalDateTime end);

}
