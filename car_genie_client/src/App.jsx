import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage.jsx"; // Import Home Page
import FindVehicle from "./pages/FindVehicle.jsx"; // Import Find Vehicle Page
import FilterVehicles from "./pages/FilterVehicles.jsx";
import VehicleManager from "./pages/VehicleManager.jsx";
import "./App.css";


const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/findvehicles" element={<FindVehicle />} />
                <Route path="/filtervehicles" element={<FilterVehicles />} />
                <Route path="/managevehicles" element={<VehicleManager />} />
            </Routes>
        </Router>
    );
};

export default App;
