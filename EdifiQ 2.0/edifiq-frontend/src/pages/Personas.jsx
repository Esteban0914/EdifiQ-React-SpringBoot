import { useEffect, useState } from "react";
import { catalogApi, personasApi, asociadosApi } from "../services/api";
import { useAuth } from "../context/useAuth";
import ModuleHeader from "../components/ModuleHeader";
import FilterBar from "../components/FilterBar";
import Modal from "../components/Modal";
import StatsCards from "../components/StatsCards";
import EmptyState from "../components/EmptyState";

const docRules = {
  1: { min: 6, max: 10, numeric: true, hint: "CC: 6 a 10 dígitos" },
  2: { min: 10, max: 11, numeric: true, hint: "TI: 10 a 11 dígitos" },
  3: { min: 6, max: 10, numeric: true, hint: "CE: 6 a 10 dígitos" },
  4: {
    min: 6,
    max: 16,
    numeric: false,
    hint: "Pasaporte: 6 a 16 caracteres alfanuméricos",
  },
  5: { min: 10, max: 10, numeric: true, hint: "NIT: 10 dígitos incluido DV" },
};
const empty = {
  idPersona: null,
  idTipoDocumento: "",
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

export default function Personas() {
  const { user } = useAuth();
  const admin = user?.rol === "Administrador";
  const [rows, setRows] = useState([]);
  const [cat, setCat] = useState({
    tiposDocumento: [],
    tiposResidente: [],
    torres: [],
    apartamentos: [],
  });
  const [stats, setStats] = useState({});
  const [q, setQ] = useState("");
  const [filters, setFilters] = useState({ tipo: "", estado: "", torre: "" });
  const [form, setForm] = useState(empty);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [associated, setAssociated] = useState([]);
  const [associatedOpen, setAssociatedOpen] = useState(false);
  const [selectedPerson, setSelectedPerson] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");

    try {
      const requests = [
        personasApi.list({
          q,
          tipo: filters.tipo || undefined,
          estado: filters.estado === "" ? undefined : filters.estado,
          torre: filters.torre || undefined,
        }),
        catalogApi.all(),
      ];

      if (admin) requests.push(personasApi.stats());

      const [rowsData, catalog, statsData] = await Promise.all(requests);
      setRows(rowsData);
      setCat({
        tiposDocumento: catalog.tiposDocumento || [],
        tiposResidente: catalog.tiposResidente || [],
        torres: catalog.torres || [],
        apartamentos: catalog.apartamentos || [],
      });

      if (admin) setStats(statsData);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(load, 180);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [q, filters.tipo, filters.estado, filters.torre, admin]);

  const edit = (row) => {
    setForm({
      ...empty,
      idPersona: row.id,
      idTipoDocumento: row.idTipoDocumento,
      numeroDocumento: row.numeroDocumento,
      nombres: row.nombres,
      apellidos: row.apellidos,
      telefono: row.telefono || "",
      correo: row.correo || "",
      activo: row.activo,
      idTorre: row.idTorre || "",
      idApartamento: row.idApartamento || "",
      idTipoResidente: row.idTipoResidente || "",
      fechaIngreso: row.fechaIngreso || empty.fechaIngreso,
    });
    setOpen(true);
  };

  const save = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form };
      delete payload.idTorre;
      if (form.idPersona) await personasApi.update(form.idPersona, payload);
      else await personasApi.create(payload);
      setOpen(false);
      await load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  };

  const rule = docRules[form.idTipoDocumento] || {
    min: 6,
    max: 16,
    numeric: false,
    hint: "Entre 6 y 16 caracteres",
  };
  const availableApartments = cat.apartamentos.filter(
    (x) => x.activo && String(x.idTorre) === String(form.idTorre),
  );
  const updateDocumento = (value) =>
    setForm({
      ...form,
      numeroDocumento: rule.numeric
        ? value.replace(/\\D/g, "").slice(0, rule.max)
        : value.slice(0, rule.max),
    });

  return (
    <>
      <ModuleHeader
        title="Personas"
        description={
          admin
            ? "Administra residentes, propietarios, arrendatarios y familiares."
            : "Consulta las personas registradas y su apartamento."
        }
        action={
          admin && (
            <button
              className="button primary"
              onClick={() => {
                setForm(empty);
                setOpen(true);
              }}
            >
              + Nueva persona
            </button>
          )
        }
      />
      {error && <div className="alert error">{error}</div>}
      {admin && (
        <StatsCards
          items={[
            { label: "Total", value: stats.total, icon: "♙" },
            { label: "Activos", value: stats.activos, icon: "✓" },
          ]}
        />
      )}
      <div className="dashboard-card">
        <FilterBar
          search={q}
          onSearch={setQ}
          filters={[
            {
              name: "tipo",
              value: filters.tipo,
              placeholder: "Tipo residente",
              onChange: (v) => setFilters({ ...filters, tipo: v }),
              options: cat.tiposResidente.map((x) => ({
                value: x.idTipoResidente,
                label: x.nombre,
              })),
            },
            {
              name: "estado",
              value: filters.estado,
              placeholder: "Todos los estados",
              onChange: (v) => setFilters({ ...filters, estado: v }),
              options: [
                { value: "1", label: "Activo" },
                { value: "0", label: "Inactivo" },
              ],
            },
            {
              name: "torre",
              value: filters.torre,
              placeholder: "Todas las torres",
              onChange: (v) => setFilters({ ...filters, torre: v }),
              options: cat.torres.map((x) => ({
                value: x.idTorre,
                label: x.nombreTorre,
              })),
            },
          ]}
          onClear={() => {
            setQ("");
            setFilters({ tipo: "", estado: "", torre: "" });
          }}
        />
        <div className="table-wrap">
          {loading ? (
            <div className="loading">Cargando...</div>
          ) : rows.length === 0 ? (
            <EmptyState />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Persona</th>
                  <th>Documento</th>
                  <th>Ubicación</th>
                  <th>Tipo</th>
                  <th>Estado</th>
                  {admin && <th></th>}
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr
                    key={row.id}
                    onClick={async () => {
                      if (!admin || !row.idApartamento) return;
                      try {
                        setSelectedPerson(row);
                        setAssociated(await asociadosApi.byPerson(row.id));
                        setAssociatedOpen(true);
                      } catch (e) {
                        setError(e.message);
                      }
                    }}
                    style={{
                      cursor:
                        admin && row.idApartamento ? "pointer" : "default",
                    }}
                  >
                    <td>
                      <strong>
                        {row.nombres} {row.apellidos}
                      </strong>
                      <small>{row.correo || "Sin correo"}</small>
                    </td>
                    <td>
                      {row.numeroDocumento}
                      <small>{row.tipoDocumento}</small>
                    </td>
                    <td>
                      {row.apartamento || "Sin asignar"}
                      <small>{row.torre || ""}</small>
                    </td>
                    <td>{row.tipoResidente || "Sin asignar"}</td>
                    <td>
                      <span
                        className={`status ${row.activo ? "success" : "muted"}`}
                      >
                        {row.activo ? "Activo" : "Inactivo"}
                      </span>
                    </td>
                    {admin && (
                      <td className="actions">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            edit(row);
                          }}
                        >
                          Editar
                        </button>
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            personasApi
                              .toggle(row.id)
                              .then(load)
                              .catch((e) => setError(e.message));
                          }}
                        >
                          {row.activo ? "Desactivar" : "Activar"}
                        </button>
                        <button
                          className="danger-text"
                          onClick={(e) => {
                            e.stopPropagation();
                            if (
                              window.confirm(
                                `¿Desactivar a ${row.nombres} ${row.apellidos}?`,
                              )
                            )
                              personasApi
                                .remove(row.id)
                                .then(load)
                                .catch((e) => setError(e.message));
                          }}
                        >
                          Eliminar
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
      {admin && (
        <Modal
          open={open}
          title={form.idPersona ? "Editar persona" : "Nueva persona"}
          onClose={() => setOpen(false)}
          onSubmit={save}
          loading={saving}
        >
          <div className="form-grid person-form">
            <label>
              Tipo documento
              <select
                value={form.idTipoDocumento}
                onChange={(e) =>
                  setForm({ ...form, idTipoDocumento: e.target.value })
                }
                disabled={Boolean(form.idPersona)}
                required
              >
                <option value="">Seleccione</option>
                {cat.tiposDocumento.map((x) => (
                  <option key={x.idTipoDocumento} value={x.idTipoDocumento}>
                    {x.nombre}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Número documento
              <input
                minLength={rule.min}
                maxLength={rule.max}
                inputMode={rule.numeric ? "numeric" : "text"}
                value={form.numeroDocumento}
                onChange={(e) => updateDocumento(e.target.value)}
                readOnly={Boolean(form.idPersona)}
                required
              />
              <small className="field-help">{rule.hint}</small>
            </label>
            <label>
              Nombres
              <input
                value={form.nombres}
                onChange={(e) => setForm({ ...form, nombres: e.target.value })}
                readOnly={Boolean(form.idPersona)}
                required
              />
            </label>
            <label>
              Apellidos
              <input
                value={form.apellidos}
                onChange={(e) =>
                  setForm({ ...form, apellidos: e.target.value })
                }
                readOnly={Boolean(form.idPersona)}
                required
              />
            </label>
            <label>
              Teléfono
              <input
                type="tel"
                inputMode="numeric"
                minLength="10"
                maxLength="10"
                pattern="[0-9]{10}"
                value={form.telefono}
                onChange={(e) =>
                  setForm({
                    ...form,
                    telefono: e.target.value.replace(/\\D/g, "").slice(0, 10),
                  })
                }
                placeholder="3001234567"
              />
              <small className="field-help">Exactamente 10 dígitos.</small>
            </label>
            <label>
              Correo
              <input
                type="email"
                value={form.correo}
                onChange={(e) => setForm({ ...form, correo: e.target.value })}
              />
            </label>
            <label>
              Torre
              <select
                value={form.idTorre}
                onChange={(e) =>
                  setForm({
                    ...form,
                    idTorre: e.target.value,
                    idApartamento: "",
                  })
                }
              >
                <option value="">Seleccione</option>
                {cat.torres.map((x) => (
                  <option key={x.idTorre} value={x.idTorre}>
                    {x.nombreTorre}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Apartamento
              <select
                value={form.idApartamento}
                onChange={(e) =>
                  setForm({ ...form, idApartamento: e.target.value })
                }
                disabled={!form.idTorre}
              >
                <option value="">
                  {form.idTorre
                    ? "Sin asignar"
                    : "Seleccione una torre primero"}
                </option>
                {availableApartments.map((x) => (
                  <option key={x.idApartamento} value={x.idApartamento}>
                    Apto {x.numeroApartamento}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Tipo residente
              <select
                value={form.idTipoResidente}
                onChange={(e) =>
                  setForm({ ...form, idTipoResidente: e.target.value })
                }
              >
                <option value="">Sin asignar</option>
                {cat.tiposResidente.map((x) => (
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
                value={form.fechaIngreso}
                onChange={(e) =>
                  setForm({ ...form, fechaIngreso: e.target.value })
                }
              />
            </label>
          </div>
        </Modal>
      )}
      {admin && (
        <Modal
          open={associatedOpen}
          title={`Personas asociadas · ${selectedPerson?.nombres || ""} ${selectedPerson?.apellidos || ""}`}
          onClose={() => setAssociatedOpen(false)}
        >
          <div className="table-wrap">
            {associated.length === 0 ? (
              <EmptyState
                title="Sin asociados"
                description="No hay personas activas asociadas a este apartamento."
              />
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Persona</th>
                    <th>Documento</th>
                    <th>Tipo</th>
                  </tr>
                </thead>
                <tbody>
                  {associated.map((p) => (
                    <tr key={p.id}>
                      <td>
                        <strong>
                          {p.nombres} {p.apellidos}
                        </strong>
                      </td>
                      <td>{p.numeroDocumento}</td>
                      <td>{p.tipoResidente}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </Modal>
      )}
    </>
  );
}
