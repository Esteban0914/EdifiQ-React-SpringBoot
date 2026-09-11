package EdifiQ.service;

import EdifiQ.repository.ModuleQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    private ModuleQueryRepository queryRepository;
    private EntityManager entityManager;
    private DashboardService service;

    @BeforeEach
    void setUp() throws Exception {
        queryRepository = mock(ModuleQueryRepository.class);
        entityManager = mock(EntityManager.class);
        service = new DashboardService(queryRepository);

        Field field = DashboardService.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(service, entityManager);
    }

    @Test
    void adminShouldAggregatePrimaryDashboardCounts() {
        Object[] data = {12L, 5L, 3L, 9L, 4L};
        when(queryRepository.dashboard()).thenReturn(data);

        Map<String, Object> result = service.admin();

        assertEquals(12L, result.get("personas"));
        assertEquals(5L, result.get("visitasHoy"));
        assertEquals(3L, result.get("paquetesHoy"));
        assertEquals(9L, result.get("recibosPendientes"));
        assertEquals(4L, result.get("reservasHoy"));
    }

    @Test
    void guardShouldIncludePendingCounts() {
        Object[] data = {12L, 5L, 3L, 9L, 4L};
        when(queryRepository.dashboard()).thenReturn(data);

        Query pendingVisits = mock(Query.class);
        Query pendingPackages = mock(Query.class);
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM visita WHERE id_estado_visita=1")).thenReturn(pendingVisits);
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM paquete WHERE id_estado_paquete=1")).thenReturn(pendingPackages);
        when(pendingVisits.getSingleResult()).thenReturn(7L);
        when(pendingPackages.getSingleResult()).thenReturn(2L);

        Map<String, Object> result = service.guard();

        assertEquals(12L, result.get("personas"));
        assertEquals(7L, result.get("visitasPendientes"));
        assertEquals(2L, result.get("paquetesPendientes"));
    }

    @Test
    void residentShouldReturnEmptySummaryWhenNoApartmentIsFound() {
        Query query = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", 99)).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of());

        Map<String, Object> result = service.resident(99);

        assertNull(result.get("apartamento"));
        assertEquals(0, result.get("paquetes"));
        assertEquals(0, result.get("visitasHoy"));
        assertEquals(0, result.get("reservas"));
        assertEquals(0, result.get("recibosPendientes"));
    }

    @Test
    void residentShouldReturnApartmentCountsWhenResidentHasAnApartment() {
        Query apartmentQuery = mock(Query.class);
        Query countQuery = mock(Query.class);

        when(entityManager.createNativeQuery(contains("apartamento_persona"))).thenReturn(apartmentQuery);
        when(entityManager.createNativeQuery(contains("SELECT COUNT(*) FROM paquete"))).thenReturn(countQuery);
        when(entityManager.createNativeQuery(contains("SELECT COUNT(*) FROM visita"))).thenReturn(countQuery);
        when(entityManager.createNativeQuery(contains("SELECT COUNT(*) FROM reserva_zona"))).thenReturn(countQuery);
        when(entityManager.createNativeQuery(contains("SELECT COUNT(*) FROM recibo"))).thenReturn(countQuery);

        when(apartmentQuery.setParameter("id", 8)).thenReturn(apartmentQuery);
        when(apartmentQuery.setMaxResults(1)).thenReturn(apartmentQuery);
        when(apartmentQuery.getResultList()).thenReturn(java.util.List.of(3));
        when(countQuery.getSingleResult()).thenReturn(8L);

        Map<String, Object> result = service.resident(8);

        assertEquals(3, result.get("apartamento"));
        assertEquals(8L, result.get("paquetes"));
        assertEquals(8L, result.get("visitasHoy"));
        assertEquals(8L, result.get("reservas"));
        assertEquals(8L, result.get("recibosPendientes"));
    }
}
