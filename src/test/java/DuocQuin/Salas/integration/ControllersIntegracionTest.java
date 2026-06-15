package DuocQuin.Salas.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import DuocQuin.Salas.model.Sala;
import DuocQuin.Salas.repository.SalaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_ctrl;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ControllersIntegracionTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SalaRepository salaRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/salas";
        salaRepository.deleteAll();
    }

    @Test
    void crearybuscarsala() {
        Sala nuevaSala = new Sala(null, "Box", 10);
        TestRestTemplate usuario_autorizado = restTemplate.withBasicAuth("user", "password");

        ResponseEntity<Sala> respuestaPost = usuario_autorizado.postForEntity(baseUrl, nuevaSala, Sala.class);

        assertEquals(HttpStatus.CREATED, respuestaPost.getStatusCode());
        
        Sala salaCreada = respuestaPost.getBody();
        assertNotNull(salaCreada);
        Long id = salaCreada.getIdSala();
        assertNotNull(id);

        ResponseEntity<Sala> respuestaGet = usuario_autorizado.getForEntity(baseUrl + "/" + id, Sala.class);

        assertEquals(HttpStatus.OK, respuestaGet.getStatusCode());
    
        Sala salaObtenida = respuestaGet.getBody();
        assertNotNull(salaObtenida);
        assertEquals("Box", salaObtenida.getTipoDeSala());
        assertEquals(10, salaObtenida.getCapacidad());
    }

    @Test
    void validacioninvalidahttp400() {
        Sala salaInvalida = new Sala(null, "", 10);
        TestRestTemplate usuario_autorizado = restTemplate.withBasicAuth("user", "password");

        ResponseEntity<String> respuestaPost = usuario_autorizado.postForEntity(baseUrl, salaInvalida, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, respuestaPost.getStatusCode());
    }

    @Test
    void actualizaryelimnarsalaexistente(){
        TestRestTemplate usuario_autorizado = restTemplate.withBasicAuth("user", "password");

        Sala sala = salaRepository.save(new Sala(null, "Box", 10));
        Long id = sala.getIdSala();
        assertNotNull(id);

        Sala detallesActualizados = new Sala(null, "Box 1", 20);
        HttpEntity<Sala> peticionPut = new HttpEntity<>(detallesActualizados);

        ResponseEntity<Sala> respuestaPut = usuario_autorizado.exchange(baseUrl + "/" + id, HttpMethod.PUT, peticionPut, Sala.class);

        assertEquals(HttpStatus.OK, respuestaPut.getStatusCode());
        Sala salaActualizada = respuestaPut.getBody();
        assertNotNull(salaActualizada);
        assertEquals("Box 1", salaActualizada.getTipoDeSala());

        ResponseEntity<Void> respuestaDelete = usuario_autorizado.exchange(baseUrl + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, respuestaDelete.getStatusCode());
        assertFalse(salaRepository.findById(id).isPresent());
    }

    @Test
    void buscarylistar(){
        TestRestTemplate usuario_autorizado = restTemplate.withBasicAuth("user", "password");

        Sala sala1 = new Sala(null, "Box", 10);
        Sala sala2 = new Sala(null, "Box 2", 20);

        salaRepository.save(sala1);
        salaRepository.save(sala2);

        ResponseEntity<Sala[]> respuestaListar = usuario_autorizado.getForEntity(baseUrl, Sala[].class);

        assertEquals(HttpStatus.OK, respuestaListar.getStatusCode());
        Sala[] salas = respuestaListar.getBody();
        assertNotNull(salas);
        assertEquals(2, salas.length);

        ResponseEntity<Sala> respuestaTipo = usuario_autorizado.getForEntity(baseUrl + "/" + sala1.getIdSala(), Sala.class);

        assertEquals(HttpStatus.OK, respuestaTipo.getStatusCode());
        Sala salaObtenida = respuestaTipo.getBody();
        assertNotNull(salaObtenida);
        assertEquals("Box", salaObtenida.getTipoDeSala());

        
        ResponseEntity<Sala[]> respuestaBuscar = usuario_autorizado.getForEntity(baseUrl + "/buscar?tipo=Box", Sala[].class);
        assertEquals(HttpStatus.OK, respuestaBuscar.getStatusCode());
        
        Sala[] salasBuscadas = respuestaBuscar.getBody();
        assertNotNull(salasBuscadas);
        assertTrue(salasBuscadas.length > 0);
        assertEquals("Box", salasBuscadas[0].getTipoDeSala());
    }
}