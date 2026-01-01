import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { MapPin, Calendar, Phone, Trash2 } from 'lucide-react';

const ItemCard = ({ item, onDelete }) => {
    const isLost = item.type === 'LOST';
    const date = item.date ? new Date(item.date).toLocaleDateString() : 'Unknown';
    const { user } = useAuth();
    
    // Check ownership: user must be logged in and user.id must match item.userId
    const isOwner = user && item.userId && String(user.id) === String(item.userId);

    const handleDelete = async () => {
        if (!window.confirm('Are you sure you want to delete this item?')) return;
        
        try {
            await api.delete(`/items/${item.id}`);
            if (onDelete) onDelete(item.id);
        } catch (error) {
            console.error(error);
            alert('Error deleting item');
        }
    };

    return (
        <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <div style={{ 
                padding: '1.5rem', 
                borderBottom: '1px solid #f1f5f9',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'start'
            }}>
                <div>
                    <span style={{ 
                        fontSize: '0.75rem', 
                        padding: '0.25rem 0.75rem', 
                        borderRadius: '99px', 
                        backgroundColor: isLost ? '#fee2e2' : '#e0f2f1', 
                        color: isLost ? '#ef4444' : '#009688',
                        fontWeight: 600,
                        textTransform: 'uppercase',
                        letterSpacing: '0.05em'
                    }}>
                        {item.status}
                    </span>
                    <h3 style={{ marginTop: '0.75rem', fontSize: '1.25rem' }}>{item.itemName}</h3>
                    <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>{item.category}</p>
                </div>
            </div>
            
            <div style={{ padding: '1.5rem', flex: 1 }}>
                <p style={{ marginBottom: '1.5rem', color: 'var(--color-text-main)' }}>
                    {item.description}
                </p>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                        <MapPin size={16} />
                        <span>{item.location}</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                        <Calendar size={16} />
                        <span>{date}</span>
                    </div>
                     {item.contactInfo && (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                            <Phone size={16} />
                            <span>{item.contactInfo}</span>
                        </div>
                    )}
                </div>
            </div>
            
            <div style={{ padding: '1rem 1.5rem', backgroundColor: '#f8fafc', borderTop: '1px solid #f1f5f9' }}>
                <button className="btn" style={{ 
                    width: '100%', 
                    backgroundColor: 'white', 
                    border: '1px solid #e2e8f0',
                    color: 'var(--color-text-main)'
                }}>View Details</button>
                 {isOwner && (
                    <button onClick={handleDelete} className="btn" style={{ 
                        width: '100%', 
                        marginTop: '0.5rem',
                        backgroundColor: '#fee2e2', 
                        border: '1px solid #fecaca',
                        color: '#ef4444',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '0.5rem'
                    }}>
                        <Trash2 size={16} />
                        Delete
                    </button>
                )}
            </div>
        </div>
    );
};

export default ItemCard;
