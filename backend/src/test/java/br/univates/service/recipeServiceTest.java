package br.univates.service;

import br.univates.dtos.recipeDTO;
import br.univates.model.recipes;
import br.univates.repository.recipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class recipeServiceTest {

    @Mock
    private recipeRepository recipeRepository;

    @InjectMocks
    private recipeService recipeService;

    private recipes sampleRecipe;
    private recipeDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new recipeDTO("Bolo de Cenoura", "Receita clássica", new BigDecimal("25.00"), "Doce");

        sampleRecipe = new recipes();
        sampleRecipe.setId(1L);
        sampleRecipe.setName("Bolo de Cenoura");
        sampleRecipe.setDescription("Receita clássica");
        sampleRecipe.setPrice(new BigDecimal("25.00"));
        sampleRecipe.setRecipeType("Doce");
        sampleRecipe.setCreatedAt(LocalDateTime.now());
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    // 1 criar receita
    @Test
    void shouldCreateRecipeSuccessfully() {
        when(recipeRepository.save(any(recipes.class))).thenReturn(sampleRecipe);

        recipes result = recipeService.createRecipe(sampleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bolo de Cenoura");
        assertThat(result.getRecipeType()).isEqualTo("Doce");
        verify(recipeRepository, times(1)).save(any(recipes.class));
    }

    //2 retornar dados ao criar
    @Test
    void shouldMapAllDtoFieldsOnCreate() {
        when(recipeRepository.save(any(recipes.class))).thenAnswer(inv -> inv.getArgument(0));

        recipes result = recipeService.createRecipe(sampleDTO);

        assertThat(result.getName()).isEqualTo(sampleDTO.name());
        assertThat(result.getDescription()).isEqualTo(sampleDTO.description());
        assertThat(result.getPrice()).isEqualByComparingTo(sampleDTO.price());
        assertThat(result.getRecipeType()).isEqualTo(sampleDTO.recipeType());
    }

    // ─── READ ALL ─────────────────────────────────────────────────────────────

    // 3 retornar todas as receitas
    @Test
    void shouldReturnAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(List.of(sampleRecipe));

        List<recipes> result = recipeService.getAllRecipes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bolo de Cenoura");
        verify(recipeRepository, times(1)).findAll();
    }

    // 4 retornar lista vazia
    @Test
    void shouldReturnEmptyListWhenNoRecipes() {
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());

        List<recipes> result = recipeService.getAllRecipes();

        assertThat(result).isEmpty();
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    // 5 atualizar receita
    @Test
    void shouldUpdateExistingRecipeSuccessfully() {
        recipeDTO updateDTO = new recipeDTO("Bolo Atualizado", "Desc nova", new BigDecimal("30.00"), "Salgado");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(sampleRecipe));
        when(recipeRepository.save(any(recipes.class))).thenAnswer(inv -> inv.getArgument(0));

        recipes result = recipeService.updateRecipe(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Bolo Atualizado");
        assertThat(result.getRecipeType()).isEqualTo("Salgado");
        assertThat(result.getPrice()).isEqualByComparingTo("30.00");
    }

    // 6 erro ao atualizar inexistente
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentRecipe() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(99L, sampleDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrada");

        verify(recipeRepository, never()).save(any());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    // 7 deletar receita
    @Test
    void shouldDeleteExistingRecipeSuccessfully() {
        when(recipeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(recipeRepository).deleteById(1L);

        assertThatCode(() -> recipeService.deleteRecipe(1L)).doesNotThrowAnyException();

        verify(recipeRepository, times(1)).deleteById(1L);
    }

    // 8 erro ao deletar inexistente
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRecipe() {
        when(recipeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> recipeService.deleteRecipe(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrada");

        verify(recipeRepository, never()).deleteById(any());
    }

    // ─── FILTER ───────────────────────────────────────────────────────────────

    // 9  filtrar receitas com um atributo (type)
    @Test
    void shouldFilterByTypeOnly() {
        when(recipeRepository.findByRecipeType("Doce")).thenReturn(List.of(sampleRecipe));

        List<recipes> result = recipeService.getFilteredRecipes("Doce", null);

        assertThat(result).hasSize(1);
        verify(recipeRepository).findByRecipeType("Doce");
        verify(recipeRepository, never()).findByCreatedAtBetween(any(), any());
    }

    // 10  filtrar receitas com um atributo (data)
    @Test
    void shouldFilterByDateOnly() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        when(recipeRepository.findByCreatedAtBetween(start, end)).thenReturn(List.of(sampleRecipe));

        List<recipes> result = recipeService.getFilteredRecipes(null, today);

        assertThat(result).hasSize(1);
        verify(recipeRepository).findByCreatedAtBetween(start, end);
        verify(recipeRepository, never()).findByRecipeType(any());
    }

    // 11 filtrar receitas com dois atributos (type e data)
    @Test
    void shouldFilterByTypeAndDate() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        when(recipeRepository.findByRecipeTypeAndCreatedAtBetween("Doce", start, end))
                .thenReturn(List.of(sampleRecipe));

        List<recipes> result = recipeService.getFilteredRecipes("Doce", today);

        assertThat(result).hasSize(1);
        verify(recipeRepository).findByRecipeTypeAndCreatedAtBetween("Doce", start, end);
    }

    // 12 retornar todas as receitas quando sem filtro
    @Test
    void shouldReturnAllWhenNoFilterProvided() {
        when(recipeRepository.findAll()).thenReturn(List.of(sampleRecipe));

        List<recipes> result = recipeService.getFilteredRecipes(null, null);

        assertThat(result).hasSize(1);
        verify(recipeRepository).findAll();
        verify(recipeRepository, never()).findByRecipeType(any());
        verify(recipeRepository, never()).findByCreatedAtBetween(any(), any());
    }
}
