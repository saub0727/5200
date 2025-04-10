import { Link, useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();
  const currentPath = location.pathname;
  
  const navItems = [
    { name: 'Home', path: '/' },
    { name: 'Find Vehicle', path: '/findvehicles' },
    { name: 'Filter Vehicles', path: '/filtervehicles' },
    { name: 'Manage Vehicles', path: '/managevehicles' },
    { name: 'Recommendations', path: '/recommendations' }
  ];

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">Car Genie</Link>
      </div>
      <ul className="navbar-nav">
        {navItems.map((item) => (
          <li key={item.path} className="nav-item">
            <Link 
              to={item.path} 
              className={currentPath === item.path ? 'nav-link active' : 'nav-link'}
            >
              {item.name}
            </Link>
          </li>
        ))}
      </ul>
    </nav>
  );
};

export default Navbar;