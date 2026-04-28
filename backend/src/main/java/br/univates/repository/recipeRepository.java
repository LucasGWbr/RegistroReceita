package br.univates.repository;

import br.univates.model.recipes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface recipeRepository extends JpaRepository<recipes, Long> {
    List<recipes> findByRecipeType(String recipeType);

    List<recipes> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<recipes> findByRecipeTypeAndCreatedAtBetween(String recipeType, LocalDateTime start, LocalDateTime end);

}
