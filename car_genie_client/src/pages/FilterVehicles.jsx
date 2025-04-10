import { useState, useEffect } from "react";

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
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [vehiclesPerPage] = useState(20);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'ascending' });
    const [hasSearched, setHasSearched] = useState(false); // Add this new state

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
        setLoading(true);
        setHasSearched(true); // Set this when a search is initiated
        const queryParams = new URLSearchParams(filters).toString();
        console.log("Sending request to:", `http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/filtervehicles?${queryParams}`);

        try {
            const response = await fetch(`http://localhost:8080/CarGenieServer-1.0-SNAPSHOT/filtervehicles?${queryParams}`);
            const data = await response.json();
            console.log("API Response:", data);
            setVehicles(Array.isArray(data) ? data : []);
            setCurrentPage(1); // Reset to first page on new search
        } catch (error) {
            console.error("Error fetching vehicles:", error);
        } finally {
            setLoading(false);
        }
    };

    // Sorting logic
    const requestSort = (key) => {
        let direction = 'ascending';
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({ key, direction });
    };

    const sortedVehicles = [...vehicles].sort((a, b) => {
        if (sortConfig.key === null) return 0;
        let aValue = a[sortConfig.key];
        let bValue = b[sortConfig.key];

        // Handle numeric values for sorting
        if (sortConfig.key === 'price' || sortConfig.key === 'year') {
            aValue = Number(aValue);
            bValue = Number(bValue);
        }

        if (aValue < bValue) {
            return sortConfig.direction === 'ascending' ? -1 : 1;
        }
        if (aValue > bValue) {
            return sortConfig.direction === 'ascending' ? 1 : -1;
        }
        return 0;
    });

    // Get current vehicles for pagination
    const indexOfLastVehicle = currentPage * vehiclesPerPage;
    const indexOfFirstVehicle = indexOfLastVehicle - vehiclesPerPage;
    const currentVehicles = sortedVehicles.slice(indexOfFirstVehicle, indexOfLastVehicle);

    // Change page
    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    // State for modal
    const [modalInfo, setModalInfo] = useState({ isOpen: false, vehicle: null });

    // Column definitions with headers and accessor functions
    const columns = [
        { header: "ID", accessor: "vehicleId" },
        { header: "VIN", accessor: "vin" },
        { header: "Price", accessor: "price", format: (value) => `$${value.toLocaleString()}` },
        { header: "Year", accessor: "year" },
        { header: "Make", accessor: "make" },
        { header: "Model", accessor: "model" },
        { header: "Condition", accessor: "condition" },
        { header: "Mileage", accessor: "mileage", format: (value) => value ? `${value.toLocaleString()} mi` : 'N/A' },
        { header: "Fuel", accessor: "fuel" },
        { header: "Transmission", accessor: "transmission" },
        { header: "Drive", accessor: "drive" },
        { header: "Title Status", accessor: "titleStatus" },
        { header: "Details", accessor: "details", isAction: true }
    ];

    // Function to get the sort indicator
    const getSortIndicator = (key) => {
        if (sortConfig.key !== key) return '⇵';
        return sortConfig.direction === 'ascending' ? '↑' : '↓';
    };

    // Clear all filters
    const clearFilters = () => {
        setFilters({
            condition: "",
            titleStatus: "",
            fuel: "",
            transmission: "",
            drive: "",
            minPrice: "",
            maxPrice: "",
        });
    };

    // Function to open a vehicle detail in a popup window
    const openVehicleInPopup = (vehicle) => {
        const content = `
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>${vehicle.year || ''} ${vehicle.make || ''} ${vehicle.model || ''} Details</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                        background-color: #121212;
                        color: #ffffff;
                        padding: 20px;
                        margin: 0;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                        background-color: #1e1e1e;
                        border-radius: 8px;
                        padding: 20px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    h1 {
                        margin-top: 0;
                        border-bottom: 1px solid #333;
                        padding-bottom: 10px;
                    }
                    .info-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
                        gap: 15px;
                        margin-bottom: 20px;
                    }
                    .info-item {
                        margin-bottom: 10px;
                    }
                    .info-label {
                        font-weight: bold;
                        color: #9e9e9e;
                    }
                    .description {
                        background-color: #2d2d2d;
                        padding: 15px;
                        border-radius: 4px;
                        margin-top: 20px;
                        white-space: pre-line;
                    }
                    button {
                        background-color: #ffffff;
                        color: #121212;
                        border: none;
                        padding: 8px 16px;
                        border-radius: 4px;
                        cursor: pointer;
                        font-weight: bold;
                        margin-top: 20px;
                    }
                    button:hover {
                        background-color: #e0e0e0;
                    }
                </style>
                <script>
                    // Center the window on load
                    window.onload = function() {
                        // Get the window dimensions
                        const width = 800;
                        const height = 600;
                        
                        // Calculate the position
                        const left = (screen.width / 2) - (width / 2);
                        const top = (screen.height / 2) - (height / 2);
                        
                        // Center the window
                        window.moveTo(left, top);
                        window.resizeTo(width, height);
                    };
                </script>
            </head>
            <body>
                <div class="container">
                    <h1>${vehicle.year || ''} ${vehicle.make || ''} ${vehicle.model || ''}</h1>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">Vehicle ID:</div>
                            <div>${vehicle.vehicleId || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">VIN:</div>
                            <div>${vehicle.vin || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Price:</div>
                            <div>$${vehicle.price ? vehicle.price.toLocaleString() : 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Year:</div>
                            <div>${vehicle.year || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Make:</div>
                            <div>${vehicle.make || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Model:</div>
                            <div>${vehicle.model || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Condition:</div>
                            <div>${vehicle.condition || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Mileage:</div>
                            <div>${vehicle.mileage ? vehicle.mileage.toLocaleString() + ' mi' : 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Fuel:</div>
                            <div>${vehicle.fuel || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Transmission:</div>
                            <div>${vehicle.transmission || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Drive:</div>
                            <div>${vehicle.drive || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Title Status:</div>
                            <div>${vehicle.titleStatus || 'N/A'}</div>
                        </div>
                    </div>
                    <h2>Description</h2>
                    <div class="description">
                        ${vehicle.description || 'No description available for this vehicle.'}
                    </div>
                    <div style="text-align: center;">
                        <button onclick="window.close()">Close Window</button>
                    </div>
                </div>
            </body>
            </html>
        `;

        // Open a new window and write the content
        const newWindow = window.open('about:blank', '_blank', 'width=800,height=600,resizable=yes,scrollbars=yes');
        if (newWindow) {
            newWindow.document.write(content);
            newWindow.document.close();
            newWindow.focus();
        } else {
            alert("Popup blocked. Please allow popups for this site to view vehicle details.");
        }
    };

    return (
        <div className="p-4 max-w-6xl mx-auto bg-black text-white min-h-screen">
            <h1 className="text-2xl font-bold mb-6">Filter Vehicles</h1>

            {/* Filter section */}
            <div className="bg-gray-900 p-4 rounded-lg mb-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                    {Object.keys(enums).map((key) => (
                        <div key={key} className="flex flex-col">
                            <label className="mb-1 text-sm text-gray-400 capitalize">{key.replace(/([A-Z])/g, ' $1').toLowerCase()}</label>
                            <select
                                name={key}
                                value={filters[key]}
                                onChange={handleChange}
                                className="p-2 border rounded bg-black text-white"
                            >
                                <option value="">Any</option>
                                {enums[key].map((option) => (
                                    <option key={option} value={option}>
                                        {option.replace(/_/g, ' ')}
                                    </option>
                                ))}
                            </select>
                        </div>
                    ))}
                    <div className="flex flex-col">
                        <label className="mb-1 text-sm text-gray-400">Min Price</label>
                        <input
                            name="minPrice"
                            type="number"
                            placeholder="Min Price"
                            value={filters.minPrice}
                            onChange={handleChange}
                            className="p-2 border rounded bg-black text-white"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label className="mb-1 text-sm text-gray-400">Max Price</label>
                        <input
                            name="maxPrice"
                            type="number"
                            placeholder="Max Price"
                            value={filters.maxPrice}
                            onChange={handleChange}
                            className="p-2 border rounded bg-black text-white"
                        />
                    </div>
                </div>
                <div className="flex gap-4">
                    <button
                        onClick={fetchVehicles}
                        disabled={loading}
                        className="flex-1 p-2 bg-white text-black rounded hover:bg-gray-300 disabled:opacity-50"
                    >
                        {loading ? 'Searching...' : 'Search'}
                    </button>
                    <button
                        onClick={clearFilters}
                        className="p-2 bg-transparent border border-white text-white rounded hover:bg-gray-800"
                    >
                        Clear Filters
                    </button>
                </div>
            </div>

            {/* Results section */}
            {loading ? (
                <div className="text-center py-12">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-white"></div>
                    <p className="mt-2">Loading vehicles...</p>
                </div>
            ) : vehicles.length > 0 ? (
                <div className="mt-6">
                    <h2 className="text-lg font-semibold mb-4">Results: {vehicles.length} vehicles found</h2>

                    {/* Responsive Table with Column Width Management */}
                    <div className="mb-2 text-sm italic text-gray-400 text-center">Scroll horizontally to view all data →</div>
                    <div className="overflow-x-auto mx-auto p-4 bg-gray-900 rounded-lg border border-gray-700" style={{
                        maxWidth: "95%",
                        overflowX: "auto",
                        WebkitOverflowScrolling: "touch",
                        paddingBottom: "16px", // More padding at bottom for scrollbar
                        margin: "0 auto 20px auto", // Added vertical margin
                    }}>
                        <table className="mx-auto bg-gray-900 rounded-lg overflow-hidden" style={{
                            borderCollapse: 'collapse',
                            margin: "0 auto" // Center the table
                        }}>
                            <thead className="bg-gray-800">
                                <tr>
                                    {columns.map((column) => (
                                        <th
                                            key={column.accessor}
                                            style={{
                                                padding: '12px 16px',
                                                textAlign: 'left',
                                                fontSize: '12px',
                                                fontWeight: '500',
                                                color: '#d1d5db',
                                                textTransform: 'uppercase',
                                                letterSpacing: '0.05em',
                                                cursor: 'pointer',
                                                border: '1px solid white',
                                                whiteSpace: 'nowrap' // Prevent wrapping in headers
                                            }}
                                            onClick={() => requestSort(column.accessor)}
                                        >
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                                {column.header}
                                                <span style={{ color: '#6b7280' }}>{getSortIndicator(column.accessor)}</span>
                                            </div>
                                        </th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {currentVehicles.map((vehicle) => (
                                    <tr key={vehicle.vehicleId} style={{ backgroundColor: '#1f2937' }}>
                                        {columns.map((column) => {
                                            if (column.isAction) {
                                                return (
                                                    <td
                                                        key={`${vehicle.vehicleId}-${column.accessor}`}
                                                        style={{
                                                            padding: '12px 16px',
                                                            border: '1px solid white',
                                                            whiteSpace: 'nowrap'
                                                        }}
                                                    >
                                                        <button
                                                            onClick={() => openVehicleInPopup(vehicle)}
                                                            style={{
                                                                padding: '4px 12px',
                                                                backgroundColor: '#2563eb',
                                                                color: 'white',
                                                                borderRadius: '4px',
                                                                cursor: 'pointer',
                                                                transition: 'background-color 0.2s'
                                                            }}
                                                        >
                                                            View
                                                        </button>
                                                    </td>
                                                );
                                            }
                                            const value = vehicle[column.accessor];
                                            const displayValue = column.format && value !== undefined
                                                ? column.format(value)
                                                : value || 'N/A';

                                            return (
                                                <td
                                                    key={`${vehicle.vehicleId}-${column.accessor}`}
                                                    style={{
                                                        padding: '12px 16px',
                                                        border: '1px solid white',
                                                        whiteSpace: 'nowrap'
                                                    }}
                                                >
                                                    {displayValue}
                                                </td>
                                            );
                                        })}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination */}
                    {vehicles.length > vehiclesPerPage && (
                        <div className="mt-6 flex flex-col items-center">
                            <div className="mb-2 text-sm">
                                Page <span className="font-bold">{currentPage}</span> of {Math.ceil(vehicles.length / vehiclesPerPage)}
                            </div>
                            <nav className="flex items-center">
                                {/* First page button */}
                                <button
                                    onClick={() => paginate(1)}
                                    disabled={currentPage === 1}
                                    className="px-3 py-1 border border-gray-700 bg-gray-800 disabled:opacity-50"
                                >
                                    «
                                </button>

                                {/* Previous page button */}
                                <button
                                    onClick={() => paginate(currentPage > 1 ? currentPage - 1 : 1)}
                                    disabled={currentPage === 1}
                                    className="px-3 py-1 border border-gray-700 bg-gray-800 disabled:opacity-50 mx-1"
                                >
                                    ‹
                                </button>

                                {/* Page numbers */}
                                {Array.from({ length: Math.ceil(vehicles.length / vehiclesPerPage) }).map((_, index) => {
                                    // Only show a window of 5 pages
                                    if ((index + 1 < currentPage - 2) || (index + 1 > currentPage + 2)) {
                                        // Only show first, last, and pages around current
                                        if (index !== 0 && index !== Math.ceil(vehicles.length / vehiclesPerPage) - 1) {
                                            return null;
                                        }
                                    }

                                    // Show ellipsis for page gaps
                                    if (index === 1 && currentPage > 4) {
                                        return <span key="ellipsis-start" className="px-3 py-1 border border-gray-700 bg-gray-800 mx-1">...</span>;
                                    }

                                    if (index === Math.ceil(vehicles.length / vehiclesPerPage) - 2 && currentPage < Math.ceil(vehicles.length / vehiclesPerPage) - 3) {
                                        return <span key="ellipsis-end" className="px-3 py-1 border border-gray-700 bg-gray-800 mx-1">...</span>;
                                    }

                                    const isCurrentPage = currentPage === index + 1;
                                    return (
                                        <button
                                            key={index}
                                            onClick={() => paginate(index + 1)}
                                            style={{
                                                padding: '0.25rem 0.75rem',
                                                margin: '0 0.25rem',
                                                border: '1px solid #4b5563',
                                                backgroundColor: isCurrentPage ? '#ffffff' : '#1f2937',
                                                color: isCurrentPage ? '#000000' : '#ffffff',
                                                fontWeight: isCurrentPage ? 'bold' : 'normal',
                                                borderRadius: '0.25rem',
                                            }}
                                        >
                                            {index + 1}
                                        </button>
                                    );
                                })}

                                {/* Next page button */}
                                <button
                                    onClick={() => paginate(currentPage < Math.ceil(vehicles.length / vehiclesPerPage) ? currentPage + 1 : currentPage)}
                                    disabled={currentPage === Math.ceil(vehicles.length / vehiclesPerPage)}
                                    className="px-3 py-1 border border-gray-700 bg-gray-800 disabled:opacity-50 mx-1"
                                >
                                    ›
                                </button>

                                {/* Last page button */}
                                <button
                                    onClick={() => paginate(Math.ceil(vehicles.length / vehiclesPerPage))}
                                    disabled={currentPage === Math.ceil(vehicles.length / vehiclesPerPage)}
                                    className="px-3 py-1 border border-gray-700 bg-gray-800 disabled:opacity-50"
                                >
                                    »
                                </button>
                            </nav>
                        </div>
                    )}
                </div>
            ) : hasSearched ? ( // Only show "no results" message after a search
                <div className="text-center py-12 bg-gray-900 rounded-lg">
                    <p className="text-gray-300">No vehicles found. Adjust your filters and try again.</p>
                </div>
            ) : null}
        </div>
    );
};

export default FilterVehicles;