import { useEffect, useState } from 'react'
import styles from './Recipes.module.css'

const typeColors = {
    'Sobremesa':       { bg: '#fef3c7', text: '#92400e' },
    'Prato Principal': { bg: '#dbeafe', text: '#1e40af' },
    'Sopa':            { bg: '#e0f2fe', text: '#075985' },
    'Lanche':          { bg: '#fce7f3', text: '#9d174d' },
    'Bebida':          { bg: '#d1fae5', text: '#065f46' },
    'Entrada':         { bg: '#ede9fe', text: '#5b21b6' },
}

const typeEmoji = {
    'Sobremesa':       '🍰',
    'Prato Principal': '🍽️',
    'Sopa':            '🥣',
    'Lanche':          '🥪',
    'Bebida':          '🥤',
    'Entrada':         '🥗',
}

const EMPTY_FORM = { name: '', description: '', price: '', recipe_type: '' }

export default function Recipes({ user, onLogout }) {
    const [recipes, setRecipes]   = useState([])
    const [loading, setLoading]   = useState(true)
    const [error, setError]       = useState('')
    const [showForm, setShowForm] = useState(false)
    const [editing, setEditing]   = useState(null) // recipe being edited
    const [form, setForm]         = useState(EMPTY_FORM)
    const [saving, setSaving]     = useState(false)
    const [formError, setFormError] = useState('')

    const loadRecipes = () => {
        setLoading(true)
        fetch('/api/recipe/read/all')
            .then(res => { if (!res.ok) throw new Error('Erro ao carregar receitas'); return res.json() })
            .then(setRecipes)
            .catch(err => setError(err.message))
            .finally(() => setLoading(false))
    }

    useEffect(() => { loadRecipes() }, [])

    const openCreate = () => {
        setEditing(null)
        setForm(EMPTY_FORM)
        setFormError('')
        setShowForm(true)
    }

    const openEdit = (recipe) => {
        setEditing(recipe)
        setForm({ name: recipe.name, description: recipe.description, price: recipe.price, recipe_type: recipe.recipe_type })
        setFormError('')
        setShowForm(true)
    }

    const closeForm = () => { setShowForm(false); setEditing(null) }

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const handleSubmit = async (e) => {
        e.preventDefault()
        setFormError('')
        setSaving(true)
        try {
            const url    = editing ? `/api/recipe/update/${editing.id}` : '/api/recipe/create'
            const method = editing ? 'PUT' : 'POST'
            const res = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...form, price: Number(form.price) }),
            })
            if (!res.ok) throw new Error('Erro ao salvar receita')
            closeForm()
            loadRecipes()
        } catch (err) {
            setFormError(err.message)
        } finally {
            setSaving(false)
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm('Deletar esta receita?')) return
        try {
            const res = await fetch(`/api/recipe/delete/${id}`, { method: 'DELETE' })
            if (!res.ok) throw new Error('Erro ao deletar')
            loadRecipes()
        } catch (err) {
            alert(err.message)
        }
    }

    return (
        <div className={styles.page}>
            <header className={styles.header}>
                <div className={styles.headerLeft}>
                    <span className={styles.logo}>🍴</span>
                    <h1>Receitas</h1>
                </div>
                <div className={styles.userInfo}>
                    <span>Olá, <strong>{user.name ?? user.login}</strong></span>
                    <button onClick={openCreate} className={styles.addBtn}>+ Nova Receita</button>
                    <button onClick={onLogout} className={styles.logoutBtn}>Sair</button>
                </div>
            </header>

            <main className={styles.main}>
                {loading && (
                    <div className={styles.statusBox}>
                        <span className={styles.spinner} />
                        <p>Carregando receitas...</p>
                    </div>
                )}
                {error && <p className={styles.error}>⚠️ {error}</p>}
                {!loading && !error && recipes.length === 0 && (
                    <p className={styles.status}>Nenhuma receita encontrada.</p>
                )}

                {!loading && !error && recipes.length > 0 && (
                    <>
                        <p className={styles.count}>{recipes.length} receita{recipes.length !== 1 ? 's' : ''} encontrada{recipes.length !== 1 ? 's' : ''}</p>
                        <div className={styles.grid}>
                            {recipes.map((recipe, i) => {
                                const colors = typeColors[recipe.recipe_type] ?? { bg: '#f3f4f6', text: '#374151' }
                                const emoji  = typeEmoji[recipe.recipe_type]  ?? '🍴'
                                return (
                                    <div key={recipe.id ?? i} className={styles.card}>
                                        <div className={styles.cardHeader}>
                                            <span className={styles.emoji}>{emoji}</span>
                                            <span className={styles.badge} style={{ background: colors.bg, color: colors.text }}>
                        {recipe.recipe_type ?? 'Sem tipo'}
                      </span>
                                        </div>
                                        <h2 className={styles.cardTitle}>{recipe.name ?? `Receita ${i + 1}`}</h2>
                                        {recipe.description && <p className={styles.cardDesc}>{recipe.description}</p>}
                                        <div className={styles.cardFooter}>
                      <span className={styles.price}>
                        {recipe.price != null ? `R$ ${Number(recipe.price).toFixed(2).replace('.', ',')}` : '—'}
                      </span>
                                            <div className={styles.cardActions}>
                                                <button onClick={() => openEdit(recipe)} className={styles.editBtn}>Editar</button>
                                                <button onClick={() => handleDelete(recipe.id)} className={styles.deleteBtn}>Deletar</button>
                                            </div>
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </>
                )}
            </main>

            {/* Modal */}
            {showForm && (
                <div className={styles.overlay} onClick={closeForm}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>{editing ? 'Editar Receita' : 'Nova Receita'}</h2>
                        <form onSubmit={handleSubmit} className={styles.form}>
                            <label>Nome
                                <input name="name" value={form.name} onChange={handleChange} required />
                            </label>
                            <label>Descrição
                                <textarea name="description" value={form.description} onChange={handleChange} rows={3} />
                            </label>
                            <label>Preço
                                <input name="price" type="number" step="0.01" value={form.price} onChange={handleChange} required />
                            </label>
                            <label>Tipo
                                <input name="recipe_type" value={form.recipe_type} onChange={handleChange} required placeholder="Ex: Sobremesa, Lanche..." />
                            </label>
                            {formError && <p className={styles.formError}>{formError}</p>}
                            <div className={styles.modalActions}>
                                <button type="button" onClick={closeForm} className={styles.cancelBtn}>Cancelar</button>
                                <button type="submit" disabled={saving} className={styles.saveBtn}>
                                    {saving ? 'Salvando...' : 'Salvar'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    )
}