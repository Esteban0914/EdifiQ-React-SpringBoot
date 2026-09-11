import { useCallback, useEffect, useMemo, useState } from "react";
import {
  catalogApi,
  estructuraApi,
  personasApi,
  usuariosApi,
} from "../services/api";
import ModuleHeader from "../components/ModuleHeader";
import FilterBar from "../components/FilterBar";
import Modal from "../components/Modal";
import EmptyState from "../components/EmptyState";

const empty = {
  idUsuario: null,
  idPersona: null,
  idTipoDocumento: "",
  username: "",
  password: "",
  idRol: "",
  idEstadoUsuario: 1,
  numeroDocumento: "",
  nombres: "",
  apellidos: "",
  telefono: "",
  correo: "",
  activo: true,
  idTorre: "",
  idApartamento: "",
  idTipoResidente: "",
  fechaIngreso: new Date().toISOString().slice(0, 10),
};

export default function Usuarios() {
  const [rows, setRows] = useState([]);
  const [personas, setPersonas] = useState([]);
  const [cat, setCat] = useState({
    roles: [],
    estadosUsuario: [],
    tiposDocumento: [],
    tiposResidente: [],
    apartamentos: [],
  });
  const [filters, setFilters] = useState({ q: "", rol: "", estado: "" });
  const [form, setForm] = useState(empty);
  const [open, setOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      const [data, catalog, apartamentos, people] = await Promise.all([
        usuariosApi.list({
          q: filters.q,
          rol: filters.rol || undefined,
          estado: filters.estado || undefined,
        }),
        catalogApi.all(),
        estructuraApi.apartamentos(),
        personasApi.list(),
      ]);
      setRows(data);
      setPersonas(people);
      setCat({ ...catalog, apartamentos });
    } catch (e) {
      setError(e.message);
    }
  }, [filters.estado, filters.q, filters.rol]);

  useEffect(() => {
    const t = setTimeout(load, 180);
    return () => clearTimeout(t);
  }, [load]);

  const personaOptions = useMemo(() => {
    const assigned = new Set(
      rows
        .filter((r) => r.idUsuario !== form.idUsuario)
        .map((r) => r.idPersona),
    );
    return personas.filter((p) => !assigned.has(p.id));
  }, [personas, rows, form.idUsuario]);

  const selectPersona = (id) => {
    const persona = personas.find((p) => String(p.id) === String(id));
    if (!persona) {
      setForm((f) => ({
        ...f,
        idPersona: "",
        idTipoDocumento: "",
        numeroDocumento: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        correo: "",
      }));
      return;
    }
    setForm((f) => ({
      ...f,
      idPersona: persona.id,
      idTipoDocumento: persona.idTipoDocumento,
      numeroDocumento: persona.numeroDocumento || "",
      nombres: persona.nombres || "",
      apellidos: persona.apellidos || "",
      telefono: persona.telefono || "",
      correo: persona.correo || "",
      idTorre: persona.idTorre || "",
      idApartamento: persona.idApartamento || "",
    }));
  };

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form };
      delete payload.idTorre;
      form.idUsuario
        ? await usuariosApi.update(form.idUsuario, payload)
        : await usuariosApi.create(payload);
      setOpen(false);
      setForm(empty);
      await load();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const openNew = () => {
    setForm(empty);
    setError("");
    setOpen(true);
  };
  const openEdit = (r) => {
    setForm({
      ...empty,
      ...r,
      idUsuario: r.idUsuario,
      idPersona: r.idPersona,
      idTorre: r.idTorre || "",
      password: "",
      idTipoDocumento: r.idTipoDocumento || 1,
      idEstadoUsuario: r.idEstadoUsuario,
      activo: r.idEstadoUsuario === 1,
    });
    setOpen(true);
  };

  const availableApartments = (cat.apartamentos || []).filter(
    (apartment) =>
      apartment.activo && String(apartment.idTorre) === String(form.idTorre),
  );

  return (
    <>
      <ModuleHeader
        title="Usuarios"
        description="Cada cuenta se vincula a una persona existente. Al seleccionarla, sus datos se cargan automáticamente."
        action={
          <button className="button primary" onClick={openNew}>
            + Nuevo usuario
          </button>
        }
      />
      {error && <div className="alert error">{error}</div>}
      <div className="dashboard-card">
        <FilterBar
          search={filters.q}
          onSearch={(v) => setFilters({ ...filters, q: v })}
          filters={[
            {
              name: "rol",
              value: filters.rol,
              placeholder: "Todos los roles",
              onChange: (v) => setFilters({ ...filters, rol: v }),
              options: (cat.roles || []).map((x) => ({
                value: x.idRol,
                label: x.nombre,
              })),
            },
            {
              name: "estado",
              value: filters.estado,
              placeholder: "Todos los estados",
              onChange: (v) => setFilters({ ...filters, estado: v }),
              options: (cat.estadosUsuario || []).map((x) => ({
                value: x.idEstadoUsuario,
                label: x.nombre,
              })),
            },
          ]}
          onClear={() => setFilters({ q: "", rol: "", estado: "" })}
        />
        <div className="table-wrap">
          {rows.length === 0 ? (
            <EmptyState />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Persona</th>
                  <th>Rol</th>
                  <th>Ubicación</th>
                  <th>Estado</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.idUsuario}>
                    <td>
                      <strong>{r.username}</strong>
                      <small>{r.correo}</small>
                    </td>
                    <td>
                      {r.nombres} {r.apellidos}
                      <small>{r.numeroDocumento}</small>
                    </td>
                    <td>{r.rol}</td>
                    <td>
                      {r.apartamento || "Sin asignar"}
                      <small>{r.torre || ""}</small>
                    </td>
                    <td>
                      <span
                        className={`status ${r.idEstadoUsuario === 1 ? "success" : "muted"}`}
                      >
                        {r.estado}
                      </span>
                    </td>
                    <td className="actions">
                      <button onClick={() => openEdit(r)}>Editar</button>
                      <button
                        onClick={() =>
                          usuariosApi
                            .toggle(r.idUsuario)
                            .then(load)
                            .catch((e) => setError(e.message))
                        }
                      >
                        {r.idEstadoUsuario === 1 ? "Desactivar" : "Activar"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <Modal
        open={open}
        title={form.idUsuario ? "Editar usuario" : "Nuevo usuario"}
        onClose={() => setOpen(false)}
        onSubmit={save}
        loading={saving}
      >
        <div className="form-grid user-form">
          <label>
            Persona existente
            <select
              value={form.idPersona || ""}
              onChange={(e) => selectPersona(e.target.value)}
              required
            >
              <option value="">Seleccione una persona</option>
              {personaOptions.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nombres} {p.apellidos} · {p.numeroDocumento}
                </option>
              ))}
            </select>
            <small className="field-help">
              Los datos de documento, nombre, teléfono y correo se copian de
              Personas.
            </small>
          </label>
          <label>
            Usuario
            <input
              maxLength="50"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              placeholder={
                form.idUsuario ? "Dejar vacía para mantenerla" : "Obligatoria"
              }
              required={!form.idUsuario}
            />
          </label>
          <label>
            Rol
            <select
              value={form.idRol}
              onChange={(e) => setForm({ ...form, idRol: e.target.value })}
              required
            >
              <option value="">Seleccione</option>
              {(cat.roles || []).map((x) => (
                <option key={x.idRol} value={x.idRol}>
                  {x.nombre}
                </option>
              ))}
            </select>
          </label>
          <label>
            Estado
            <select
              value={form.idEstadoUsuario}
              onChange={(e) =>
                setForm({ ...form, idEstadoUsuario: e.target.value })
              }
            >
              {(cat.estadosUsuario || []).map((x) => (
                <option key={x.idEstadoUsuario} value={x.idEstadoUsuario}>
                  {x.nombre}
                </option>
              ))}
            </select>
          </label>

          <label>
            Tipo documento
            <input
              value={
                cat.tiposDocumento?.find(
                  (x) =>
                    String(x.idTipoDocumento) === String(form.idTipoDocumento),
                )?.nombre || ""
              }
              disabled
            />
          </label>
          <label>
            Número documento
            <input value={form.numeroDocumento} disabled />
          </label>
          <label>
            Nombres
            <input value={form.nombres} disabled />
          </label>
          <label>
            Apellidos
            <input value={form.apellidos} disabled />
          </label>
          <label>
            Teléfono
            <input value={form.telefono} disabled />
          </label>
          <label>
            Correo
            <input value={form.correo} disabled />
          </label>

          <label>
            Torre
            <select
              value={form.idTorre || ""}
              onChange={(e) =>
                setForm({
                  ...form,
                  idTorre: e.target.value,
                  idApartamento: "",
                })
              }
            >
              <option value="">Seleccione</option>
              {(cat.torres || []).map((tower) => (
                <option key={tower.idTorre} value={tower.idTorre}>
                  {tower.nombreTorre}
                </option>
              ))}
            </select>
          </label>
          <label>
            Apartamento
            <select
              value={form.idApartamento || ""}
              onChange={(e) =>
                setForm({ ...form, idApartamento: e.target.value })
              }
              disabled={!form.idTorre}
            >
              <option value="">
                {form.idTorre ? "Sin asignar" : "Seleccione una torre primero"}
              </option>
              {availableApartments.map((apartment) => (
                <option
                  key={apartment.idApartamento}
                  value={apartment.idApartamento}
                >
                  Apto {apartment.numeroApartamento}
                </option>
              ))}
            </select>
          </label>
          <label>
            Tipo residente
            <select
              value={form.idTipoResidente || ""}
              onChange={(e) =>
                setForm({ ...form, idTipoResidente: e.target.value })
              }
            >
              <option value="">Sin asignar</option>
              {(cat.tiposResidente || []).map((x) => (
                <option key={x.idTipoResidente} value={x.idTipoResidente}>
                  {x.nombre}
                </option>
              ))}
            </select>
          </label>
          <label>
            Fecha ingreso
            <input
              type="date"
              value={form.fechaIngreso || ""}
              onChange={(e) =>
                setForm({ ...form, fechaIngreso: e.target.value })
              }
            />
          </label>
        </div>
      </Modal>
    </>
  );
}
