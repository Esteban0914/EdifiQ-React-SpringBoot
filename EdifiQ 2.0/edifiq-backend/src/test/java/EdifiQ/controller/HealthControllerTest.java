package EdifiQ.controller;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    @Test
    void healthShouldReturnApplicationStatusPayload() {
        HealthController controller = new HealthController();

        Map<String, String> response = controller.health();

        assertEquals(2, response.size());
        assertEquals("ok", response.get("status"));
        assertEquals("EdifiQ Backend", response.get("service"));
    }
}
