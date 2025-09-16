import { useContext } from 'react';
import header from '../assets/header.png';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';

const Header = () => {
    const {userData} = useContext(AppContext);
    const navigate = useNavigate();
    
    return (
        <div className="text-center d-flex flex-column align-items-center justify-content-center py-5 px-3">
            
            {/* Hero Illustration */}
            <img 
                src={header} 
                alt="Secure Authentication" 
                width={160} 
                className="mb-4 rounded shadow-sm"
            />

            {/* Greeting */}
            <h5 className="fw-semibold text-secondary mb-2">
                Welcome {userData ? userData.name : "Developer"} <span role="img" aria-label="wave">👋</span>
            </h5>

            {/* Main Heading */}
            <h1 className="fw-bold display-5 mb-3" style={{ color: "#111827" }}>
                Build <span style={{ color: "#6366f1" }}>Secure</span> Applications Faster
            </h1>

            {/* Description (Resume-Friendly) */}
            <p className="text-muted fs-5 mb-4" style={{ maxWidth: "650px" }}>
                <strong>Authify</strong> is a full-stack authentication system powered by 
                <span className="fw-semibold text-success"> Spring Boot</span> and 
                <span className="fw-semibold text-info"> React</span>.  
                It implements <span className="fw-semibold">JWT-based authentication </span> 
                to provide reliable login, registration, and secure API access.  
                Designed for developers who want <span className="fw-semibold">scalability, simplicity, and security</span> in their projects.
            </p>

            {/* Action Buttons */}
            <div className="d-flex gap-3">
                <button 
                    className="btn btn-primary rounded-pill px-4 py-2 shadow"
                    style={{ backgroundColor: "#6366f1", border: "none" }}
                    onClick={() => navigate("/login")}
                >
                   Get Started
                </button>
                
            </div>
        </div>
    );
};

export default Header;
