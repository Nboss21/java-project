import React, { useState, useEffect } from 'react';
import { searchItems } from '../services/api';
import ItemCard from '../components/ItemCard';
import { Search } from 'lucide-react';

const SearchPage = () => {
    const [results, setResults] = useState({ lostItems: [], foundItems: [] });
    const [query, setQuery] = useState('');
    const [category, setCategory] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSearch = async () => {
        setLoading(true);
        try {
            const res = await searchItems({ itemName: query, category });
            const items = res.data;
            setResults({
                lostItems: items.filter(i => i.type === 'LOST'),
                foundItems: items.filter(i => i.type === 'FOUND')
            });
        } catch (error) {
            console.error("Search failed", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        handleSearch(); // Initial load
    }, []);

    return (
        <div>
            <div style={{ marginBottom: '2rem', textAlign: 'center' }}>
                <h1 style={{ marginBottom: '1rem' }}>Search Items</h1>
                <p style={{ color: 'var(--color-text-secondary)' }}>Find what you lost or see what's been found.</p>
            </div>

            <div className="card" style={{ padding: '1.5rem', marginBottom: '3rem' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1rem' }}>
                    <input 
                        className="input-field" 
                        placeholder="Search by name..." 
                        value={query} 
                        onChange={(e) => setQuery(e.target.value)} 
                    />
                    <select className="input-field" value={category} onChange={(e) => setCategory(e.target.value)}>
                        <option value="">All Categories</option>
                        <option value="ID Card">ID Card</option>
                        <option value="Charger">Charger</option>
                        <option value="Bottle">Bottle</option>
                        <option value="Electronics">Electronics</option>
                        <option value="Documents">Documents</option>
                        <option value="Clothing">Clothing</option>
                        <option value="Accessories">Accessories</option>
                    </select>
                    <button onClick={handleSearch} className="btn btn-primary" style={{ gap: '0.5rem' }}>
                        <Search size={20} /> Search
                    </button>
                </div>
            </div>

            {loading ? (
                <div style={{ textAlign: 'center', padding: '2rem' }}>Loading...</div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
                    <div>
                        <h2 style={{ marginBottom: '1rem', color: '#ef4444' }}>Lost Items</h2>
                        <div style={{ display: 'grid', gap: '1rem' }}>
                            {results.lostItems.length === 0 && <p>No items found.</p>}
                            {results.lostItems.map(item => (
                                <ItemCard key={item.id} item={item} />
                            ))}
                        </div>
                    </div>
                    <div>
                        <h2 style={{ marginBottom: '1rem', color: '#009688' }}>Found Items</h2>
                        <div style={{ display: 'grid', gap: '1rem' }}>
                            {results.foundItems.length === 0 && <p>No items found.</p>}
                            {results.foundItems.map(item => (
                                <ItemCard key={item.id} item={item} />
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SearchPage;
