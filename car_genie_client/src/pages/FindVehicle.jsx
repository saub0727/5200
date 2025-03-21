import React, { useState } from 'react';
import axios from 'axios';

function FindVehicle() {
    const [vin, setVin] = useState('');
    const [vehicle, setVehicle] = useState(null);
    const [error, setError] = useState(null);

    const fetchVehicle = async () => {
        setError(null);
        setVehicle(null);

        if (!vin.trim()) {
            setError('Please enter a VIN number.');
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/findvehicles?vin=${vin}`);
            setVehicle(response.data);
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to retrieve vehicle data.');
        }
    };

    return (
        <div>
            <h1>Vehicle Lookup</h1>
            <input
                type="text"
                placeholder="Enter VIN number"
                value={vin}
                onChange={(e) => setVin(e.target.value)}
            />
            <button onClick={fetchVehicle}>Search Vehicle</button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {vehicle && (
                <div>
                    <h2>Vehicle Information</h2>
                    <p><strong>Vehicle ID:</strong> {vehicle.vehicleId}</p>
                    <p><strong>VIN:</strong> {vehicle.vin}</p>
                    <p><strong>Price:</strong> ${vehicle.price}</p>
                    <p><strong>Posting Date:</strong> {vehicle.postingDate}</p>
                    <p><strong>Description:</strong> {vehicle.description}</p>
                </div>
            )}
        </div>
    );
}

export default FindVehicle;
