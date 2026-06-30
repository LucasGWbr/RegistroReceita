package br.univates.service;


import br.univates.dtos.RecipeDto;
import br.univates.model.Recipe;
import br.univates.repository.RecipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(RecipeDto recipeDTO){
        Recipe recipe = new Recipe();
        BeanUtils.copyProperties(recipeDTO, recipe);
        return recipeRepository.save(recipe);
    }
    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public Recipe updateRecipe(Long id, RecipeDto recipeDTO) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
        BeanUtils.copyProperties(recipeDTO, recipe);
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow();
        recipeRepository.deleteById(recipe.getId());
    }

    public List<Recipe> getFilteredRecipes(String type, LocalDate date) {
        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;

        if (date != null) {
            startOfDay = date.atStartOfDay(); // 00:00:00
            endOfDay = date.atTime(LocalTime.MAX); // 23:59:59.999999
        }

        // Lógica de Filtros
        if (type != null && date != null) {
            return recipeRepository.findByRecipeTypeAndCreatedAtBetween(type, startOfDay, endOfDay);
        }
        if (date != null) {
            return recipeRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        }
        if (type != null) {
            return recipeRepository.findByRecipeType(type);
        }

        return recipeRepository.findAll();
    }


}
