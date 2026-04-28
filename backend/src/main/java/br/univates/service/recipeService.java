package br.univates.service;


import br.univates.dtos.recipeDTO;
import br.univates.model.recipes;
import br.univates.repository.recipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<recipes> getFilteredRecipes(String type, LocalDateTime dateTime)
    {
        if (type != null && dateTime != null) {
            return recipeRepository.findByRecipeTypeAndCreatedAt(type, dateTime);
        }
        if (type != null) {
            return recipeRepository.findByRecipeType(type);
        }
        if (dateTime != null) {
            return recipeRepository.findByCreatedAt(dateTime);
        }
        return null;
    }
}
