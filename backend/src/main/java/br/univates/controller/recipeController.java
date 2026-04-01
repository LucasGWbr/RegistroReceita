package br.univates.controller;

import br.univates.model.recipes;
import br.univates.service.recipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class recipeController {
    private final recipeService recipeService;
    public recipeController(recipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/read/all")
    public ResponseEntity<List<recipes>> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
}
