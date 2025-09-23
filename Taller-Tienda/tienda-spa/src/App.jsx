import { useEffect, useMemo, useState, useCallback } from 'react'
import axios from 'axios'

// === 🧭 Config ===
const apiBase = import.meta.env.VITE_API?.replace(/\/+$/,'') || 'http://localhost:8080'
const http = axios.create({ baseURL: apiBase, headers: { 'Content-Type': 'application/json' } })

const currencyCOP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
})

// === 🎨 Estilos (sin dependencias) ===
const styles = `
:root{
  --bg:#0b0c10;           /* fondo principal (soporta dark por defecto) */
  --card:#11131a;         /* tarjetas */
  --elev:#171a22;         /* elevaciones */
  --muted:#8a8f98;        /* texto secundario */
  --text:#f5f7fb;         /* texto principal */
  --brand:#6aaefc;        /* acento */
  --brand-2:#7ce3b2;      /* acento secundario */
  --danger:#ff6b6b;
  --ok:#1dd1a1;
  --border:rgba(255,255,255,.08);
  --ring: 0 0 0 3px color-mix(in srgb, var(--brand) 25%, transparent);
  --radius:16px;
}
@media (prefers-color-scheme: light){
  :root{ --bg:#f6f7fb; --card:#ffffff; --elev:#ffffff; --text:#0b1320; --muted:#5b6470; --border:#e6e8ef; }
}
*{box-sizing:border-box}
body{ margin:0; background:var(--bg); color:var(--text); font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, 'Helvetica Neue', Arial }

.app{max-width:1100px; margin:0 auto; padding:28px}
.header{ display:flex; gap:14px; align-items:center; justify-content:space-between; margin-bottom:12px }
.h1{ font-size:28px; font-weight:800; letter-spacing:.2px; display:flex; align-items:center; gap:10px }
.badge{ font-size:12px; color:var(--muted) }

.toolbar{ display:flex; gap:10px; align-items:center; margin: 12px 0 22px }
.code{ font: 12px ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace; padding:6px 8px; background:var(--elev); border:1px solid var(--border); border-radius:10px }
.btn{ appearance:none; border:1px solid var(--border); background:linear-gradient(180deg, var(--elev), color-mix(in srgb, var(--elev) 70%, black)); color:var(--text); padding:10px 14px; border-radius:12px; font-weight:600; cursor:pointer; transition: transform .05s ease, border-color .2s ease, background .2s ease }
.btn:hover{ transform: translateY(-1px) }
.btn[disabled]{ opacity:.6; cursor:not-allowed }
.btn.primary{ border-color: color-mix(in srgb, var(--brand) 30%, var(--border)); background:linear-gradient(180deg, color-mix(in srgb, var(--brand) 12%, var(--elev)), var(--elev)) }
.btn.ghost{ background:transparent }

.grid{ display:grid; grid-template-columns: 1fr; gap:16px }
@media(min-width:960px){ .grid{ grid-template-columns: 1.2fr .8fr } }

.card{ background:var(--card); border:1px solid var(--border); border-radius:var(--radius); box-shadow: 0 10px 30px rgba(0,0,0,.2); }
.card .content{ padding:18px }
.card-title{ padding:14px 18px; border-bottom:1px solid var(--border); font-weight:700; letter-spacing:.2px }

/* Stats */
.stats{ display:grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap:12px; margin: 10px 0 16px }
.stat{ background:var(--card); border:1px solid var(--border); border-radius:18px; padding:16px; display:flex; flex-direction:column; gap:6px }
.stat .k{ font-size:12px; color:var(--muted) }
.stat .v{ font-size:20px; font-weight:800 }

/* Form */
.form{ display:grid; gap:12px }
.field{ display:grid; gap:6px }
.label{ font-size:12px; color:var(--muted) }
.input{ width:100%; padding:12px 12px; border-radius:12px; border:1px solid var(--border); background:var(--elev); color:var(--text) }
.input:focus{ outline:none; box-shadow: var(--ring); border-color: color-mix(in srgb, var(--brand) 50%, var(--border)) }
.row{ display:flex; gap:10px }
.row>.field{ flex:1 }
.actions{ display:flex; gap:10px; justify-content:flex-end }

/* Alerts */
.alert{ padding:12px 14px; border-radius:12px; border:1px solid; margin: 8px 0 16px }
.alert.error{ background: color-mix(in srgb, var(--danger) 12%, transparent); border-color: color-mix(in srgb, var(--danger) 60%, var(--border)) }
.alert.ok{ background: color-mix(in srgb, var(--ok) 12%, transparent); border-color: color-mix(in srgb, var(--ok) 60%, var(--border)) }

/* Table */
.table-wrap{ overflow:auto; border-radius:18px; border:1px solid var(--border) }
.table{ width:100%; border-collapse: collapse; background:var(--card) }
.table th, .table td{ padding:12px 14px; border-bottom:1px solid var(--border); text-align:left; vertical-align:top }
.table th{ position:sticky; top:0; backdrop-filter: blur(6px); background: color-mix(in srgb, var(--card) 75%, transparent); z-index:1; font-size:12px; color:var(--muted) }
.table tbody tr:hover{ background: color-mix(in srgb, var(--brand) 6%, transparent) }
.table tbody tr:nth-child(odd){ background: color-mix(in srgb, var(--card) 98%, black) }
.id{ opacity:.8 }

/* Empty */
.empty{ color:var(--muted); text-align:center; padding:28px }

/* Footer */
.footer{ display:flex; align-items:center; gap:12px; margin-top:18px }
.small{ color:var(--muted); font-size:12px }
`;

export default function App(){
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')
  const [form, setForm] = useState({ nombre:'', descripcion:'', precio:'', stock:'' })

  const totalItems = list.length
  const totalInventario = useMemo(() => list.reduce((acc,p)=> acc + (Number(p.stock)||0), 0), [list])
  const totalValorizado = useMemo(() => list.reduce((acc,p)=> acc + (Number(p.precio)||0)*(Number(p.stock)||0), 0), [list])

  const load = useCallback(async () => {
    setLoading(true); setError(''); setOk('')
    try{
      const { data } = await http.get('/api/products')
      const items = Array.isArray(data) ? data : (data?.content ?? [])
      setList(items)
    }catch(err){
      setError(err?.response?.data?.message || err.message || 'Error cargando productos')
    }finally{ setLoading(false) }
  },[])

  useEffect(()=>{ load() },[load])

  const save = async (e) => {
    e.preventDefault(); setError(''); setOk('')
    const precio = Number(form.precio)
    const stock = Number(form.stock)

    if (!form.nombre?.trim()) return setError('El nombre es obligatorio.')
    if (Number.isNaN(precio) || precio < 0) return setError('El precio debe ser un número válido (≥ 0).')
    if (!Number.isInteger(stock) || stock < 0) return setError('El stock debe ser un entero válido (≥ 0).')

    try{
      await http.post('/api/products', { ...form, precio, stock })
      setForm({ nombre:'', descripcion:'', precio:'', stock:'' })
      await load();
      setOk('Producto guardado correctamente.')
    }catch(err){
      setError(err?.response?.data?.message || err.message || 'No se pudo guardar')
    }
  }

  return (
    <div className="app">
      <style>{styles}</style>

      <header className="header">
        <div className="h1">🛍️ Productos</div>
        <span className="badge">API: <span className="code">{apiBase}</span></span>
      </header>

      {/* Stats */}
      <section className="stats">
        <div className="stat"><span className="k">Items</span><span className="v">{totalItems}</span></div>
        <div className="stat"><span className="k">Unidades totales</span><span className="v">{totalInventario}</span></div>
        <div className="stat"><span className="k">Valor inventario</span><span className="v">{currencyCOP.format(totalValorizado)}</span></div>
      </section>

      <div className="grid">
        {/* Tabla */}
        <section className="card">
          <div className="card-title">Listado</div>
          <div className="content">
            <div style={{display:'flex', gap:10, marginBottom:12}}>
              <button className="btn ghost" onClick={load} disabled={loading}>↻ Recargar</button>
            </div>

            {error && <div className="alert error">{error}</div>}
            {ok && <div className="alert ok">{ok}</div>}

            {loading ? (
              <div className="empty">Cargando…</div>
            ) : list.length === 0 ? (
              <div className="empty">No hay productos aún. Crea el primero con el formulario.</div>
            ) : (
              <div className="table-wrap">
                <table className="table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th>Descripción</th>
                      <th>Precio</th>
                      <th>Stock</th>
                      <th>Creado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {list.map(p=> (
                      <tr key={p.id}>
                        <td className="id">#{p.id}</td>
                        <td>{p.nombre}</td>
                        <td>{p.descripcion ?? '—'}</td>
                        <td>{currencyCOP.format(Number(p.precio)||0)}</td>
                        <td>{Number(p.stock) ?? 0}</td>
                        <td>{p.fechaCreacion ? String(p.fechaCreacion).replace('T',' ').slice(0,19) : '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </section>

        {/* Formulario */}
        <section className="card">
          <div className="card-title">Nuevo producto</div>
          <div className="content">
            <form onSubmit={save} className="form">
              <div className="field">
                <label className="label">Nombre *</label>
                <input className="input" placeholder="Nombre" value={form.nombre} onChange={e=>setForm(f=>({...f, nombre:e.target.value}))} />
              </div>

              <div className="field">
                <label className="label">Descripción</label>
                <input className="input" placeholder="Descripción" value={form.descripcion} onChange={e=>setForm(f=>({...f, descripcion:e.target.value}))} />
              </div>

              <div className="row">
                <div className="field">
                  <label className="label">Precio (COP) *</label>
                  <input className="input" type="number" min="0" step="1" placeholder="0" value={form.precio} onChange={e=>setForm(f=>({...f, precio:e.target.value}))} />
                </div>
                <div className="field">
                  <label className="label">Stock *</label>
                  <input className="input" type="number" min="0" step="1" placeholder="0" value={form.stock} onChange={e=>setForm(f=>({...f, stock:e.target.value}))} />
                </div>
              </div>

              <div className="actions">
                <button type="reset" className="btn" onClick={()=>setForm({ nombre:'', descripcion:'', precio:'', stock:'' })}>Limpiar</button>
                <button type="submit" className="btn primary" disabled={loading}>{loading ? 'Guardando…' : 'Guardar'}</button>
              </div>
            </form>
          </div>
        </section>
      </div>

      <div className="footer">
        <span className="small">Consejo: si tu front y back corren en Docker, usa el nombre del servicio (p.ej. <span className="code">http://app:8080</span>) en <span className="code">VITE_API</span>.</span>
      </div>
    </div>
  )
}
