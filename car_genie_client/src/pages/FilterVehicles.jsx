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
        console.log("Sending request to:", `http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/filtervehicles?${queryParams}`);

        try {
            const response = await fetch(`http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/filtervehicles?${queryParams}`);
            const data = await response.json();
            console.log("API Response:", data);
            setVehicles(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error fetching vehicles:", error);
        }
    };

    return (
        <div className="p-4 max-w-3xl mx-auto bg-black text-white min-h-screen">
            <h1 className="text-xl font-bold mb-4">Filter Vehicles</h1>
            <div className="grid grid-cols-2 gap-4 mb-4">
                {Object.keys(enums).map((key) => (
                    <select key={key} name={key} value={filters[key]} onChange={handleChange} className="p-2 border rounded bg-black text-white">
                        <option value="">Select {key}</option>
                        {enums[key].map((option) => (
                            <option key={option} value={option}>{option}</option>
                        ))}
                    </select>
                ))}
                <input name="minPrice" type="number" placeholder="Min Price" value={filters.minPrice} onChange={handleChange} className="p-2 border rounded bg-black text-white" />
                <input name="maxPrice" type="number" placeholder="Max Price" value={filters.maxPrice} onChange={handleChange} className="p-2 border rounded bg-black text-white" />
            </div>
            <button onClick={fetchVehicles} className="w-full p-2 bg-white text-black rounded hover:bg-gray-300">Search</button>

            {vehicles.length > 0 ? (
                <div className="mt-6">
                    <h2 className="text-lg font-semibold mb-2">Results:</h2>
                    {vehicles.map((vehicle, index) => (
                        <div key={vehicle.vehicleId} className="border border-white p-4 mb-4">
                            <h3 className="text-lg font-bold mb-2">{index + 1}</h3>
                            <div className="grid grid-cols-2 gap-4 text-left">
                                <div><span className="font-semibold">VIN:</span><span>{vehicle.vin}</span></div>
                                <div><span className="font-semibold">Price:</span><span>${vehicle.price}</span></div>
                                <div><span className="font-semibold">Posting Date:</span><span>{vehicle.postingDate}</span></div>
                            </div>
                            <div className="mt-2 text-left">
                                <span className="font-semibold">Description:</span>
                                <p className="mt-1 border-t-4 border-white pt-2">{vehicle.description}</p>
                            </div>
                            <hr className="border-t-4 border-white mt-4" />
                        </div>
                    ))}
                </div>
            ) : (
                <p className="text-gray-300 mt-4">No vehicles found.</p>
            )}
        </div>
    );
};

export default FilterVehicles;