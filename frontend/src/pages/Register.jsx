import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import styles from './Auth.module.css'

export default function Register() {
    const [form, setForm] = useState({ name: '', login: '', password: '' })
    const [error, setError] = useState('')
    const [success, setSuccess] = useState(false)
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)
        try {
            const res = await fetch('/api/user/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form),
            })
            if (!res.ok) throw new Error('Erro ao cadastrar. Tente novamente.')
            setSuccess(true)
            setTimeout(() => navigate('/login'), 1500)
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h1 className={styles.title}>Cadastro</h1>
                {success ? (
                    <p className={styles.success}>Cadastro realizado! Redirecionando...</p>
                ) : (
                    <form onSubmit={handleSubmit} className={styles.form}>
                        <label>Nome <input name="name" type="text" value={form.name} onChange={handleChange} required /></label>
                        <label>Email<input name="email" type="email" value={form.login} onChange={handleChange} required /></label>
                        <label>Senha<input name="password" type="password" value={form.password} onChange={handleChange} required /></label>
                        {error && <p className={styles.error}>{error}</p>}
                        <button type="submit" disabled={loading} className={styles.btn}>
                            {loading ? 'Cadastrando...' : 'Cadastrar'}
                        </button>
                    </form>
                )}
                <p className={styles.link}>Já tem conta? <Link to="/login">Entrar</Link></p>
            </div>
        </div>
    )
}