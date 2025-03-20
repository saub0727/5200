import { useState } from "react";

const FilterVehicles = () => {
    const [filters, setFilters] = useState({
        condition: "",
        titleStatus: "",
        fuel: "",
        transmission: "",
        drive: "",
        minPrice: "",
        maxPrice: "",
    });
    const [vehicles, setVehicles] = useState([]);

    const enums = {
        condition: ["EXCELLENT", "FAIR", "GOOD", "LIKE_NEW", "NEW", "SALVAGE"],
        titleStatus: ["CLEAN", "LIEN", "MISSING", "PARTS_ONLY", "REBUILT", "SALVAGE"],
        fuel: ["GAS", "DIESEL", "ELECTRIC", "HYBRID", "OTHER"],
        transmission: ["AUTOMATIC", "MANUAL", "OTHER"],
        drive: ["FWD", "RWD", "4WD"],
    };

    const handleChange = (e) => {
        setFilters({ ...filters, [e.target.name]: e.target.value });
    };

    const fetchVehicles = async () => {
        const queryParams = new URLSearchParams(filters).toString();
        const response = await fetch(`http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/filtervehicles?${queryParams}`);
        const data = await response.json();
        setVehicles(Array.isArray(data) ? data : []);
    };

    return (
        <div className="p-4 max-w-3xl mx-auto">
            <h1 className="text-xl font-bold mb-4">Filter Vehicles</h1>
            <div className="grid grid-cols-2 gap-4 mb-4">
                {Object.keys(enums).map((key) => (
                    <select key={key} name={key} value={filters[key]} onChange={handleChange} className="p-2 border rounded">
                        <option value="">Select {key}</option>
                        {enums[key].map((option) => (
                            <option key={option} value={option}>{option}</option>
                        ))}
                    </select>
                ))}
                <input name="minPrice" type="number" placeholder="Min Price" value={filters.minPrice} onChange={handleChange} className="p-2 border rounded" />
                <input name="maxPrice" type="number" placeholder="Max Price" value={filters.maxPrice} onChange={handleChange} className="p-2 border rounded" />
            </div>
            <button onClick={fetchVehicles} className="w-full p-2 bg-blue-500 text-white rounded hover:bg-blue-700">Search</button>

            {vehicles.length > 0 && (
                <div className="mt-6">
                    <h2 className="text-lg font-semibold mb-2">Results:</h2>
                    <div className="space-y-4">
                        {vehicles.map((vehicle, index) => (
                            <div key={vehicle.vehicleId} className="p-4 border rounded bg-gray-100">
                                <p><strong>#{index + 1}</strong></p>
                                <p><strong>VIN:</strong> {vehicle.vin}</p>
                                <p><strong>Price:</strong> ${vehicle.price}</p>
                                <p><strong>Posting Date:</strong> {vehicle.postingDate}</p>
                                <p><strong>Description:</strong> {vehicle.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default FilterVehicles;