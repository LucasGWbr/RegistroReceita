import { useEffect, useState } from 'react'
import styles from './Recipes.module.css'

const typeColors = {
    'Sobremesa': { bg: '#fef3c7', text: '#92400e' },
    'Prato Principal': { bg: '#dbeafe', text: '#1e40af' },
    'Sopa': { bg: '#e0f2fe', text: '#075985' },
    'Lanche': { bg: '#fce7f3', text: '#9d174d' },
    'Bebida': { bg: '#d1fae5', text: '#065f46' },
    'Entrada': { bg: '#ede9fe', text: '#5b21b6' },
}

const typeEmoji = {
    'Sobremesa': '🍰',
    'Prato Principal': '🍽️',
    'Sopa': '🥣',
    'Lanche': '🥪',
    'Bebida': '🥤',
    'Entrada': '🥗',
}

const EMPTY_FORM = {
    name: '',
    description: '',
    price: '',
    recipeType: ''
}

export default function Recipes({ user, onLogout }) {
    const [recipes, setRecipes] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [showForm, setShowForm] = useState(false)
    const [editing, setEditing] = useState(null) // recipe being edited
    const [form, setForm] = useState(EMPTY_FORM)
    const [saving, setSaving] = useState(false)
    const [formError, setFormError] = useState('')

    // Filtros
    const [filterDate, setFilterDate] = useState('')
    const [filterType, setFilterType] = useState('')
    const [generatingPdf, setGeneratingPdf] = useState(false)

    const loadRecipes = async (date = '', type = '') => {
        setLoading(true)
        try {
            const hasFilter = date || type;

            // 2. Define a URL base: se houver filtro usa /filter, senão usa /all
            let url = hasFilter ? '/api/recipe/read/filter' : '/api/recipe/read/all';

            // 3. Adiciona os parâmetros apenas se houver filtro
            if (hasFilter) {
                const params = new URLSearchParams();
                if (type) params.append('type', type);
                if (date) params.append('dateTime', date+'T00:00:00');
                url += `?${params.toString()}`;
            }

            const res = await fetch(url)
            if (!res.ok) throw new Error('Erro ao carregar receitas')

            const data = await res.json();
            // Tratamento de segurança: Garante que recipes seja sempre um array
            // (Evita erro de .map() se a API retornar um objeto único)
            setRecipes(Array.isArray(data) ? data : (data ? [data] : []));
            setError('')
        } catch (err) {
            setError(err.message)
            setRecipes([])
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadRecipes()
    }, [])

    const handleFilter = () => {
        loadRecipes(filterDate, filterType)
    }

    const handleClearFilters = () => {
        setFilterDate('')
        setFilterType('')
        loadRecipes('', '')
    }

    const handleDownloadPdf = async () => {
        setGeneratingPdf(true)
        try {
            let url = '/api/recipe/read/pdf';
            const params = new URLSearchParams();
            if (filterType) params.append('type', filterType);
            if (filterDate) params.append('dateTime', filterDate+'T00:00:00');
            url += `?${params.toString()}`;


            const res = await fetch(url)
            if (!res.ok) throw new Error('Erro ao gerar PDF')

            const blob = await res.blob()
            const pdfUrl = window.URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = pdfUrl
            a.download = `receitas_${new Date().toISOString().split('T')[0]}.pdf`
            document.body.appendChild(a)
            a.click()
            window.URL.revokeObjectURL(pdfUrl)
            document.body.removeChild(a)
        } catch (err) {
            alert(err.message)
        } finally {
            setGeneratingPdf(false)
        }
    }

    const openCreate = () => {
        setEditing(null)
        setForm(EMPTY_FORM)
        setFormError('')
        setShowForm(true)
    }

    const openEdit = (recipe) => {
        setEditing(recipe)
        setForm({
            name: recipe.name,
            description: recipe.description,
            price: recipe.price,
            recipeType: recipe.recipeType
        })
        setFormError('')
        setShowForm(true)
    }

    const closeForm = () => {
        setShowForm(false)
        setEditing(null)
    }

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const handleSubmit = async (e) => {
        e.preventDefault()
        setFormError('')
        setSaving(true)
        try {
            const url = editing ? `/api/recipe/update/${editing.id}` : '/api/recipe/create'
            const method = editing ? 'PUT' : 'POST'
            const res = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...form, price: Number(form.price) }),
            })
            if (!res.ok) throw new Error('Erro ao salvar receita')
            closeForm()
            loadRecipes(filterDate, filterType)
        } catch (err) {
            setFormError(err.message)
        } finally {
            setSaving(false)
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm('Deletar esta receita?')) return
        try {
            const res = await fetch(`/api/recipe/delete/${id}`, {
                method: 'DELETE'
            })
            if (!res.ok) throw new Error('Erro ao deletar')
            loadRecipes(filterDate, filterType)
        } catch (err) {
            alert(err.message)
        }
    }

    const recipeTypes = Object.keys(typeColors)

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

            {/* Filtros */}
            <div className={styles.filterSection}>
                <div className={styles.filterContainer}>
                    <div className={styles.filterInputsGroup}>
                        <div className={styles.filterGroup}>
                            <label>📅 Data</label>
                            <input
                                type="date"
                                value={filterDate}
                                onChange={(e) => setFilterDate(e.target.value)}
                                className={styles.filterInput}
                            />
                        </div>

                        <div className={styles.filterGroup}>
                            <label>🏷️ Tipo</label>
                            <select
                                value={filterType}
                                onChange={(e) => setFilterType(e.target.value)}
                                className={styles.filterInput}
                            >
                                <option value="">Todos os tipos</option>
                                {recipeTypes.map(type => (
                                    <option key={type} value={type}>{type}</option>
                                ))}
                            </select>
                        </div>

                        <div className={styles.filterActions}>
                            <button onClick={handleFilter} className={styles.filterBtn}>
                                🔍 Filtrar
                            </button>
                            <button onClick={handleClearFilters} className={styles.clearBtn}>
                                ✕ Limpar
                            </button>
                        </div>
                    </div>

                    <button
                        onClick={handleDownloadPdf}
                        disabled={generatingPdf}
                        className={styles.pdfBtn}
                    >
                        {generatingPdf ? '⏳ Gerando...' : '📄 Gerar PDF'}
                    </button>
                </div>
            </div>

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
                        <p className={styles.count}>
                            {recipes.length} receita{recipes.length !== 1 ? 's' : ''} encontrada{recipes.length !== 1 ? 's' : ''}
                        </p>
                        <div className={styles.grid}>
                            {recipes.map((recipe, i) => {
                                const colors = typeColors[recipe.recipeType] ?? { bg: '#f3f4f6', text: '#374151' }
                                const emoji = typeEmoji[recipe.recipeType] ?? '🍴'
                                return (
                                    <div key={recipe.id ?? i} className={styles.card}>
                                        <div className={styles.cardHeader}>
                                            <span className={styles.emoji}>{emoji}</span>
                                            <span className={styles.badge} style={{ background: colors.bg, color: colors.text }}>
                        {recipe.recipeType ?? 'Sem tipo'}
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
                            <label>
                                Nome
                                <input
                                    name="name"
                                    value={form.name}
                                    onChange={handleChange}
                                    required
                                />
                            </label>
                            <label>
                                Descrição
                                <textarea
                                    name="description"
                                    value={form.description}
                                    onChange={handleChange}
                                    rows={3}
                                />
                            </label>
                            <label>
                                Preço
                                <input
                                    name="price"
                                    type="number"
                                    step="0.01"
                                    value={form.price}
                                    onChange={handleChange}
                                    required
                                />
                            </label>
                            <label>
                                Tipo
                                <select
                                    name="recipeType"
                                    value={form.recipeType}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="">Selecione um tipo</option>
                                    {recipeTypes.map(type => (
                                        <option key={type} value={type}>{type}</option>
                                    ))}
                                </select>
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