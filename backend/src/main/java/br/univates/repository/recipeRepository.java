package br.univates.repository;

import br.univates.model.recipes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface recipeRepository extends JpaRepository<recipes, Long> {

}
