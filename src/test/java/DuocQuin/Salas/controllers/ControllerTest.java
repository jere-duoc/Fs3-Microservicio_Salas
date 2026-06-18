package DuocQuin.Salas.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import DuocQuin.Salas.controller.SalaController;
import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.service.SalaService;

@WebMvcTest(SalaController.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaService salaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    void obtenersalashttp200() throws Exception {
        Sala sala = new Sala(2L, "Box", 10);

        when(salaService.findAll()).thenReturn(Arrays.asList(sala));

        mockMvc.perform(get("/api/salas")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSala").value(2))
                .andExpect(jsonPath("$[0].tipoDeSala").value("Box"));
    }

    @Test
    @WithMockUser(username = "user")
    void obtenersalaporidhttp200() throws Exception {
        Sala sala = new Sala(2L, "Box", 10);

        when(salaService.findById(2L)).thenReturn(Optional.of(sala));

        mockMvc.perform(get("/api/salas/2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeSala").value("Box"))
                .andExpect(jsonPath("$.capacidad").value(10));
    }

    @Test
    @WithMockUser(username = "user")
    void salanoexistehttp404() throws Exception {
        when(salaService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/salas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void crearsalahttp201() throws Exception {
        Sala sala = new Sala(null, "Box", 10);
        Sala salaGuardada = new Sala(2L, "Box", 10);

        when(salaService.save(any(Sala.class))).thenReturn(salaGuardada);

        mockMvc.perform(post("/api/salas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sala)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSala").value(2))
                .andExpect(jsonPath("$.tipoDeSala").value("Box"))
                .andExpect(jsonPath("$.capacidad").value(10));
    }

    @Test
    @WithMockUser(username = "user")
    void crearsalacondatosinvalidoshttp400() throws Exception {
        Sala salaInvalida = new Sala(null, "", 10);

        when(salaService.save(any(Sala.class))).thenThrow(new IllegalArgumentException("El tipo de sala no puede estar vacío"));

        mockMvc.perform(post("/api/salas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salaInvalida)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user")
    void actualizarsalahttp200() throws Exception {
        Sala salaActualizada = new Sala(2L, "Box", 15);
        Sala saladetalles = new Sala(null, "Box", 15);

        when(salaService.update(eq(2L), any(Sala.class))).thenReturn(salaActualizada);
        
        mockMvc.perform(put("/api/salas/2")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saladetalles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeSala").value("Box"));
    }

    @Test
    @WithMockUser(username = "user")
    void eliminarSalahttp204() throws Exception {
        doNothing().when(salaService).deleteById(2L);

        mockMvc.perform(delete("/api/salas/2")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void getsalaportipo() throws Exception {
        Sala sala = new Sala(2L, "Box", 10);

        when(salaService.findByTipoDeSala(anyString())).thenReturn(Optional.of(sala));

        mockMvc.perform(get("/api/salas/tipo/Box")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeSala").value("Box"));
    }

    @Test
    @WithMockUser(username = "user")
    void buscarsalaportipo() throws Exception {
        Sala sala = new Sala(2L, "Box", 10);
        when(salaService.findByTipoDeSalaContaining("Box")).thenReturn(Arrays.asList(sala));

        mockMvc.perform(get("/api/salas/buscar")
                .param("tipo", "Box")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoDeSala").value("Box"));
    }

    @Test
    @WithMockUser(username = "user")
    void actualizarsalaFallaCuandoNoExiste_HttpError() throws Exception {
        Sala saladetalles = new Sala(null, "Box", 15);
        when(salaService.update(eq(999L), any(Sala.class))).thenThrow(new RuntimeException("Sala no encontrada"));

        mockMvc.perform(put("/api/salas/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saladetalles)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "user")
    void eliminarSalaFallaCuandoNoExiste_HttpError() throws Exception {
        doThrow(new RuntimeException("Error al eliminar")).when(salaService).deleteById(999L);

        mockMvc.perform(delete("/api/salas/999")
                .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "user")
    void forzarGetErrorResponse_AlProvocarExcepcionInterna() throws Exception {
        when(salaService.findAll()).thenThrow(new RuntimeException("Error interno simulado"));

        try {
            mockMvc.perform(get("/api/salas")
                    .accept(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("Error interno simulado") || e.getCause() instanceof RuntimeException);
        }
    }
}