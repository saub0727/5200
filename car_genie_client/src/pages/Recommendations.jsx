import React, { useState } from 'react';

const Recommendations = () => {
    const [vin, setVin] = useState("");
    const [recommendations, setRecommendations] = useState([]);
    const [error, setError] = useState(null);

    const fetchRecommendations = async () => {
        setError(null);
        setRecommendations([]);

        if (!vin.trim()) {
            setError("Please enter a VIN.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/recommendations?vin=${encodeURIComponent(vin)}`);
            const data = await response.json();
            console.log("Fetched Recommendations:", data);
            if (response.ok) {
                setRecommendations(data);
            } else {
                setError(data.error || "Failed to fetch recommendations");
            }
        } catch (err) {
            console.error("Error fetching recommendations:", err);
            setError("Network error. Please try again later.");
        }
    };

    const formatDate = (dateObj) => {
        if (dateObj && typeof dateObj === 'object') {
            const { year, month, day } = dateObj;
            return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        }
        return dateObj;
    };

    return (
        <div style={{ padding: "1rem", maxWidth: "800px", margin: "0 auto" }}>
            <h1>Recommend Vehicles</h1>
            <div style={{ marginBottom: "1rem" }}>
                <input
                    type="text"
                    placeholder="Enter VIN"
                    value={vin}
                    onChange={(e) => setVin(e.target.value)}
                    style={{ padding: "0.5rem", width: "70%" }}
                />
                <button
                    onClick={fetchRecommendations}
                    style={{ padding: "0.5rem 1rem", marginLeft: "1rem" }}
                >
                    Get Recommendations
                </button>
            </div>
            {error && <p style={{ color: "red" }}>{error}</p>}
            {recommendations && recommendations.length > 0 ? (
                <div>
                    <h2>Recommendation Results:</h2>
                    {recommendations.map((vehicle) => (
                        <div
                            key={vehicle.vehicleId}
                            style={{ border: "1px solid #ccc", padding: "0.5rem", marginBottom: "1rem" }}
                        >
                            <p><strong>VIN:</strong> {vehicle.vin}</p>
                            <p><strong>Price:</strong> ${vehicle.price}</p>
                            <p><strong>Posting Date:</strong> {formatDate(vehicle.postingDate)}</p>
                            <p><strong>Description:</strong> {vehicle.description}</p>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No recommendations found.</p>
            )}
        </div>
    );
};

export default Recommendations;
