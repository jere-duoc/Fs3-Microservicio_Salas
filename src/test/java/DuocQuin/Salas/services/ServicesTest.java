package DuocQuin.Salas.services;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.repository.SalaRepository;
import DuocQuin.Salas.service.SalaService;

@ExtendWith(MockitoExtension.class)
public class ServicesTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    private Sala sala1;

    @BeforeEach
    void setUp() {
        sala1 = new Sala(2L, "Box", 10);
    }
    @Test
    void listarSalas() {
        when(salaRepository.findAll()).thenReturn(Arrays.asList(sala1));

        List<Sala> resultado = salaService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Box", resultado.get(0).getTipoDeSala());
        verify(salaRepository, times(1)).findAll();
    }

    @Test
    void obtenerSalaPorId() {
        when(salaRepository.findById(2L)).thenReturn(Optional.of(sala1));

        Optional<Sala> resultado = salaService.findById(2L);

        assertTrue(resultado.isPresent());
        assertEquals("Box", resultado.get().getTipoDeSala());
        assertEquals(10, resultado.get().getCapacidad());
        verify(salaRepository, times(1)).findById(2L);
    }

    @Test
    void guardarSala() {
        when(salaRepository.save(sala1)).thenReturn(sala1);

        Sala guardada = salaService.save(sala1);

        assertNotNull(guardada);
        assertEquals("Box", guardada.getTipoDeSala());
        assertEquals(10, guardada.getCapacidad());
        verify(salaRepository, times(1)).save(sala1);
    }

    @Test
    void tiposalavacia() {

        Sala salaVacia = new Sala(2L, "", 10);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            salaService.save(salaVacia);
        });

        assertEquals("El tipo de sala no puede estar vacío", exception.getMessage());
        verify(salaRepository,never()).save(any());

    }

    @Test
    void capacidadInvalida() {
        Sala salaCapacidadInvalida = new Sala(2L, "Box", 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            salaService.save(salaCapacidadInvalida);
        });

        assertEquals("La capacidad debe ser mayor a 0", exception.getMessage());
        verify(salaRepository,never()).save(any());
    }

    @Test
    void actualizarSala() {
        Sala salaActualizada = new Sala(null, "Box", 15);
        
        when(salaRepository.findById(anyLong())).thenReturn(Optional.of(sala1));
        when(salaRepository.save(any(Sala.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sala actualizada = salaService.update(2L, salaActualizada);

        assertNotNull(actualizada);
        assertEquals("Box", actualizada.getTipoDeSala());
        assertEquals(15, actualizada.getCapacidad());
        verify(salaRepository, times(1)).findById(2L);
    }

    @Test
    void salaNoEncontrada() {
        when(salaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            salaService.update(33L, sala1);
        });

        assertEquals("Sala no encontrada con ID: 33", exception.getMessage());
        verify(salaRepository, times(1)).findById(33L);
    }

    @Test
    void eliminarSala() {
        when(salaRepository.existsById(2L)).thenReturn(true);
        doNothing().when(salaRepository).deleteById(2L);

        assertDoesNotThrow(() -> salaService.deleteById(2L));
        verify(salaRepository, times(1)).existsById(2L);
    }

    @Test
    void eliminarSalaNoEncontrada() {
        when(salaRepository.existsById(anyLong())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            salaService.deleteById(33L);
        });

        assertEquals("Sala no encontrada con ID: 33", exception.getMessage());
        verify(salaRepository,never()).deleteById(anyLong());
    }

}
