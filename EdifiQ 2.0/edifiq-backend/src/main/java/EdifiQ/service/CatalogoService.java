package EdifiQ.service;

import EdifiQ.entity.Apartamento;
import EdifiQ.entity.EstadoPaquete;
import EdifiQ.entity.EstadoRecibo;
import EdifiQ.entity.EstadoReserva;
import EdifiQ.entity.EstadoUsuario;
import EdifiQ.entity.EstadoVisita;
import EdifiQ.entity.Rol;
import EdifiQ.entity.TipoDocumento;
import EdifiQ.entity.TipoResidente;
import EdifiQ.entity.TipoServicio;
import EdifiQ.entity.TipoVisita;
import EdifiQ.entity.Torre;
import EdifiQ.entity.ZonaComun;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.EstadoPaqueteRepository;
import EdifiQ.repository.EstadoReciboRepository;
import EdifiQ.repository.EstadoReservaRepository;
import EdifiQ.repository.EstadoUsuarioRepository;
import EdifiQ.repository.EstadoVisitaRepository;
import EdifiQ.repository.RolRepository;
import EdifiQ.repository.TipoDocumentoRepository;
import EdifiQ.repository.TipoResidenteRepository;
import EdifiQ.repository.TipoServicioRepository;
import EdifiQ.repository.TipoVisitaRepository;
import EdifiQ.repository.TorreRepository;
import EdifiQ.repository.ZonaComunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CatalogoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoResidenteRepository tipoResidenteRepository;
    private final TipoVisitaRepository tipoVisitaRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;
    private final EstadoVisitaRepository estadoVisitaRepository;
    private final EstadoPaqueteRepository estadoPaqueteRepository;
    private final EstadoReciboRepository estadoReciboRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final RolRepository rolRepository;
    private final TorreRepository torreRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ZonaComunRepository zonaComunRepository;

    public CatalogoService(
            TipoDocumentoRepository tipoDocumentoRepository,
            TipoResidenteRepository tipoResidenteRepository,
            TipoVisitaRepository tipoVisitaRepository,
            TipoServicioRepository tipoServicioRepository,
            EstadoUsuarioRepository estadoUsuarioRepository,
            EstadoVisitaRepository estadoVisitaRepository,
            EstadoPaqueteRepository estadoPaqueteRepository,
            EstadoReciboRepository estadoReciboRepository,
            EstadoReservaRepository estadoReservaRepository,
            RolRepository rolRepository,
            TorreRepository torreRepository,
            ApartamentoRepository apartamentoRepository,
            ZonaComunRepository zonaComunRepository
    ) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoResidenteRepository = tipoResidenteRepository;
        this.tipoVisitaRepository = tipoVisitaRepository;
        this.tipoServicioRepository = tipoServicioRepository;
        this.estadoUsuarioRepository = estadoUsuarioRepository;
        this.estadoVisitaRepository = estadoVisitaRepository;
        this.estadoPaqueteRepository = estadoPaqueteRepository;
        this.estadoReciboRepository = estadoReciboRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.rolRepository = rolRepository;
        this.torreRepository = torreRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.zonaComunRepository = zonaComunRepository;
    }

    /**
     * Devuelve exclusivamente DTOs/mapas planos. No exponemos entidades JPA
     * directamente desde el controlador, evitando problemas de serialización
     * y acoplamiento entre API y modelo de persistencia.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> all() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("tiposDocumento", tipoDocumentoRepository.findAll().stream()
                .map(this::tipoDocumento)
                .toList());

        response.put("tiposResidente", tipoResidenteRepository.findAll().stream()
                .map(this::tipoResidente)
                .toList());

        response.put("tiposVisita", tipoVisitaRepository.findAll().stream()
                .map(this::tipoVisita)
                .toList());

        response.put("tiposServicio", tipoServicioRepository.findAll().stream()
                .map(this::tipoServicio)
                .toList());

        response.put("estadosUsuario", estadoUsuarioRepository.findAll().stream()
                .map(this::estadoUsuario)
                .toList());

        response.put("estadosVisita", estadoVisitaRepository.findAll().stream()
                .map(this::estadoVisita)
                .toList());

        response.put("estadosPaquete", estadoPaqueteRepository.findAll().stream()
                .map(this::estadoPaquete)
                .toList());

        response.put("estadosRecibo", estadoReciboRepository.findAll().stream()
                .map(this::estadoRecibo)
                .toList());

        response.put("estadosReserva", estadoReservaRepository.findAll().stream()
                .map(this::estadoReserva)
                .toList());

        response.put("roles", rolRepository.findAll().stream()
                .map(this::rol)
                .toList());

        response.put("torres", torreRepository.findAll().stream()
                .map(this::torre)
                .toList());

        response.put("apartamentos", apartamentoRepository.findAll().stream()
                .map(this::apartamento)
                .toList());

        response.put("zonas", zonaComunRepository.findAll().stream()
                .map(this::zona)
                .toList());

        return response;
    }

    private Map<String, Object> tipoDocumento(TipoDocumento item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idTipoDocumento", item.getIdTipoDocumento());
        map.put("nombre", item.getNombre());
        map.put("abreviatura", item.getAbreviatura());
        return map;
    }

    private Map<String, Object> tipoResidente(TipoResidente item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idTipoResidente", item.getIdTipoResidente());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> tipoVisita(TipoVisita item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idTipoVisita", item.getIdTipoVisita());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> tipoServicio(TipoServicio item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idTipoServicio", item.getIdTipoServicio());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> estadoUsuario(EstadoUsuario item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idEstadoUsuario", item.getIdEstadoUsuario());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> estadoVisita(EstadoVisita item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idEstadoVisita", item.getIdEstadoVisita());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> estadoPaquete(EstadoPaquete item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idEstadoPaquete", item.getIdEstadoPaquete());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> estadoRecibo(EstadoRecibo item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idEstadoRecibo", item.getIdEstadoRecibo());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> estadoReserva(EstadoReserva item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idEstadoReserva", item.getIdEstadoReserva());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> rol(Rol item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idRol", item.getIdRol());
        map.put("nombre", item.getNombre());
        return map;
    }

    private Map<String, Object> torre(Torre item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idTorre", item.getIdTorre());
        map.put("nombreTorre", item.getNombreTorre());
        map.put("fechaCreacion", item.getFechaCreacion());
        return map;
    }

    private Map<String, Object> apartamento(Apartamento item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idApartamento", item.getIdApartamento());
        map.put("numeroApartamento", item.getNumeroApartamento());
        map.put("piso", item.getPiso());
        map.put("activo", item.getActivo());
        map.put("fechaCreacion", item.getFechaCreacion());
        map.put("fechaActualizacion", item.getFechaActualizacion());
        map.put("idTorre", item.getIdTorre());
        return map;
    }

    private Map<String, Object> zona(ZonaComun item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idZona", item.getIdZona());
        map.put("nombre", item.getNombre());
        map.put("descripcion", item.getDescripcion());
        return map;
    }
}
