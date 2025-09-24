import { Link, useNavigate } from "react-router-dom";
import logo_home from "../assets/logo_home.png";
import { useState, useContext } from "react";
import { AppContext } from "../context/AppContext";
import axios from "axios";
import { toast } from "react-toastify";

const ResetPassword = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [otp, setOtp] = useState("");
    const [isEmailSent, setIsEmailSent] = useState(false);
    const { backendUrl } = useContext(AppContext);

    axios.defaults.withCredentials = true;

    const handleEmailSubmit = async (e) => {
        e.preventDefault();
        if (!email) return toast.error("Please enter your email.");
        try {
            setLoading(true);
            const res = await axios.post(`${backendUrl}/send-reset-otp?email=${encodeURIComponent(email)}`);
            if (res.status === 200) {
                setIsEmailSent(true);
                toast.success("OTP sent to your email!");
            }
        } catch (err) {
            // Check for a specific error from the backend response
            if (err.response) {
                toast.error(err.response.data?.message || "Failed to send OTP. Try again.");
            } else {
                toast.error("Network error. Please try again.");
            }
        } finally { setLoading(false); }
    };

    const handlePasswordReset = async (e) => {
        e.preventDefault();
        if (!otp || !newPassword) return toast.error("Please enter OTP and new password.");
        try {
            setLoading(true);
            const res = await axios.post(`${backendUrl}/reset-password`, { email, otp, newPassword });
            if (res.status === 200) {
                toast.success("Password reset successful! Please login.");
                navigate("/login");
            }
        } catch (err) {
            // Check for a specific error from the backend response
            if (err.response) {
                toast.error(err.response.data?.message || "Invalid OTP or failed to reset password.");
            } else {
                toast.error("Network error. Please try again.");
            }
        } finally { setLoading(false); }
    };

    return (
        <div className="email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
            style={{ background: "linear-gradient(90deg, #6a5af9, #826f9c)" }}>
            <Link to="/" className="position-absolute top-0 start-0 p-4 d-flex align-items-center gap-2 text-decoration-none">
                <img src={logo_home} alt="logo" height={32} width={32} />
                <span className="fs-4 fw-semibold text-light">Authify</span>
            </Link>

            {!isEmailSent ? (
                <div className="rounded-4 p-5 text-center shadow bg-white" style={{ width: "100%", maxWidth: "400px" }}>
                    <h4 className="mb-2">Reset Password</h4>
                    <p className="mb-4">Enter your registered email address</p>
                    <form onSubmit={handleEmailSubmit}>
                        <input type="email" className="form-control mb-3" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required />
                        <button type="submit" className="btn btn-primary w-100" disabled={loading}>{loading ? "Sending..." : "Submit"}</button>
                    </form>
                </div>
            ) : (
                <div className="rounded-4 p-5 text-center shadow bg-white" style={{ width: "100%", maxWidth: "400px" }}>
                    <h4 className="mb-2">Verify OTP & Reset</h4>
                    <form onSubmit={handlePasswordReset}>
                        <input type="text" className="form-control mb-3" placeholder="Enter OTP" value={otp} onChange={e => setOtp(e.target.value)} required />
                        <input type="password" className="form-control mb-3" placeholder="New Password" value={newPassword} onChange={e => setNewPassword(e.target.value)} required />
                        <button type="submit" className="btn btn-primary w-100" disabled={loading}>{loading ? "Resetting..." : "Reset Password"}</button>
                    </form>
                </div>
            )}
        </div>
    );
};

export default ResetPassword;
