import { useState } from "react";

const API_BASE_URL = "http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/vehicle";

export default function VehicleManager() {
    const [vehicle, setVehicle] = useState({
        vehicleId: "",
        vin: "",
        price: "",
        postingDate: "",
        description: "",
        modelId: "",
    });

    const [vehicleIdToDelete, setVehicleIdToDelete] = useState("");
    const [message, setMessage] = useState("");

    // Handle input change
    const handleChange = (e) => {
        setVehicle({ ...vehicle, [e.target.name]: e.target.value });
    };

    // Create Vehicle (POST)
    const createVehicle = async () => {
        try {
            const response = await fetch(API_BASE_URL, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(vehicle),
            });

            const data = await response.json();
            setMessage(response.ok ? "Vehicle created successfully!" : `Error: ${data.error}`);
        } catch {
            setMessage("Failed to create vehicle");
        }
    };

    // Update Vehicle (PUT)
    const updateVehicle = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/${vehicle.vehicleId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(vehicle),
            });

            const data = await response.json();
            setMessage(response.ok ? "Vehicle updated successfully!" : `Error: ${data.error}`);
        } catch {
            setMessage("Failed to update vehicle");
        }
    };

    // Delete Vehicle (DELETE)
    const deleteVehicle = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/${vehicleIdToDelete}`, {
                method: "DELETE",
            });

            setMessage(response.ok ? "Vehicle deleted successfully!" : "Failed to delete vehicle");
        } catch {
            setMessage("Failed to delete vehicle");
        }
    };

    return (
        <div>
            <h2>Manage Vehicles</h2>

            {/* Create & Update Form */}
            <h3>Create / Update Vehicle</h3>
            <input type="text" name="vehicleId" placeholder="Vehicle ID" value={vehicle.vehicleId} onChange={handleChange} /><br />
            <input type="text" name="vin" placeholder="VIN" value={vehicle.vin} onChange={handleChange} /><br />
            <input type="number" name="price" placeholder="Price" value={vehicle.price} onChange={handleChange} /><br />
            <input type="date" name="postingDate" placeholder="Posting Date" value={vehicle.postingDate} onChange={handleChange} /><br />
            <input type="text" name="description" placeholder="Description" value={vehicle.description} onChange={handleChange} /><br />
            <input type="number" name="modelId" placeholder="Model ID" value={vehicle.modelId} onChange={handleChange} /><br />
            <button onClick={createVehicle}>Create Vehicle</button>
            <button onClick={updateVehicle}>Update Vehicle</button>

            <hr />

            {/* Delete Section */}
            <h3>Delete Vehicle</h3>
            <input type="text" placeholder="Vehicle ID" value={vehicleIdToDelete} onChange={(e) => setVehicleIdToDelete(e.target.value)} /><br />
            <button onClick={deleteVehicle}>Delete Vehicle</button>

            {/* Display messages */}
            <p>{message}</p>
        </div>
    );
}
