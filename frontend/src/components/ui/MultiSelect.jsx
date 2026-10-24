import Icon from './Icon'

/**
 * Selection multiple sous forme de chips.
 * options : [{ id, label }]  ·  value : [id, ...]  ·  onChange(newValueArray)
 */
export default function MultiSelect({ options, value = [], onChange, placeholder = 'Ajouter...' }) {
  const selected = options.filter((o) => value.includes(o.id))
  const available = options.filter((o) => !value.includes(o.id))

  const add = (id) => {
    if (id) onChange([...value, Number(id)])
  }
  const remove = (id) => onChange(value.filter((v) => v !== id))

  return (
    <div className="multiselect">
      {selected.length > 0 && (
        <div className="chips">
          {selected.map((o) => (
            <span key={o.id} className="chip">
              {o.label}
              <button type="button" onClick={() => remove(o.id)} aria-label="Retirer">
                <Icon name="x" size={13} />
              </button>
            </span>
          ))}
        </div>
      )}
      <select value="" onChange={(e) => add(e.target.value)}>
        <option value="">{placeholder}</option>
        {available.map((o) => (
          <option key={o.id} value={o.id}>
            {o.label}
          </option>
        ))}
      </select>
    </div>
  )
}
