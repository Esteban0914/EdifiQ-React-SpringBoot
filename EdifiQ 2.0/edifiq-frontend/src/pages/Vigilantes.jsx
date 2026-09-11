import { useCallback, useEffect, useState } from "react";
import { vigilantesApi } from "../services/api";
import EmptyState from "../components/EmptyState";
import FilterBar from "../components/FilterBar";
import Modal from "../components/Modal";
import ModuleHeader from "../components/ModuleHeader";

const createEmptyForm = () => ({
  idUsuario: null,
  idPersona: null,
  numeroDocumento: "",
  nombres: "",
  apellidos: "",
  telefono: "",
  correo: "",
  username: "",
  password: "",
  activo: true,
});

export default function Vigilantes() {
  const [rows, setRows] = useState([]);
  const [q, setQ] = useState("");
  const [estado, setEstado] = useState("");
  const [form, setForm] = useState(createEmptyForm);
  const [open, setOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      const data = await vigilantesApi.list({
        q,
        estado: estado === "" ? undefined : estado,
      });
      setRows(data);
    } catch (e) {
      setError(e.message);
    }
  }, [estado, q]);

  useEffect(() => {
    const timer = setTimeout(load, 180);
    return () => clearTimeout(timer);
  }, [load]);

  const openNew = () => {
    setForm(createEmptyForm());
    setError("");
    setOpen(true);
  };

  const openEdit = (row) => {
    setForm({
      ...createEmptyForm(),
      ...row,
      idUsuario: row.idUsuario,
      idPersona: row.idPersona,
      password: "",
    });
    setOpen(true);
  };

  const save = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      await vigilantesApi.save(form);
      setOpen(false);
      setForm(createEmptyForm());
      await load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  };

  const updateForm = (field, value) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  return (
    <>
      <ModuleHeader
        title="Vigilantes"
        description="Administra las cuentas del personal de portería."
        action={
          <button className="button primary" onClick={openNew}>
            + Nuevo vigilante
          </button>
        }
      />

      {error && <div className="alert error">{error}</div>}

      <div className="dashboard-card">
        <FilterBar
          search={q}
          onSearch={setQ}
          filters={[
            {
              name: "estado",
              value: estado,
              placeholder: "Todos los estados",
              onChange: setEstado,
              options: [
                { value: "1", label: "Activo" },
                { value: "2", label: "Inactivo" },
              ],
            },
          ]}
          onClear={() => {
            setQ("");
            setEstado("");
          }}
        />

        <div className="table-wrap">
          {rows.length === 0 ? (
            <EmptyState />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Documento</th>
                  <th>Usuario</th>
                  <th>Contacto</th>
                  <th>Estado</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.idUsuario}>
                    <td>
                      <strong>
                        {row.nombres} {row.apellidos}
                      </strong>
                    </td>
                    <td>{row.numeroDocumento}</td>
                    <td>{row.username}</td>
                    <td>
                      {row.telefono}
                      <small>{row.correo}</small>
                    </td>
                    <td>
                      <span
                        className={`status ${row.idEstado === 1 ? "success" : "muted"}`}
                      >
                        {row.estado}
                      </span>
                    </td>
                    <td className="actions">
                      <button onClick={() => openEdit(row)}>Editar</button>
                      <button
                        onClick={() =>
                          vigilantesApi
                            .toggle(row.idUsuario)
                            .then(load)
                            .catch((e) => setError(e.message))
                        }
                      >
                        {row.idEstado === 1 ? "Desactivar" : "Activar"}
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
        title={form.idUsuario ? "Editar vigilante" : "Nuevo vigilante"}
        onClose={() => setOpen(false)}
        onSubmit={save}
        loading={saving}
      >
        <div className="form-grid guard-form">
          <label>
            Número documento
            <input
              value={form.numeroDocumento}
              onChange={(event) =>
                updateForm("numeroDocumento", event.target.value)
              }
              required
            />
          </label>

          <label>
            Nombres
            <input
              value={form.nombres}
              onChange={(event) => updateForm("nombres", event.target.value)}
              required
            />
          </label>

          <label>
            Apellidos
            <input
              value={form.apellidos}
              onChange={(event) => updateForm("apellidos", event.target.value)}
              required
            />
          </label>

          <label>
            Teléfono
            <input
              type="tel"
              inputMode="numeric"
              value={form.telefono}
              onChange={(event) => updateForm("telefono", event.target.value)}
            />
          </label>

          <label>
            Correo
            <input
              type="email"
              value={form.correo}
              onChange={(event) => updateForm("correo", event.target.value)}
            />
          </label>

          <label>
            Usuario
            <input
              maxLength="50"
              value={form.username}
              onChange={(event) => updateForm("username", event.target.value)}
              required
            />
          </label>

          <label>
            Contraseña
            <input
              type="password"
              value={form.password}
              onChange={(event) => updateForm("password", event.target.value)}
              placeholder={
                form.idUsuario ? "Dejar vacía para mantenerla" : "Obligatoria"
              }
              required={!form.idUsuario}
            />
          </label>

          <label>
            Estado
            <select
              value={form.activo ? "1" : "0"}
              onChange={(event) =>
                updateForm("activo", event.target.value === "1")
              }
            >
              <option value="1">Activo</option>
              <option value="0">Inactivo</option>
            </select>
          </label>
        </div>
      </Modal>
    </>
  );
}
