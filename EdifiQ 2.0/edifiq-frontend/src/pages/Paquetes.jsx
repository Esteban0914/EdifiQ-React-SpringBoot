import { useCallback, useEffect, useState } from "react";
import { catalogApi, paquetesApi, asociadosApi } from "../services/api";
import ModuleHeader from "../components/ModuleHeader";
import FilterBar from "../components/FilterBar";
import Modal from "../components/Modal";
import EmptyState from "../components/EmptyState";
import { useAuth } from "../context/useAuth";
const empty = {
  idPaquete: null,
  descripcion: "",
  remitente: "",
  fechaRecepcion: new Date().toISOString().slice(0, 16),
  fechaEntrega: "",
  idEstadoPaquete: 1,
  idApartamento: "",
  idPersona: "",
};
export default function Paquetes() {
  const { user } = useAuth();
  const canWrite = user.rol !== "Residente";
  const [rows, setRows] = useState([]),
    [cat, setCat] = useState({
      estadosPaquete: [],
      torres: [],
      apartamentos: [],
    }),
    [people, setPeople] = useState([]),
    [filters, setFilters] = useState({
      q: "",
      estado: "",
      torre: "",
      fecha: "",
    }),
    [form, setForm] = useState(empty),
    [apartmentTower, setApartmentTower] = useState(""),
    [open, setOpen] = useState(false),
    [saving, setSaving] = useState(false),
    [error, setError] = useState(""),
    [delivery, setDelivery] = useState({
      open: false,
      id: null,
      people: [],
      selected: "",
    });
  const load = useCallback(async () => {
    try {
      const [r, c] = await Promise.all([
        paquetesApi.list({
          q: filters.q,
          estado: filters.estado || undefined,
          torre: filters.torre || undefined,
          fecha: filters.fecha || undefined,
        }),
        catalogApi.all(),
      ]);
      setRows(r);
      setCat(c);
    } catch (e) {
      setError(e.message);
    }
  }, [filters.fecha, filters.estado, filters.q, filters.torre]);
  useEffect(() => {
    const t = setTimeout(load, 150);
    return () => clearTimeout(t);
  }, [load]);
  const loadPeople = async (apt) => {
    if (!apt) {
      setPeople([]);
      return;
    }
    try {
      setPeople(await asociadosApi.byApartment(Number(apt)));
    } catch (e) {
      setError(e.message);
    }
  };
  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (canWrite) {
        if (form.idPaquete) await paquetesApi.update(form.idPaquete, form)
        else await paquetesApi.create(form)
      }
      setOpen(false);
      await load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  };
  const openDelivery = async (r) => {
    try {
      const p = await paquetesApi.people(r.id);
      setDelivery({ open: true, id: r.id, people: p, selected: "" });
    } catch (e) {
      setError(e.message);
    }
  };
  const deliver = async () => {
    if (!delivery.selected) return;
    try {
      await paquetesApi.deliver(delivery.id, Number(delivery.selected));
      setDelivery({ open: false, id: null, people: [], selected: "" });
      await load();
    } catch (e) {
      setError(e.message);
    }
  };
  return (
    <>
      <ModuleHeader
        title="Paquetes"
        description="Cada entrega solo puede asignarse a una persona activa asociada al apartamento."
        action={
          canWrite && (
            <button
              className="button primary"
              onClick={() => {
                setForm(empty);
                setApartmentTower("");
                setPeople([]);
                setOpen(true);
              }}
            >
              + Registrar paquete
            </button>
          )
        }
      />
      {error && <div className="alert error">{error}</div>}
      <div className="dashboard-card">
        <FilterBar
          search={filters.q}
          onSearch={(v) => setFilters({ ...filters, q: v })}
          filters={[
            {
              name: "estado",
              value: filters.estado,
              placeholder: "Estado",
              onChange: (v) => setFilters({ ...filters, estado: v }),
              options: cat.estadosPaquete.map((x) => ({
                value: x.idEstadoPaquete,
                label: x.nombre,
              })),
            },
            {
              name: "torre",
              value: filters.torre,
              placeholder: "Torre",
              onChange: (v) => setFilters({ ...filters, torre: v }),
              options: cat.torres.map((x) => ({
                value: x.idTorre,
                label: x.nombreTorre,
              })),
            },
          ]}
          onClear={() =>
            setFilters({ q: "", estado: "", torre: "", fecha: "" })
          }
        />
        <div className="table-wrap">
          {rows.length === 0 ? (
            <EmptyState />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Paquete</th>
                  <th>Destino</th>
                  <th>Fecha</th>
                  <th>Estado</th>
                  <th>Recibido por</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.id}>
                    <td>
                      <strong>{r.descripcion}</strong>
                      <small>{r.remitente}</small>
                    </td>
                    <td>
                      {r.apartamento}
                      <small>{r.torre}</small>
                    </td>
                    <td>
                      {String(r.fechaRecepcion).replace("T", " ").slice(0, 16)}
                    </td>
                    <td>
                      <span
                        className={`status ${r.idEstado === 2 ? "success" : ""}`}
                      >
                        {r.estado}
                      </span>
                    </td>
                    <td>{r.persona || "Pendiente"}</td>
                    <td className="actions">
                      {canWrite && r.idEstado === 1 && (
                        <>
                          <button onClick={() => openDelivery(r)}>
                            Entregar
                          </button>
                          <button
                            onClick={() => {
                              setForm({
                                idPaquete: r.id,
                                descripcion: r.descripcion,
                                remitente: r.remitente,
                                fechaRecepcion: String(r.fechaRecepcion).slice(
                                  0,
                                  16,
                                ),
                                fechaEntrega: r.fechaEntrega
                                  ? String(r.fechaEntrega).slice(0, 16)
                                  : "",
                                idEstadoPaquete: r.idEstado,
                                idApartamento: r.idApartamento,
                                idPersona: r.idPersona || "",
                              });
                              setApartmentTower(
                                cat.apartamentos.find(
                                  (x) =>
                                    String(x.idApartamento) ===
                                    String(r.idApartamento),
                                )?.idTorre || "",
                              );
                              loadPeople(r.idApartamento);
                              setOpen(true);
                            }}
                          >
                            Editar
                          </button>
                        </>
                      )}
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
        title={form.idPaquete ? "Editar paquete" : "Registrar paquete"}
        onClose={() => setOpen(false)}
        onSubmit={save}
        loading={saving}
      >
        <div className="form-grid">
          <label>
            Descripción
            <input
              value={form.descripcion}
              onChange={(e) =>
                setForm({ ...form, descripcion: e.target.value })
              }
              readOnly={Boolean(form.idPaquete)}
              required
            />
          </label>
          <label>
            Remitente
            <input
              value={form.remitente}
              onChange={(e) => setForm({ ...form, remitente: e.target.value })}
              required
            />
          </label>
          <label>
            Torre
            <select
              value={apartmentTower}
              onChange={(e) => {
                setApartmentTower(e.target.value);
                setForm({ ...form, idApartamento: "", idPersona: "" });
                setPeople([]);
              }}
            >
              <option value="">Todas las torres</option>
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
              onChange={(e) => {
                setForm({
                  ...form,
                  idApartamento: e.target.value,
                  idPersona: "",
                });
                loadPeople(e.target.value);
              }}
              disabled={Boolean(form.idPaquete)}
              required
            >
              <option value="">Seleccione</option>
              {cat.apartamentos
                .filter(
                  (x) =>
                    x.activo &&
                    (!apartmentTower ||
                      String(x.idTorre) === String(apartmentTower)),
                )
                .map((x) => (
                  <option key={x.idApartamento} value={x.idApartamento}>
                    {x.numeroApartamento} · Torre #{x.idTorre}
                  </option>
                ))}
            </select>
          </label>
          <label>
            Persona destinataria
            <select
              value={form.idPersona}
              onChange={(e) => setForm({ ...form, idPersona: e.target.value })}
              disabled={Boolean(form.idPaquete)}
            >
              <option value="">Pendiente</option>
              {people.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nombres} {p.apellidos} · {p.tipoResidente}
                </option>
              ))}
            </select>
          </label>
          <label>
            Fecha recepción
            <input
              type="datetime-local"
              value={form.fechaRecepcion}
              onChange={(e) =>
                setForm({ ...form, fechaRecepcion: e.target.value })
              }
            />
          </label>
        </div>
      </Modal>
      <Modal
        open={delivery.open}
        title="Entregar paquete"
        onClose={() => setDelivery({ ...delivery, open: false })}
        onSubmit={(e) => {
          e.preventDefault();
          deliver();
        }}
        loading={false}
      >
        <label>
          ¿Quién reclama el paquete?
          <select
            value={delivery.selected}
            onChange={(e) =>
              setDelivery({ ...delivery, selected: e.target.value })
            }
            required
          >
            <option value="">Seleccione una persona asociada</option>
            {delivery.people.map((p) => (
              <option key={p.id} value={p.id}>
                {p.nombres} {p.apellidos} · {p.tipoResidente}
              </option>
            ))}
          </select>
        </label>
        {delivery.people.length === 0 && (
          <small className="field-help">
            No hay personas activas asociadas a este apartamento.
          </small>
        )}
      </Modal>
    </>
  );
}
