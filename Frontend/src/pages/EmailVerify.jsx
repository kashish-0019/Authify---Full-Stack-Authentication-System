import { Link, useNavigate } from "react-router-dom";
import logo from "../assets/logo_home.png";
import { useContext, useState, useRef, useEffect } from "react";
import { AppContext } from "../context/AppContext";
import { toast } from "react-toastify";
import axios from "axios";

// Utility to safely combine base URL and path
const apiUrl = (base, path) => {
    if (!base.endsWith("/")) base += "/";
    if (path.startsWith("/")) path = path.slice(1);
    return base + path;
};

const EmailVerify = () => {
    const inputRef = useRef([]);
    const [loading, setLoading] = useState(false);
    const { getUserData, backendUrl, isLoggedIn, userData } = useContext(AppContext);
    const navigate = useNavigate();

    // Auto focus when entering OTP
    const handleChange = (e, i) => {
        const value = e.target.value;
        if (value && i < 5) inputRef.current[i + 1].focus();
        if (!value && i > 0) inputRef.current[i - 1].focus();
    };

    // Paste handler for OTP
    const handlePaste = (e) => {
        e.preventDefault();
        const paste = e.clipboardData.getData("text").slice(0, 6).split("");
        paste.forEach((digit, i) => {
            if (inputRef.current[i]) inputRef.current[i].value = digit;
        });
        const next = paste.length < 6 ? paste.length : 5;
        inputRef.current[next].focus();
    };

    // Collect OTP from inputs
    const getOtp = () => inputRef.current.map((input) => input.value).join("");

    // Verify OTP API call
    const handleVerify = async () => {
        const otp = getOtp();
        if (otp.length !== 6) {
            toast.error("Please enter a valid 6-digit OTP");
            return;
        }

        setLoading(true);
        try {
            const response = await axios.post(apiUrl(backendUrl, "/verify-otp"), { otp }, { withCredentials: true });
            if (response.status === 200) {
                toast.success("OTP verified successfully!");
                getUserData();
                navigate("/");
            } else {
                toast.error("Invalid OTP");
            }
        } catch (error) {
            toast.error("Failed to verify OTP. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    // Redirect if already logged in and verified
    useEffect(() => {
        if (isLoggedIn && userData?.isAccountVerified) navigate("/");
    }, [isLoggedIn, userData, navigate]);

    return (
        <div>
            <div
                className="email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
                style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)" }}
            >
                {/* Logo + Back to Home */}
                <Link to="/" className="position-absolute top-0 start-0 p-4 d-flex align-items-center gap-2 text-decoration-none">
                    <img src={logo} alt="logo" height={32} width={32} />
                    <span className="fs-4 fw-semibold text-light">Authify</span>
                </Link>

                {/* Card */}
                <div className="p-5 rounded-4 shadow bg-white" style={{ width: "400px" }}>
                    <h4 className="text-center fw-bold mb-4">Email Verify OTP</h4>
                    <p className="text-center mb-4">
                        Enter the 6-digit email code sent to your email.
                    </p>

                    {/* OTP Inputs */}
                    <div className="d-flex justify-content-between gap-2 mb-4">
                        {[...Array(6)].map((_, i) => (
                            <input
                                key={i}
                                type="text"
                                maxLength={1}
                                className="form-control text-center fs-4 otp-input"
                                ref={(el) => (inputRef.current[i] = el)}
                                onChange={(e) => handleChange(e, i)}
                                onPaste={handlePaste}
                            />
                        ))}
                    </div>

                    {/* Verify Button */}
                    <button
                        className="btn btn-primary w-100 fw-semibold"
                        disabled={loading}
                        onClick={handleVerify}
                    >
                        {loading ? "Verifying..." : "Verify Email"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default EmailVerify;
