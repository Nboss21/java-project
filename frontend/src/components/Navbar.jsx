import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Link, useLocation } from 'react-router-dom';
import { Search, PlusCircle, Home } from 'lucide-react';

const Navbar = () => {
    const location = useLocation();
    const { user, logout } = useAuth();

    const isActive = (path) => location.pathname === path ? 'active-link' : '';

    return (
        <nav style={{
            backgroundColor: 'var(--color-surface)',
            borderBottom: '1px solid #e2e8f0',
            padding: '1rem 0',
            position: 'sticky',
            top: 0,
            zIndex: 100
        }}>
            <div className="container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '1.5rem', fontWeight: 700, color: 'var(--color-primary)' }}>
                    <span>CampusFinder</span>
                </Link>

                <div style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
                    <NavLink to="/" icon={<Home size={18} />} label="Dashboard" active={location.pathname === '/'} />
                    <NavLink to="/search" icon={<Search size={18} />} label="Search" active={location.pathname === '/search'} />
                    
                    {user ? (
                        <>
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <Link to="/report-lost" className="btn btn-outline" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>
                                    Report Lost
                                </Link>
                                <Link to="/report-found" className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>
                                    Report Found
                                </Link>
                            </div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                <span style={{ fontSize: '0.9rem', fontWeight: 600 }}>Hello, {user.username}</span>
                                <button onClick={logout} className="btn btn-text" style={{ fontSize: '0.9rem', color: '#ef4444' }}>
                                    Logout
                                </button>
                            </div>
                        </>
                    ) : (
                        <div style={{ display: 'flex', gap: '1rem' }}>
                            <Link to="/login" className="btn btn-text" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>
                                Login
                            </Link>
                            <Link to="/signup" className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>
                                Sign Up
                            </Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

const NavLink = ({ to, icon, label, active }) => (
    <Link to={to} style={{ 
        display: 'flex', 
        alignItems: 'center', 
        gap: '0.5rem', 
        color: active ? 'var(--color-primary)' : 'var(--color-text-secondary)',
        fontWeight: active ? 600 : 500,
        transition: 'color 0.2s'
    }}>
        {icon}
        <span>{label}</span>
    </Link>
);

export default Navbar;
