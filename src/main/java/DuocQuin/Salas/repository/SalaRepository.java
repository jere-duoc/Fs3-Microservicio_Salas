package DuocQuin.Salas.repository;

import DuocQuin.Salas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Sala.
 * Proporciona operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    /**
     * Busca una sala por su tipo.
     * @param tipoDeSala El tipo de sala a buscar
     * @return Optional con la sala encontrada
     */
    Optional<Sala> findByTipoDeSala(String tipoDeSala);
    
    /**
     * Busca salas cuyo tipo contenga el valor especificado.
     * @param tipo Parte del tipo de sala a buscar
     * @return Lista de salas encontradas
     */
    @Query("SELECT s FROM Sala s WHERE s.tipoDeSala LIKE %:tipo%")
    List<Sala> findByTipoDeSalaContaining(@Param("tipo") String tipo);
    
    /**
     * Verifica si existe una sala con el tipo especificado.
     * @param tipoDeSala El tipo de sala a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByTipoDeSala(String tipoDeSala);
}
