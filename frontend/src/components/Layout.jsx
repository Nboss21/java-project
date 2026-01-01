import React from 'react';
import Navbar from './Navbar';

const Layout = ({ children }) => {
    return (
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
            <Navbar />
            <main style={{ flex: 1, padding: '2rem 0' }}>
                <div className="container">
                    {children}
                </div>
            </main>
            <footer style={{ 
                backgroundColor: 'var(--color-primary)', 
                color: 'white', 
                padding: '2rem 0', 
                textAlign: 'center',
                marginTop: 'auto'
            }}>
                <div className="container">
                    <p>© 2025 Campus Lost & Found System. University Project.</p>
                </div>
            </footer>
        </div>
    );
};

export default Layout;
