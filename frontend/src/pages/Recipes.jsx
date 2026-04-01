import { useEffect, useState } from 'react'
import styles from './Recipes.module.css'

const typeColors = {
    'Sobremesa':      { bg: '#fef3c7', text: '#92400e' },
    'Prato Principal':{ bg: '#dbeafe', text: '#1e40af' },
    'Sopa':           { bg: '#e0f2fe', text: '#075985' },
    'Lanche':         { bg: '#fce7f3', text: '#9d174d' },
    'Bebida':         { bg: '#d1fae5', text: '#065f46' },
    'Entrada':        { bg: '#ede9fe', text: '#5b21b6' },
}

const typeEmoji = {
    'Sobremesa':       '🍰',
    'Prato Principal': '🍽️',
    'Sopa':            '🥣',
    'Lanche':          '🥪',
    'Bebida':          '🥤',
    'Entrada':         '🥗',
}

export default function Recipes({ user, onLogout }) {
    const [recipes, setRecipes] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetch('/api/recipe/read/all')
            .then((res) => {
                if (!res.ok) throw new Error('Erro ao carregar receitas')
                return res.json()
            })
            .then(setRecipes)
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    return (
        <div className={styles.page}>
            <header className={styles.header}>
                <div className={styles.headerLeft}>
                    <span className={styles.logo}>🍴</span>
                    <h1>Receitas</h1>
                </div>
                <div className={styles.userInfo}>
                    <span>Olá, <strong>{user.name ?? user.login}</strong></span>
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
                                            <span
                                                className={styles.badge}
                                                style={{ background: colors.bg, color: colors.text }}
                                            >
                        {recipe.recipe_type ?? 'Sem tipo'}
                      </span>
                                        </div>
                                        <h2 className={styles.cardTitle}>{recipe.name ?? `Receita ${i + 1}`}</h2>
                                        {recipe.description && (
                                            <p className={styles.cardDesc}>{recipe.description}</p>
                                        )}
                                        <div className={styles.cardFooter}>
                      <span className={styles.price}>
                        {recipe.price != null
                            ? `R$ ${Number(recipe.price).toFixed(2).replace('.', ',')}`
                            : '—'}
                      </span>
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </>
                )}
            </main>
        </div>
    )
}