import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import styles from './Auth.module.css'

export default function Login({ onLogin }) {
    const [form, setForm] = useState({ login: '', password: '' })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)
        try {
            const res = await fetch('/api/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form),
            })
            if (!res.ok) throw new Error('Login ou senha inválidos')
            const data = await res.json()
            onLogin(data)
            navigate('/recipes')
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h1 className={styles.title}>Entrar</h1>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <label>Email
                        <input name="email" type="email" value={form.login} onChange={handleChange} required />
                    </label>
                    <label>Senha
                        <input name="password" type="password" value={form.password} onChange={handleChange} required />
                    </label>
                    {error && <p className={styles.error}>{error}</p>}
                    <button type="submit" disabled={loading} className={styles.btn}>
                        {loading ? 'Entrando...' : 'Entrar'}
                    </button>
                </form>
                <p className={styles.link}>Não tem conta? <Link to="/register">Cadastre-se</Link></p>
            </div>
        </div>
    )
}