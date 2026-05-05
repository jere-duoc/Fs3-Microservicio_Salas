package DuocQuin.Salas.controller;

import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar las operaciones relacionadas con Salas.
 */
@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaController {
    
    @Autowired
    private SalaService salaService;
    
    /**
     * Obtiene todas las salas disponibles.
     * @return ResponseEntity con la lista de salas
     */
    @GetMapping
    public ResponseEntity<List<Sala>> getAllSalas() {
        List<Sala> salas = salaService.findAll();
        return ResponseEntity.ok(salas);
    }
    
    /**
     * Obtiene una sala por su ID.
     * @param id El ID de la sala
     * @return ResponseEntity con la sala encontrada o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sala> getSalaById(@PathVariable Long id) {
        Optional<Sala> sala = salaService.findById(id);
        return sala.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crea una nueva sala.
     * @param sala Los datos de la sala a crear
     * @return ResponseEntity con la sala creada (201 Created)
     */
    @PostMapping
    public ResponseEntity<?> createSala(@Valid @RequestBody Sala sala) {
        try {
            Sala nuevaSala = salaService.save(sala);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSala);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(getErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse("Error al crear la sala"));
        }
    }
    
    /**
     * Actualiza una sala existente.
     * @param id El ID de la sala a actualizar
     * @param salaDetails Los nuevos datos de la sala
     * @return ResponseEntity con la sala actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSala(@PathVariable Long id, @Valid @RequestBody Sala salaDetails) {
        try {
            Sala salaActualizada = salaService.update(id, salaDetails);
            return ResponseEntity.ok(salaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(getErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse("Error al actualizar la sala"));
        }
    }
    
    /**
     * Elimina una sala por su ID.
     * @param id El ID de la sala a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSala(@PathVariable Long id) {
        try {
            salaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(getErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse("Error al eliminar la sala"));
        }
    }
    
    /**
     * Busca una sala por su tipo.
     * @param tipoSala El tipo de sala a buscar
     * @return ResponseEntity con la sala encontrada o 404 si no existe
     */
    @GetMapping("/tipo/{tipoSala}")
    public ResponseEntity<Sala> getSalaByTipo(@PathVariable String tipoSala) {
        Optional<Sala> sala = salaService.findByTipoDeSala(tipoSala);
        return sala.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Busca salas cuyo tipo contenga el valor especificado.
     * @param tipo Parte del tipo de sala a buscar
     * @return ResponseEntity con la lista de salas encontradas
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Sala>> buscarPorTipo(@RequestParam String tipo) {
        List<Sala> salas = salaService.findByTipoDeSalaContaining(tipo);
        return ResponseEntity.ok(salas);
    }
    
    /**
     * Método auxiliar para crear una respuesta de error estandarizada.
     * @param mensaje El mensaje de error
     * @return Map con la estructura de error
     */
    private Map<String, Object> getErrorResponse(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", mensaje);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
