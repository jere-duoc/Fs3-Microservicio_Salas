package DuocQuin.Salas.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.repository.SalaRepository;
import DuocQuin.Salas.service.SalaService;


@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ServicesIntegracionTest {
    
    @Autowired
    private SalaService salaService;

    @Autowired
    private SalaRepository salaRepository;

    private Sala sala1;

    @BeforeEach
    void setUp() {
        salaRepository.deleteAll();
    }

    @Test
    void buscaryguardarsala() {
        Sala salatest = new Sala(null, "Box", 6);
        Sala guardada = salaService.save(salatest);

        assertNotNull(guardada.getIdSala());
        assertEquals("Box", guardada.getTipoDeSala());
        assertEquals(6, guardada.getCapacidad());

            Optional<Sala> resultadobd = salaService.findById(guardada.getIdSala());
            assertTrue(resultadobd.isPresent());
            assertEquals(6, resultadobd.get().getCapacidad());

    }

    @Test
    void guardarcondatosinvalidos() {
        Sala salainvalida = new Sala(null, "Sala test", 0);

        assertThrows(Exception.class, () -> {
            salaService.save(salainvalida);
        });

        List<Sala> salas = salaService.findAll();
        assertTrue(salas.isEmpty());
    }
}
