package EdifiQ.service;

import EdifiQ.dto.ReservaRequest;
import EdifiQ.entity.ReservaZona;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.EstadoReservaRepository;
import EdifiQ.repository.ModuleQueryRepository;
import EdifiQ.repository.ReservaZonaRepository;
import EdifiQ.repository.ZonaComunRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservaService {
    private static final String RESERVA_NO_ENCONTRADA = "Reserva no encontrada";

    @PersistenceContext
    private EntityManager entityManager;

    private final ReservaZonaRepository repository;
    private final ModuleQueryRepository query;
    private final ZonaComunRepository zonaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final EstadoReservaRepository estadoRepository;

    public ReservaService(ReservaZonaRepository repository,
                          ModuleQueryRepository query,
                          ZonaComunRepository zonaRepository,
                          ApartamentoRepository apartamentoRepository,
                          EstadoReservaRepository estadoRepository) {
        this.repository = repository;
        this.query = query;
        this.zonaRepository = zonaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.estadoRepository = estadoRepository;
    }

    public List<Map<String, Object>> buscar(String q, Integer estado, Integer zona, Integer torre, LocalDate fecha, Integer apartamento) {
        return query.reservas(q, estado, zona, torre, fecha, apartamento).stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", ((Number) row[0]).intValue());
            m.put("zona", row[1]);
            m.put("fecha", row[2]);
            m.put("horaInicio", row[3]);
            m.put("horaFin", row[4]);
            m.put("invitados", row[5] == null ? 0 : ((Number) row[5]).intValue());
            m.put("estado", row[6]);
            m.put("apartamento", row[7]);
            m.put("torre", row[8]);
            m.put("idZona", ((Number) row[9]).intValue());
            m.put("idApartamento", ((Number) row[10]).intValue());
            m.put("idEstado", ((Number) row[11]).intValue());
            return m;
        }).toList();
    }

    @Transactional
    public void guardar(ReservaRequest request, Integer apartmentScope) {
        validate(request);
        if (apartmentScope != null && !apartmentScope.equals(request.idApartamento())) {
            throw new IllegalArgumentException("No puedes utilizar un apartamento diferente al tuyo");
        }

        Number overlapResult = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM reserva_zona
            WHERE id_zona = :zona
              AND fecha_reserva = :fecha
              AND id_estado_reserva <> 3
              AND (:id IS NULL OR id_reserva <> :id)
              AND hora_inicio < :horaFin
              AND hora_fin > :horaInicio
        """)
                .setParameter("zona", request.idZona())
                .setParameter("fecha", request.fechaReserva())
                .setParameter("id", request.idReserva())
                .setParameter("horaFin", request.horaFin())
                .setParameter("horaInicio", request.horaInicio())
                .getSingleResult();
            long overlaps = overlapResult.longValue();

        if (overlaps > 0) throw new IllegalArgumentException("La zona ya está reservada en ese horario");

        zonaRepository.findById(request.idZona()).orElseThrow(() -> new IllegalArgumentException("Zona inválida"));
        apartamentoRepository.findById(request.idApartamento()).orElseThrow(() -> new IllegalArgumentException("Apartamento inválido"));
        if (request.idEstadoReserva() != null) {
            estadoRepository.findById(request.idEstadoReserva()).orElseThrow(() -> new IllegalArgumentException("Estado de reserva inválido"));
        }

        ReservaZona reserva = request.idReserva() == null
                ? new ReservaZona()
                : repository.findById(request.idReserva()).orElseThrow(() -> new IllegalArgumentException(RESERVA_NO_ENCONTRADA));

        if (apartmentScope != null && !apartmentScope.equals(reserva.getIdApartamento()) && request.idReserva() != null) {
            throw new IllegalArgumentException("No puedes modificar una reserva de otro apartamento");
        }

        reserva.setFechaReserva(request.fechaReserva());
        reserva.setHoraInicio(request.horaInicio());
        reserva.setHoraFin(request.horaFin());
        Integer cantidadInvitados = request.cantidadInvitados();
        Integer estadoReserva = request.idEstadoReserva();
        reserva.setCantidadInvitados(cantidadInvitados == null ? 0 : cantidadInvitados);
        reserva.setIdEstadoReserva(estadoReserva == null ? 1 : estadoReserva);
        reserva.setIdZona(request.idZona());
        reserva.setIdApartamento(request.idApartamento());
        repository.save(reserva);
    }

    @Transactional
    public void cambiarEstado(Integer id, Integer estado) {
        if (estado == null || (estado != 2 && estado != 3)) {
            throw new IllegalArgumentException("Estado de reserva no válido para aprobación o rechazo");
        }
        estadoRepository.findById(estado)
                .orElseThrow(() -> new IllegalArgumentException("Estado de reserva inválido"));

        ReservaZona reserva = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(RESERVA_NO_ENCONTRADA));

        if (reserva.getIdEstadoReserva() == 3 && estado == 2) {
            throw new IllegalArgumentException("Una reserva cancelada no puede aprobarse");
        }
        reserva.setIdEstadoReserva(estado);
        repository.save(reserva);
    }

    @Transactional
    public void cancelar(Integer id, Integer apartmentScope) {
        ReservaZona reserva = repository.findById(id).orElseThrow(() -> new IllegalArgumentException(RESERVA_NO_ENCONTRADA));
        if (apartmentScope != null && !apartmentScope.equals(reserva.getIdApartamento())) {
            throw new IllegalArgumentException("No puedes cancelar una reserva de otro apartamento");
        }
        reserva.setIdEstadoReserva(3);
        repository.save(reserva);
    }

    @Transactional
    public void eliminar(Integer id) { repository.deleteById(id); }

    private void validate(ReservaRequest request) {
        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new IllegalArgumentException("La hora final debe ser posterior a la hora inicial");
        }
        if (request.cantidadInvitados() != null && request.cantidadInvitados() < 0) {
            throw new IllegalArgumentException("La cantidad de invitados no puede ser negativa");
        }
    }
}
