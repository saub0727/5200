import { useNavigate } from "react-router-dom";

const HomePage = () => {
    const navigate = useNavigate();

    return (
        <div className="p-8 max-w-2xl mx-auto text-center">
            <h1 className="text-2xl font-bold mb-6">Car Genie</h1>
            <div className="bg-white shadow-md rounded-lg p-6 mb-6">
                <p className="text-lg">Find Vehicles by VIN or apply filters to refine your search.</p>
            </div>
            <div className="grid grid-cols-1 gap-4">
                <button
                    className="px-6 py-3 bg-blue-500 text-white rounded hover:bg-blue-700 transition"
                    onClick={() => navigate("/findvehicles")}
                >
                    Find Vehicle
                </button>
                <button
                    className="px-6 py-3 bg-green-500 text-white rounded hover:bg-green-700 transition"
                    onClick={() => navigate("/filtervehicles")}
                >
                    Filter Vehicles
                </button>
                <button
                    className="px-6 py-3 bg-purple-500 text-white rounded hover:bg-purple-700 transition"
                    onClick={() => navigate("/managevehicles")}
                >
                    Manage Vehicles
                </button>
            </div>
        </div>
    );
};

export default HomePage;
