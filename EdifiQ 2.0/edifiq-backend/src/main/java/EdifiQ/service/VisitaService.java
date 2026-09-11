package EdifiQ.service;

import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;

 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 import EdifiQ.dto.VisitaRequest;
import EdifiQ.entity.Visita;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.EstadoVisitaRepository;
import EdifiQ.repository.ModuleQueryRepository;
import EdifiQ.repository.TipoDocumentoRepository;
import EdifiQ.repository.TipoVisitaRepository;
import EdifiQ.repository.VisitaRepository;
@Service public class VisitaService {
 private final VisitaRepository repo; private final ModuleQueryRepository query; private final TipoVisitaRepository tipoRepo; private final TipoDocumentoRepository docRepo; private final EstadoVisitaRepository estadoRepo; private final ApartamentoRepository aptRepo; private final ApartmentScopeService scope;
 public VisitaService(VisitaRepository r,ModuleQueryRepository q,TipoVisitaRepository t,TipoDocumentoRepository d,EstadoVisitaRepository e,ApartamentoRepository a,ApartmentScopeService scope){repo=r;query=q;tipoRepo=t;docRepo=d;estadoRepo=e;aptRepo=a;this.scope=scope;}
 public List<Map<String,Object>> buscar(String q,Integer estado,Integer tipo,Integer torre,LocalDate fechaDesde,LocalDate fechaHasta,Integer apartamento){return query.visitas(q,estado,tipo,torre,fechaDesde,fechaHasta,apartamento).stream().map(r->{Map<String,Object> m=new LinkedHashMap<>();m.put("id",Integer.parseInt(String.valueOf(r[0])));m.put("nombreVisitante",r[1]);m.put("documentoVisitante",r[2]);m.put("tipo",r[3]);m.put("abreviaturaDocumento",r[4]);m.put("motivo",r[5]==null?"":r[5]);m.put("fechaIngreso",r[6]);m.put("fechaSalida",r[7]);m.put("estado",r[8]);m.put("apartamento",r[9]);m.put("torre",r[10]);m.put("idApartamento",Integer.parseInt(String.valueOf(r[11])));m.put("idTipoVisita",Integer.parseInt(String.valueOf(r[12])));m.put("idTipoDocumento",Integer.parseInt(String.valueOf(r[13])));m.put("idEstado",Integer.parseInt(String.valueOf(r[14])));m.put("autorizada",r[15] instanceof Boolean b?b:Integer.parseInt(String.valueOf(r[15]))==1);return m;}).toList();}
 @Transactional public void guardar(VisitaRequest req){tipoRepo.findById(req.idTipoVisita()).orElseThrow(()->new IllegalArgumentException("Tipo de visita inválido"));docRepo.findById(req.idTipoDocumento()).orElseThrow(()->new IllegalArgumentException("Tipo de documento inválido"));DatosValidationService.validarDocumento(req.idTipoDocumento(), req.documentoVisitante());aptRepo.findById(req.idApartamento()).orElseThrow(()->new IllegalArgumentException("Apartamento inválido"));Visita v=req.idVisita()==null?new Visita():repo.findById(req.idVisita()).orElseThrow(()->new IllegalArgumentException("Visita no encontrada"));if(req.idVisita()!=null && v.getIdEstadoVisita()==3) throw new IllegalArgumentException("La visita ya fue finalizada y no puede editarse");if(!req.nombreVisitante().trim().matches(".*[A-Za-zÁÉÍÓÚáéíóúÑñ].*")) throw new IllegalArgumentException("El nombre del visitante no es válido");v.setIdTipoVisita(req.idTipoVisita());v.setIdTipoDocumento(req.idTipoDocumento());v.setNombreVisitante(req.nombreVisitante().trim());v.setDocumentoVisitante(req.documentoVisitante().trim());v.setMotivoVisita(req.motivoVisita());v.setFechaIngreso(req.fechaIngreso()==null?LocalDateTime.now():req.fechaIngreso());v.setFechaSalida(req.fechaSalida());v.setIdApartamento(req.idApartamento());v.setIdEstadoVisita(req.idEstadoVisita()==null?1:req.idEstadoVisita());v.setAutorizada(req.autorizada()!=null && req.autorizada());repo.save(v);}
 @Transactional public void cambiarEstado(Integer id,Integer estado){estadoRepo.findById(estado).orElseThrow(()->new IllegalArgumentException("Estado inválido"));if(!List.of(1,2,3,4).contains(estado))throw new IllegalArgumentException("Estado de visita no permitido");Visita v=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Visita no encontrada"));if(v.getIdEstadoVisita()==3||v.getIdEstadoVisita()==4)throw new IllegalArgumentException("La visita ya está cerrada");if(estado==2&&!Boolean.TRUE.equals(v.getAutorizada()))throw new IllegalArgumentException("La visita no ha sido autorizada por el residente");v.setIdEstadoVisita(estado);if((estado==3||estado==4)&&v.getFechaSalida()==null)v.setFechaSalida(LocalDateTime.now());repo.save(v);}
 @Transactional public void autorizar(Integer id, boolean autorizada, Integer persona){ Visita v=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Visita no encontrada")); Integer apt=scope.activeApartmentForPerson(persona); if(apt==null || !apt.equals(v.getIdApartamento())) throw new IllegalArgumentException("No puedes modificar esta visita"); if(v.getIdEstadoVisita()==3) throw new IllegalArgumentException("La visita ya fue finalizada"); v.setAutorizada(autorizada); if(!autorizada && v.getIdEstadoVisita()==2) v.setIdEstadoVisita(1); repo.save(v); }
 @Transactional public void eliminar(Integer id){repo.deleteById(id);}
}
