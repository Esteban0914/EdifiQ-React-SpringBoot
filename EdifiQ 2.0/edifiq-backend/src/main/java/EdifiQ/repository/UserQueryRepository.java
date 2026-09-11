package EdifiQ.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserQueryRepository {
 @PersistenceContext private EntityManager em;
 public List<Object[]> vigilantes(String q,Integer estado){
  String sql="""
   SELECT u.id_usuario,p.id_persona,p.nombres,p.apellidos,p.numero_documento,p.telefono,p.correo,u.username,eu.nombre,u.id_estado_usuario
   FROM usuario u JOIN persona p ON p.id_persona=u.id_persona JOIN rol r ON r.id_rol=u.id_rol JOIN estado_usuario eu ON eu.id_estado_usuario=u.id_estado_usuario
   WHERE r.nombre='Vigilante' AND (:q='' OR p.nombres LIKE CONCAT('%',:q,'%') OR p.apellidos LIKE CONCAT('%',:q,'%') OR p.numero_documento LIKE CONCAT('%',:q,'%') OR u.username LIKE CONCAT('%',:q,'%'))
     AND (:estado IS NULL OR u.id_estado_usuario=:estado) ORDER BY p.apellidos,p.nombres
  """;var x=em.createNativeQuery(sql);x.setParameter("q",q==null?"":q.trim());x.setParameter("estado",estado);return x.getResultList();
 }
}
