import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useState } from 'react'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Recipes from './pages/Recipes.jsx'

export default function App() {
    const [user, setUser] = useState(() => {
        try {
            const saved = localStorage.getItem('user')
            return saved ? JSON.parse(saved) : null
        } catch {
            localStorage.removeItem('user')
            return null
        }
    })

  const handleLogin = (userData) => {
    localStorage.setItem('user', JSON.stringify(userData))
    setUser(userData)
  }

  const handleLogout = () => {
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login"    element={!user ? <Login onLogin={handleLogin} /> : <Navigate to="/recipes" />} />
        <Route path="/register" element={!user ? <Register /> : <Navigate to="/recipes" />} />
        <Route path="/recipes"  element={user  ? <Recipes user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
        <Route path="*"         element={<Navigate to={user ? '/recipes' : '/login'} />} />
      </Routes>
    </BrowserRouter>
  )
}
