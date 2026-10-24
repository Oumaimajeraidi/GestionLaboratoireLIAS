import { Routes, Route } from 'react-router-dom'
import PublicLayout from './components/layout/PublicLayout'
import AppLayout from './components/layout/AppLayout'
import ProtectedRoute from './components/ProtectedRoute'

import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import AdhesionPage from './pages/AdhesionPage'
import PublicationsPubliquesPage from './pages/public/PublicationsPubliquesPage'
import EvenementsPublicsPage from './pages/public/EvenementsPublicsPage'

import DashboardPage from './pages/DashboardPage'
import MembresPage from './pages/MembresPage'
import MembreDetailPage from './pages/MembreDetailPage'
import PublicationsPage from './pages/PublicationsPage'
import EvenementsPage from './pages/EvenementsPage'
import CalendrierPage from './pages/CalendrierPage'
import DocumentsPage from './pages/DocumentsPage'
import DemandesPage from './pages/DemandesPage'
import MandatsPage from './pages/MandatsPage'
import MaterielPage from './pages/MaterielPage'
import ConventionsPage from './pages/ConventionsPage'
import ReunionsPage from './pages/ReunionsPage'
import MessagesPage from './pages/MessagesPage'
import RapportAnnuelPage from './pages/RapportAnnuelPage'
import RecherchePage from './pages/RecherchePage'
import NotFoundPage from './pages/NotFoundPage'

export default function App() {
  return (
    <Routes>
      {/* ---------- Espace public ---------- */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/publications-publiques" element={<PublicationsPubliquesPage />} />
        <Route path="/evenements-publics" element={<EvenementsPublicsPage />} />
        <Route path="/adhesion" element={<AdhesionPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Route>

      {/* ---------- Espace de travail (authentifié) ---------- */}
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/membres" element={<MembresPage />} />
          <Route path="/membres/:id" element={<MembreDetailPage />} />
          <Route path="/publications" element={<PublicationsPage />} />
          <Route path="/evenements" element={<EvenementsPage />} />
          <Route path="/calendrier" element={<CalendrierPage />} />
          <Route path="/documents" element={<DocumentsPage />} />
          <Route path="/messages" element={<MessagesPage />} />
          <Route path="/recherche" element={<RecherchePage />} />
          <Route path="/mandats" element={<MandatsPage />} />
          <Route path="/conventions" element={<ConventionsPage />} />
          <Route path="/reunions" element={<ReunionsPage />} />
          <Route path="/materiels" element={<MaterielPage />} />
          <Route path="/rapport" element={<RapportAnnuelPage />} />

          {/* Réservé directeur / admin */}
          <Route element={<ProtectedRoute roles={['ADMIN', 'DIRECTEUR']} />}>
            <Route path="/demandes" element={<DemandesPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
