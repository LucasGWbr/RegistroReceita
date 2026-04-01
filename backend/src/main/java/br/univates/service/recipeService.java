package br.univates.service;


import br.univates.dtos.recipeDTO;
import br.univates.model.recipes;
import br.univates.repository.recipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
