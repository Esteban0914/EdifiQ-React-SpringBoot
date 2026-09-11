package EdifiQ.service;

import EdifiQ.dto.ApartamentoRequest;
import EdifiQ.dto.TorreRequest;
import EdifiQ.dto.ZonaComunRequest;
import EdifiQ.entity.Apartamento;
import EdifiQ.entity.Torre;
import EdifiQ.entity.ZonaComun;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.TorreRepository;
import EdifiQ.repository.ZonaComunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class EstructuraService {
    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");
    private final TorreRepository torreRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ZonaComunRepository zonaRepository;

    public EstructuraService(TorreRepository torreRepository,
                             ApartamentoRepository apartamentoRepository,
                             ZonaComunRepository zonaRepository) {
        this.torreRepository = torreRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.zonaRepository = zonaRepository;
    }

    public List<Torre> torres() {
        return torreRepository.findAll();
    }

    public List<Apartamento> apartamentos() {
        return apartamentoRepository.findAll();
    }

    public List<ZonaComun> zonas() {
        return zonaRepository.findAll();
    }

    /**
     * Crea una o varias torres y genera automáticamente sus apartamentos.
     * Ejemplo: 5 pisos x 4 apartamentos => A1..A4, B1..B4 ... E1..E4.
     */
    @Transactional
    public Torre guardarTorre(TorreRequest req) {
        if (req.idTorre() != null) {
            return actualizarTorre(req);
        }

        int cantidadTorres = Objects.requireNonNullElse(req.cantidadTorres(), 1);
        int pisos = Objects.requireNonNullElse(req.pisos(), 5);
        int apartamentosPorPiso = Objects.requireNonNullElse(req.apartamentosPorPiso(), 4);
        validarParametrosTorre(cantidadTorres, pisos, apartamentosPorPiso);

        Torre primera = null;
        for (int i = 0; i < cantidadTorres; i++) {
            Torre torre = crearTorre(req.nombreTorre(), cantidadTorres, i);
            if (primera == null) primera = torre;
            crearApartamentos(torre, pisos, apartamentosPorPiso);
        }
        return primera;
    }

    private Torre actualizarTorre(TorreRequest request) {
        Torre torre = torreRepository.findById(request.idTorre())
                .orElseThrow(() -> new IllegalArgumentException("Torre no encontrada"));
        torre.setNombreTorre(request.nombreTorre().trim());
        return torreRepository.save(torre);
    }

    private void validarParametrosTorre(int cantidadTorres, int pisos, int apartamentosPorPiso) {
        if (cantidadTorres < 1 || cantidadTorres > 50) {
            throw new IllegalArgumentException("La cantidad de torres debe estar entre 1 y 50");
        }
        if (pisos < 1 || pisos > 26) {
            throw new IllegalArgumentException("La cantidad de pisos debe estar entre 1 y 26");
        }
        if (apartamentosPorPiso < 1 || apartamentosPorPiso > 20) {
            throw new IllegalArgumentException("Los apartamentos por piso deben estar entre 1 y 20");
        }
    }

    private Torre crearTorre(String base, int cantidadTorres, int indice) {
        String nombre = cantidadTorres == 1 ? base.trim() : base.trim() + " " + (indice + 1);
        if (nombre.length() > 20) {
            throw new IllegalArgumentException("El nombre generado de la torre no puede superar 20 caracteres: " + nombre);
        }
        if (torreRepository.existsByNombreTorreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe la torre: " + nombre);
        }

        Torre torre = new Torre();
        torre.setNombreTorre(nombre);
        torre.setFechaCreacion(LocalDateTime.now(COLOMBIA_ZONE));
        return torreRepository.save(torre);
    }

    private void crearApartamentos(Torre torre, int pisos, int apartamentosPorPiso) {
        for (int piso = 1; piso <= pisos; piso++) {
            for (int numero = 1; numero <= apartamentosPorPiso; numero++) {
                Apartamento apartamento = new Apartamento();
                apartamento.setNumeroApartamento(String.valueOf((piso * 100) + numero));
                apartamento.setPiso(piso);
                apartamento.setActivo(true);
                apartamento.setIdTorre(torre.getIdTorre());
                apartamentoRepository.save(apartamento);
            }
        }
    }

    @Transactional
    public void eliminarTorre(Integer id) {
        if (!torreRepository.existsById(id)) throw new IllegalArgumentException("Torre no encontrada");
        try {
            torreRepository.deleteById(id);
            torreRepository.flush();
        } catch (Exception _) {
            throw new IllegalArgumentException("No puedes eliminar la torre porque tiene apartamentos asociados");
        }
    }

    @Transactional
    public Apartamento guardarApartamento(ApartamentoRequest req) {
        torreRepository.findById(req.idTorre()).orElseThrow(() -> new IllegalArgumentException("Torre no válida"));
        Apartamento apartamento = req.idApartamento() == null
                ? new Apartamento()
                : apartamentoRepository.findById(req.idApartamento()).orElseThrow(() -> new IllegalArgumentException("Apartamento no encontrado"));
        apartamento.setNumeroApartamento(req.numeroApartamento().trim());
        apartamento.setPiso(req.piso());
        apartamento.setIdTorre(req.idTorre());
        apartamento.setActivo(req.activo() == null || req.activo());
        return apartamentoRepository.save(apartamento);
    }

    @Transactional
    public void toggleApartamento(Integer id) {
        Apartamento apartamento = apartamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apartamento no encontrado"));
        apartamento.setActivo(!Boolean.TRUE.equals(apartamento.getActivo()));
        apartamentoRepository.save(apartamento);
    }

    @Transactional
    public void eliminarApartamento(Integer id) {
        if (!apartamentoRepository.existsById(id)) throw new IllegalArgumentException("Apartamento no encontrado");
        try {
            apartamentoRepository.deleteById(id);
            apartamentoRepository.flush();
        } catch (Exception _) {
            throw new IllegalArgumentException("No puedes eliminar el apartamento porque tiene registros asociados");
        }
    }

    @Transactional
    public ZonaComun guardarZona(ZonaComunRequest req) {
        ZonaComun zona = req.idZona() == null
                ? new ZonaComun()
                : zonaRepository.findById(req.idZona()).orElseThrow(() -> new IllegalArgumentException("Zona no encontrada"));
        zona.setNombre(req.nombre().trim());
        zona.setDescripcion(req.descripcion().trim());
        return zonaRepository.save(zona);
    }

    @Transactional
    public void eliminarZona(Integer id) {
        if (!zonaRepository.existsById(id)) throw new IllegalArgumentException("Zona no encontrada");
        try {
            zonaRepository.deleteById(id);
            zonaRepository.flush();
        } catch (Exception _) {
            throw new IllegalArgumentException("No puedes eliminar la zona porque tiene reservas asociadas");
        }
    }
}
