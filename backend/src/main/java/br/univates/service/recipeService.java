package br.univates.service;


import br.univates.dtos.recipeDTO;
import br.univates.model.recipes;
import br.univates.repository.recipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;

@Service
public class recipeService {
    private final recipeRepository recipeRepository;

    public recipeService(recipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public recipes createRecipe(recipeDTO recipeDTO){
        recipes recipe = new recipes();
        BeanUtils.copyProperties(recipeDTO, recipe);
        return recipeRepository.save(recipe);
    }
    public List<recipes> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public recipes updateRecipe(Long id, recipeDTO recipeDTO) {
        recipes recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
        BeanUtils.copyProperties(recipeDTO, recipe);
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RuntimeException("Receita não encontrada");
        }
        recipeRepository.deleteById(id);
    }

    public List<recipes> getFilteredRecipes(String type, LocalDate date) {
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
