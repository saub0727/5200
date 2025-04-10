import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import FindVehicle from "./pages/FindVehicle.jsx";
import FilterVehicles from "./pages/FilterVehicles.jsx";
import VehicleManager from "./pages/VehicleManager.jsx";
import Recommendations from "./pages/Recommendations.jsx";
import Navbar from "./components/Navbar.jsx";
import "./App.css";

const App = () => {
  return (
    <Router>
      <div className="app-container">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/findvehicles" element={<FindVehicle />} />
            <Route path="/filtervehicles" element={<FilterVehicles />} />
            <Route path="/managevehicles" element={<VehicleManager />} />
            <Route path="/recommendations" element={<Recommendations />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
};

export default App;