package br.univates.controller;

import br.univates.dtos.recipeDTO;
import br.univates.model.recipes;
import br.univates.service.recipeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class recipeController {
    private final recipeService recipeService;
    public recipeController(recipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/create")
    public ResponseEntity<recipes> createRecipe(@RequestBody recipeDTO recipeDTO) {
        recipes created = recipeService.createRecipe(recipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/read/all")
    public ResponseEntity<List<recipes>> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/read/filter")
    public ResponseEntity<List<recipes>> getFilteredRecipes(
            @RequestParam(required = false) String type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dateTime
    )
    {
        return ResponseEntity.ok(recipeService.getFilteredRecipes(type, dateTime));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<recipes> updateRecipe(@PathVariable Long id, @RequestBody recipeDTO recipeDTO) {
        try {
            return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
