package EdifiQ.exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

    @Test
    void genericErrorsShouldNotExposeInternalExceptionDetails() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.generic(
                new IllegalStateException("database credentials leaked")
        );

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error interno del servidor.", response.getBody().get("message"));
        assertFalse(response.getBody().containsKey("debugExceptionClass"));
        assertFalse(response.getBody().containsKey("debugExceptionMessage"));
        assertFalse(response.getBody().containsKey("debugCauseClass"));
        assertFalse(response.getBody().containsKey("debugCauseMessage"));
    }
}