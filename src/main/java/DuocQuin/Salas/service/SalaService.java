package DuocQuin.Salas.service;

import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con Salas.
 */
@Service
@Transactional
public class SalaService {
    
    @Autowired
    private SalaRepository salaRepository;
    
    /**
     * Obtiene todas las salas.
     * @return Lista de todas las salas
     */
    public List<Sala> findAll() {
        return salaRepository.findAll();
    }
    
    /**
     * Obtiene una sala por su ID.
     * @param id El ID de la sala
     * @return Optional con la sala encontrada
     */
    public Optional<Sala> findById(Long id) {
        return salaRepository.findById(id);
    }
    
    /**
     * Guarda una nueva sala.
     * @param sala La sala a guardar
     * @return La sala guardada
     */
    public Sala save(Sala sala) {
        if (sala.getTipoDeSala() == null || sala.getTipoDeSala().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de sala no puede estar vacío");
        }
        if (sala.getCapacidad() == null || sala.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        return salaRepository.save(sala);
    }
    
    /**
     * Actualiza una sala existente.
     * @param id El ID de la sala a actualizar
     * @param salaDetails Los nuevos detalles de la sala
     * @return La sala actualizada
     */
    public Sala update(Long id, Sala salaDetails) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada con ID: " + id));
        
        if (salaDetails.getTipoDeSala() != null && !salaDetails.getTipoDeSala().trim().isEmpty()) {
            sala.setTipoDeSala(salaDetails.getTipoDeSala());
        }
        if (salaDetails.getCapacidad() != null && salaDetails.getCapacidad() > 0) {
            sala.setCapacidad(salaDetails.getCapacidad());
        }
        
        return salaRepository.save(sala);
    }
    
    /**
     * Elimina una sala por su ID.
     * @param id El ID de la sala a eliminar
     */
    public void deleteById(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new IllegalArgumentException("Sala no encontrada con ID: " + id);
        }
        salaRepository.deleteById(id);
    }
    
    /**
     * Busca una sala por su tipo.
     * @param tipoDeSala El tipo de sala a buscar
     * @return Optional con la sala encontrada
     */
    public Optional<Sala> findByTipoDeSala(String tipoDeSala) {
        return salaRepository.findByTipoDeSala(tipoDeSala);
    }
    
    /**
     * Busca salas cuyo tipo contenga el valor especificado.
     * @param tipo Parte del tipo de sala a buscar
     * @return Lista de salas encontradas
     */
    public List<Sala> findByTipoDeSalaContaining(String tipo) {
        return salaRepository.findByTipoDeSalaContaining(tipo);
    }
}
