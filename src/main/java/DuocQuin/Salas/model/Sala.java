package DuocQuin.Salas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una Sala en el sistema.
 */
@Entity
@Table(name = "salas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sala {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long idSala;
    
    @NotBlank(message = "El tipo de sala es obligatorio")
    @Column(name = "tipo_de_sala", nullable = false, length = 100)
    private String tipoDeSala;
    
    @Positive(message = "La capacidad debe ser un número positivo")
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;
}
