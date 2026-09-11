import { useCallback, useEffect, useState } from "react";
import { catalogApi, visitasApi, asociadosApi } from "../services/api";
import ModuleHeader from "../components/ModuleHeader";
import FilterBar from "../components/FilterBar";
import Modal from "../components/Modal";
import EmptyState from "../components/EmptyState";
import { useAuth } from "../context/useAuth";
const empty = {
  idVisita: null,
  idTipoVisita: "",
  idTipoDocumento: "1",
  nombreVisitante: "",
  documentoVisitante: "",
  motivoVisita: "",
  fechaIngreso: new Date().toISOString().slice(0, 16),
  fechaSalida: "",
  idEstadoVisita: 1,
  autorizada: true,
  idApartamento: "",
};
export default function Visitas() {
  const { user } = useAuth();
  const resident = user.rol === "Residente";
  const canWrite = !resident;
  const [rows, setRows] = useState([]),
    [cat, setCat] = useState({
      tiposVisita: [],
      tiposDocumento: [],
      estadosVisita: [],
      torres: [],
      apartamentos: [],
    }),
    [filters, setFilters] = useState({
      q: "",
      estado: "",
      tipo: "",
      torre: "",
      fechaDesde: "",
      fechaHasta: "",
    }),
    [form, setForm] = useState(empty),
    [apartmentTower, setApartmentTower] = useState(""),
    [open, setOpen] = useState(false),
    [saving, setSaving] = useState(false),
    [error, setError] = useState(""),
    [formError, setFormError] = useState(""),
    [myApt, setMyApt] = useState(null),
    [reportRows, setReportRows] = useState([]),
    [printing, setPrinting] = useState(false);
  const matchesDateRange = useCallback((visit) => {
    const visitDate = String(visit.fechaIngreso).slice(0, 10);
    return (!filters.fechaDesde || visitDate >= filters.fechaDesde) &&
      (!filters.fechaHasta || visitDate <= filters.fechaHasta);
  }, [filters.fechaDesde, filters.fechaHasta]);
  const load = useCallback(async () => {
    try {
      const [vis, c] = await Promise.all([
        visitasApi.list({
          q: filters.q,
          estado: filters.estado || undefined,
          tipo: filters.tipo || undefined,
          torre: filters.torre || undefined,
          fechaDesde: filters.fechaDesde || undefined,
          fechaHasta: filters.fechaHasta || undefined,
        }),
        catalogApi.all(),
      ]);
      setRows(vis.filter(matchesDateRange));
      setCat(c);
      if (resident) {
        const people = await asociadosApi.mine();
        const principal = people.find((x) => x.idTipoResidente !== 3);
        if (principal) setMyApt(principal.idApartamento);
      }
    } catch (e) {
      setError(e.message);
    }
  }, [filters, matchesDateRange, resident]);
  useEffect(() => {
    const t = setTimeout(load, 150);
    return () => clearTimeout(t);
  }, [load]);
  useEffect(() => {
    if (!printing) return undefined;
    const finishPrint = () => setPrinting(false);
    window.addEventListener("afterprint", finishPrint);
    return () => window.removeEventListener("afterprint", finishPrint);
  }, [printing]);
  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    try {
      await visitasApi.create({
        ...form,
        idApartamento: resident ? myApt : form.idApartamento,
        autorizada: form.autorizada,
      });
      setOpen(false);
      setForm(empty);
      await load();
    } catch (e) {
      setFormError(e.message);
    } finally {
      setSaving(false);
    }
  };
  const authorize = async (r, v) => {
    try {
      await visitasApi.authorize(r.id, v);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };
  const changeStatus = async (id, status) => {
    try {
      await visitasApi.status(id, status);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };
  const statusOptions = (row) => cat.estadosVisita
    .filter((item) => [2, 3, 4].includes(Number(item.idEstadoVisita)) && Number(item.idEstadoVisita) !== row.idEstado)
    .map((item) => <option key={item.idEstadoVisita} value={item.idEstadoVisita}>{item.nombre}</option>);
  const generateReport = async () => {
    if (!filters.fechaDesde || !filters.fechaHasta) {
      setError("Selecciona la fecha inicial y la fecha final.");
      return;
    }
    if (filters.fechaDesde > filters.fechaHasta) {
      setError("La fecha inicial no puede ser posterior a la fecha final.");
      return;
    }
    setError("");
    try {
      const report = await visitasApi.list({
        q: filters.q,
        estado: filters.estado || undefined,
        tipo: filters.tipo || undefined,
        torre: filters.torre || undefined,
        fechaDesde: filters.fechaDesde,
        fechaHasta: filters.fechaHasta,
      });
      setReportRows(report.filter(matchesDateRange));
      setPrinting(true);
      setTimeout(() => window.print(), 100);
    } catch (e) {
      setError(e.message);
    }
  };
  return (
    <>
      <ModuleHeader
        title="Visitas"
        description={
          resident
            ? "Registra tus visitantes y decide quién está autorizado a ingresar."
            : "Controla ingresos, autorizaciones y salidas."
        }
        action={
          <div className="actions">
            {!resident && <button className="button" onClick={generateReport}>Generar reporte PDF</button>}
            <button
              className="button primary"
              onClick={() => {
              setForm({ ...empty, idApartamento: myApt || "" });
              setApartmentTower("");
              setFormError("");
              setOpen(true);
              }}
            >+ Registrar visita</button>
          </div>
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
                    options: cat.estadosVisita.map((x) => ({
                      value: x.idEstadoVisita,
                      label: x.nombre,
                    })),
                  },
                  ...(canWrite ? [
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
                  ] : []),
          ]}
          onClear={() =>
            setFilters({ q: "", estado: "", tipo: "", torre: "", fechaDesde: "", fechaHasta: "" })
          }
        />
        {!resident && <div className="date-range-bar">
          <label>Desde<input type="date" value={filters.fechaDesde} onChange={(e) => setFilters({ ...filters, fechaDesde: e.target.value })} /></label>
          <label>Hasta<input type="date" value={filters.fechaHasta} onChange={(e) => setFilters({ ...filters, fechaHasta: e.target.value })} /></label>
        </div>}
        <div className="table-wrap">
          {rows.length === 0 ? (
            <EmptyState />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Visitante</th>
                  <th>Apartamento</th>
                  <th>Ingreso</th>
                  <th>Autorización</th>
                  <th>Estado</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.id}>
                    <td>
                      <strong>{r.nombreVisitante}</strong>
                      <small>{r.documentoVisitante}</small>
                    </td>
                    <td>
                      {r.apartamento}
                      <small>{r.torre}</small>
                    </td>
                    <td>
                      {String(r.fechaIngreso).replace("T", " ").slice(0, 16)}
                    </td>
                    <td>
                      <span
                        className={`status ${r.autorizada ? "success" : "muted"}`}
                      >
                        {r.autorizada ? "Autorizada" : "No autorizada"}
                      </span>
                    </td>
                    <td><span className={`status status-${r.idEstado}`}>{r.estado}</span></td>
                    <td className="actions">
                      {resident && r.idEstado === 1 && (
                        <>
                          <button onClick={() => authorize(r, true)}>
                            Autorizar
                          </button>
                          <button
                            className="danger-text"
                            onClick={() => authorize(r, false)}
                          >
                            No autorizar
                          </button>
                        </>
                      )}
                      {canWrite && ![3, 4].includes(r.idEstado) && <select className="status-action" value="" onChange={(e) => e.target.value && changeStatus(r.id, Number(e.target.value))} aria-label={`Cambiar estado de ${r.nombreVisitante}`}><option value="">Cambiar estado</option>{statusOptions(r)}</select>}
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
        title="Registrar visita"
        onClose={() => setOpen(false)}
        onSubmit={save}
        loading={saving}
      >
        {formError && <div className="alert error">{formError}</div>}
        <div className="form-grid">
          <label>
            Tipo visita
            <select
              value={form.idTipoVisita}
              onChange={(e) =>
                setForm({ ...form, idTipoVisita: e.target.value })
              }
              required
            >
              <option value="">Seleccione</option>
              {cat.tiposVisita.map((x) => (
                <option key={x.idTipoVisita} value={x.idTipoVisita}>
                  {x.nombre}
                </option>
              ))}
            </select>
          </label>
          <label>
            Tipo documento
            <select
              value={form.idTipoDocumento}
              onChange={(e) =>
                setForm({ ...form, idTipoDocumento: e.target.value })
              }
              required
            >
              {cat.tiposDocumento.map((x) => (
                <option key={x.idTipoDocumento} value={x.idTipoDocumento}>
                  {x.nombre}
                </option>
              ))}
            </select>
          </label>
          <label>
            Nombre visitante
            <input
              value={form.nombreVisitante}
              onChange={(e) =>
                setForm({ ...form, nombreVisitante: e.target.value })
              }
              required
            />
          </label>
          <label>
            Documento
            <input
              value={form.documentoVisitante}
              onChange={(e) =>
                setForm({
                  ...form,
                  documentoVisitante: e.target.value
                    .replace(/\D/g, "")
                    .slice(0, 16),
                })
              }
              required
            />
          </label>
          {!resident && (
            <label>
              Torre
              <select
                value={apartmentTower}
                onChange={(e) => {
                  setApartmentTower(e.target.value);
                  setForm({ ...form, idApartamento: "" });
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
          )}
          {!resident && (
            <label>
              Apartamento
              <select
                value={form.idApartamento}
                onChange={(e) =>
                  setForm({ ...form, idApartamento: e.target.value })
                }
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
          )}
          <label>
            Motivo
            <input
              value={form.motivoVisita}
              onChange={(e) =>
                setForm({ ...form, motivoVisita: e.target.value })
              }
            />
          </label>
          <label>
            Ingreso
            <input
              type="datetime-local"
              value={form.fechaIngreso}
              onChange={(e) =>
                setForm({ ...form, fechaIngreso: e.target.value })
              }
              required
            />
          </label>
          {resident && (
            <label className="checkbox-field">
              <input
                type="checkbox"
                checked={form.autorizada}
                onChange={(e) =>
                  setForm({ ...form, autorizada: e.target.checked })
                }
              />{" "}
              Autorizar esta visita
            </label>
          )}
        </div>
      </Modal>
      {printing && <div className="print-report">
        <h1>Reporte de visitas</h1>
        <p>Periodo: {filters.fechaDesde} hasta {filters.fechaHasta}</p>
        <p>Total de visitas: {reportRows.length}</p>
        <table><thead><tr><th>Visitante</th><th>Documento</th><th>Apartamento</th><th>Torre</th><th>Ingreso</th><th>Estado</th></tr></thead>
          <tbody>{reportRows.map((r) => <tr key={r.id}><td>{r.nombreVisitante}</td><td>{r.documentoVisitante}</td><td>{r.apartamento}</td><td>{r.torre}</td><td>{String(r.fechaIngreso).replace("T", " ").slice(0, 16)}</td><td>{r.estado}</td></tr>)}</tbody>
        </table>
      </div>}
    </>
  );
}
