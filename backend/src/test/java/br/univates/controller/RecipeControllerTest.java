package br.univates.controller;

import br.univates.config.JwtService;
import br.univates.dtos.RecipeDto;
import br.univates.model.Recipe;
import br.univates.service.PdfService;
import br.univates.service.RecipeService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@WithMockUser
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PdfService pdfService;

    private Recipe sampleRecipe;
    private RecipeDto sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new RecipeDto("Frango Grelhado", "Prato saudável", new BigDecimal("18.50"), "Salgado");

        sampleRecipe = new Recipe();
        sampleRecipe.setId(1L);
        sampleRecipe.setName("Frango Grelhado");
        sampleRecipe.setDescription("Prato saudável");
        sampleRecipe.setPrice(new BigDecimal("18.50"));
        sampleRecipe.setRecipeType("Salgado");
        sampleRecipe.setCreatedAt(LocalDateTime.now());
    }

    // ─── POST /create ─────────────────────────────────────────────────────────

    // 13 rota deve retorna 201 quando criar
    @Test
    void shouldReturn201WhenCreatingRecipe() throws Exception {
        when(recipeService.createRecipe(any(RecipeDto.class))).thenReturn(sampleRecipe);

        mockMvc.perform(post("/api/recipe/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Frango Grelhado")))
                .andExpect(jsonPath("$.recipeType", is("Salgado")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    // 14 rota não pode chamar o service mais de uma vez
    @Test
    void shouldCallServiceOnceOnCreate() throws Exception {
        when(recipeService.createRecipe(any(RecipeDto.class))).thenReturn(sampleRecipe);

        mockMvc.perform(post("/api/recipe/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isCreated());

        verify(recipeService, times(1)).createRecipe(any(RecipeDto.class));
    }

    // ─── GET /read/all ────────────────────────────────────────────────────────

    // 15 rota deve retorna 200 quando buscar receitas
    @Test
    void shouldReturn200WithRecipeList() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(List.of(sampleRecipe));

        mockMvc.perform(get("/api/recipe/read/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Frango Grelhado")));
    }

    // 16 rota deve retorna 200 quando não tiver receitas (se estiver vazia)
    @Test
    void shouldReturn200WithEmptyList() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recipe/read/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── PUT /update/{id} ─────────────────────────────────────────────────────

    // 17 rota deve retorna 200 quando atualizar ( e receita atualizada )
    @Test
    void shouldReturn200WhenUpdatingRecipe() throws Exception {
        when(recipeService.updateRecipe(eq(1L), any(RecipeDto.class))).thenReturn(sampleRecipe);

        mockMvc.perform(put("/api/recipe/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Frango Grelhado")));
    }

    // 18 retorna 404 quando a receita nao existe
    @Test
    void shouldReturn404WhenUpdatingNonExistentRecipe() throws Exception {
        when(recipeService.updateRecipe(eq(99L), any(RecipeDto.class)))
                .thenThrow(new RuntimeException("Receita não encontrada"));

        mockMvc.perform(put("/api/recipe/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /delete/{id} ──────────────────────────────────────────────────

    // 19 retorna 204 quando deletar com sucesso
    @Test
    void shouldReturn204WhenDeletingRecipe() throws Exception {
        doNothing().when(recipeService).deleteRecipe(1L);

        mockMvc.perform(delete("/api/recipe/delete/1"))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).deleteRecipe(1L);
    }

    // 20 retorna 404 quando receita a ser excluida nao existe
    @Test
    void shouldReturn404WhenDeletingNonExistentRecipe() throws Exception {
        doThrow(new RuntimeException("Receita não encontrada"))
                .when(recipeService).deleteRecipe(99L);

        mockMvc.perform(delete("/api/recipe/delete/99"))
                .andExpect(status().isNotFound());
    }
}
