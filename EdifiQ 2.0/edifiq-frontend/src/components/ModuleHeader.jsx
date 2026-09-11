export default function ModuleHeader({ title, description, action, children }) {
  return <div className="module-header"><div className="module-header-copy"><span className="module-kicker">Panel / Gestión</span><h1>{title}</h1><p>{description}</p></div><div className="header-actions">{children}{action}</div></div>
}
