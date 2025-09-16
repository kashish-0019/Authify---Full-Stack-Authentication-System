import { useNavigate } from "react-router-dom";
import { useContext, useState, useRef, useEffect } from "react"; 
import axios from "axios";
import { toast } from "react-toastify";
import logo_home from "../assets/logo_home.png";
import { AppContext } from "../context/AppContext";

const MenuBar = () => {
  const navigate = useNavigate();
  const { userData, backendUrl, setUserData, setIsLoggedIn } = useContext(AppContext); // ✅ backendUrl fixed
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = async () => {
    try {
      axios.defaults.withCredentials = true;
      const response = await axios.post(`${backendUrl}/logout`);
      if (response.status === 200) {
        setIsLoggedIn(false);
        setUserData(null); // ✅ fixed typo
        navigate("/");
      }
    } catch (error) {
      toast.error(error.response?.data?.message || "Logout failed");
    }
  };

  const sendVerificationOtp = async()=>{
    try{
      axios.defaults.withCredentials = true;
      const response = await axios.post(backendUrl + "/send-otp");
      if(response.status === 200){
        navigate("/email-verify");
        toast.success("OTP has been sent successfully.");
      }else{
        toast.error("Unable to send OTP!");
      }
    }catch(error){
      toast.error(error.response.data.message);
    }
  }

  return (
    <nav className="navbar navbar-expand-lg shadow-sm sticky-top px-4 py-3 bg-white">
      <div className="container-fluid d-flex justify-content-between align-items-center">
        {/* Logo & Title */}
        <div className="d-flex align-items-center gap-2">
          <img src={logo_home} alt="Authify Logo" width={40} height={40} />
          <span className="fw-bold fs-4 text-primary">Authify</span>
        </div>

        {userData ? (
          <div className="position-relative" ref={dropdownRef}>
            <div
              className="bg-dark text-white rounded-circle d-flex justify-content-center align-items-center"
              style={{
                width: "40px",
                height: "40px",
                cursor: "pointer",
                userSelect: "none",
              }}
              onClick={() => setDropdownOpen((prev) => !prev)}
            >
              {userData.name?.[0]?.toUpperCase()}
            </div>

            {dropdownOpen && (
              <div
                className="position-absolute shadow bg-white rounded p-2"
                style={{ top: "50px", right: 0, zIndex: 100 }}
              >
                {!userData.isAccountVerified && (
                  <div
                    className="dropdown-item py-1 px-2"
                    style={{ cursor: "pointer" }}  onClick={sendVerificationOtp}>
                    Verify email
                  </div>
                )}
                <div
                  className="dropdown-item py-1 px-2 text-danger"
                  style={{ cursor: "pointer" }}
                  onClick={handleLogout}
                >
                  Logout
                </div>
              </div>
            )}
          </div>
        ) : (
          // Call-to-Action
          <button
            className="btn btn-primary rounded-pill px-4 shadow-sm"
            style={{ backgroundColor: "#6366f1", border: "none" }}
            onClick={() => navigate("/login")}
          >
            Login <i className="bi bi-arrow-right ms-2"></i>
          </button>
        )}
      </div>
    </nav>
  );
};

export default MenuBar;
