import React, { useEffect, useState } from 'react';
import { getLostItems, getFoundItems } from '../services/api';
import ItemCard from '../components/ItemCard';
import { Link } from 'react-router-dom';

const Dashboard = () => {
    const [recentLost, setRecentLost] = useState([]);
    const [recentFound, setRecentFound] = useState([]);

    useEffect(() => {
        // Fetch recent items (limiting to top 3 for dashboard)
        getLostItems().then(res => setRecentLost(res.data.slice(0, 3))).catch(console.error);
        getFoundItems().then(res => setRecentFound(res.data.slice(0, 3))).catch(console.error);
    }, []);

    return (
        <div>
            <section style={{ 
                marginBottom: '4rem', 
                textAlign: 'center', 
                padding: '4rem 0', 
                background: 'linear-gradient(135deg, #1a237e 0%, #00bcd4 100%)',
                color: 'white',
                borderRadius: 'var(--radius-lg)',
                boxShadow: 'var(--shadow-lg)'
            }}>
                <h1 style={{ color: 'white', fontSize: '3rem', marginBottom: '1rem' }}>Lost something on Campus?</h1>
                <p style={{ fontSize: '1.2rem', marginBottom: '2rem', opacity: 0.9 }}>
                    CampusFinder is the university's official central search system.
                </p>
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
                    <Link to="/report-lost" className="btn" style={{ background: 'white', color: 'var(--color-primary)' }}>
                        I Lost Something
                    </Link>
                    <Link to="/search" className="btn" style={{ background: 'rgba(255,255,255,0.2)', color: 'white', backdropFilter: 'blur(10px)' }}>
                        Search Items
                    </Link>
                </div>
            </section>

            <div style={{ marginBottom: '3rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                    <h2>Recently Found</h2>
                    <Link to="/search" style={{ color: 'var(--color-primary)', fontWeight: 600 }}>View All</Link>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '2rem' }}>
                    {recentFound.map(item => <ItemCard key={item.id} item={item} />)}
                </div>
            </div>

            <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                    <h2>Recently Lost</h2>
                    <Link to="/search" style={{ color: 'var(--color-primary)', fontWeight: 600 }}>View All</Link>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '2rem' }}>
                    {recentLost.map(item => <ItemCard key={item.id} item={item} />)}
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
