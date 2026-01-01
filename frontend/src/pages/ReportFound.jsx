import React, { useState } from 'react';
import { reportFoundItem } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useEffect } from 'react';

const ReportFound = () => {
    const navigate = useNavigate();
    const { user } = useAuth();

    useEffect(() => {
        if (!user) {
            navigate('/login');
        }
    }, [user, navigate]);

    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        itemName: '',
        category: 'Electronics',
        description: '',
        location: '',
        date: '',
        contactInfo: '',
        status: 'FOUND'
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await reportFoundItem(formData);
            alert('Great job! Item reported as found.');
            navigate('/');
        } catch (error) {
            console.error(error);
            alert('Failed to submit report.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
             <h1 style={{ marginBottom: '2rem', textAlign: 'center' }}>Report Found Item</h1>
             <div className="card" style={{ padding: '2rem' }}>
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label>Item Name</label>
                        <input className="input-field" name="itemName" required onChange={handleChange} placeholder="e.g. Blue Umbrella" />
                    </div>
                    
                    <div className="input-group">
                        <label>Category</label>
                        <select className="input-field" name="category" onChange={handleChange}>
                            <option value="Electronics">Electronics</option>
                            <option value="Documents">Documents</option>
                            <option value="Clothing">Clothing</option>
                            <option value="Accessories">Accessories</option>
                            <option value="Others">Others</option>
                        </select>
                    </div>

                    <div className="input-group">
                        <label>Date Found</label>
                        <input className="input-field" type="date" name="date" required onChange={handleChange} />
                    </div>

                    <div className="input-group">
                        <label>Location Found</label>
                        <input className="input-field" name="location" required onChange={handleChange} placeholder="e.g. Cafeteria Table 5" />
                    </div>

                    <div className="input-group">
                        <label>Description</label>
                        <textarea className="input-field" name="description" rows="4" required onChange={handleChange} placeholder="Describe the item..." />
                    </div>

                    <div className="input-group">
                        <label>Your Contact Info (Optional)</label>
                        <input className="input-field" name="contactInfo" onChange={handleChange} placeholder="Email or Phone" />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%', backgroundColor: 'var(--color-secondary)' }} disabled={loading}>
                        {loading ? 'Submitting...' : 'Report Found Item'}
                    </button>
                </form>
             </div>
        </div>
    );
};

export default ReportFound;
