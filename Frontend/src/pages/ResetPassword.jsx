import { Link, useNavigate } from "react-router-dom";
import logo_home from "../assets/logo_home.png";
import { useState, useContext } from "react";
import { AppContext } from "../context/AppContext";
import axios from "axios";
import { toast } from "react-toastify";
import { apiUrl } from "../util/api";

const ResetPassword = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [isEmailSent, setIsEmailSent] = useState(false);

  const { backendUrl } = useContext(AppContext);
  axios.defaults.withCredentials = true;

  // Step 1: Send Reset OTP
  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    if (!email) {
      toast.error("Please enter your email.");
      return;
    }
    try {
      setLoading(true);
    
      const res = await axios.post(
        apiUrl(backendUrl, `/send-reset-otp?email=${encodeURIComponent(email)}`)
      );
      if (res.status === 200) {
        setIsEmailSent(true);
        toast.success("OTP sent to your email!");
      }
    } catch (error) {
      console.error("Error sending OTP:", error);
      toast.error("Failed to send OTP. Try again.");
    } finally {
      setLoading(false);
    }
  };

  // Step 2: Reset Password (with OTP)
  const handlePasswordReset = async (e) => {
    e.preventDefault();
    if (!otp || !newPassword) {
      toast.error("Please enter OTP and new password.");
      return;
    }
    try {
      setLoading(true);
      const res = await axios.post(apiUrl(backendUrl, "/reset-password"), {
        email,
        otp,
        newPassword,
      });
      if (res.status === 200) {
        toast.success("Password reset successful! Please login.");
        navigate("/login");
      }
    } catch (error) {
      console.error("Error resetting password:", error);
      toast.error("Invalid OTP or failed to reset password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
      style={{
        background: "linear-gradient(90deg, #6a5af9, #826f9c)",
        border: "none",
      }}
    >
      {/* Logo + Back to Home */}
      <Link
        to="/"
        className="position-absolute top-0 start-0 p-4 d-flex align-items-center gap-2 text-decoration-none"
      >
        <img src={logo_home} alt="logo" height={32} width={32} />
        <span className="fs-4 fw-semibold text-light">Authify</span>
      </Link>

      {/* Step 1: Enter Email */}
      {!isEmailSent && (
        <div
          className="rounded-4 p-5 text-center shadow bg-white"
          style={{ width: "100%", maxWidth: "400px" }}
        >
          <h4 className="mb-2">Reset Password</h4>
          <p className="mb-4">Enter your registered email address</p>

          <form onSubmit={handleEmailSubmit}>
            <div className="input-group mb-4 bg-light rounded-pill">
              <span className="input-group-text bg-transparent border-0 ps-4">
                <i className="bi bi-envelope"></i>
              </span>
              <input
                type="email"
                className="form-control bg-transparent border-0 ps-1 pe-4 rounded-end"
                placeholder="Enter your email address"
                style={{ height: "50px" }}
                onChange={(e) => setEmail(e.target.value)}
                value={email}
                required
              />
            </div>

            <button
              className="btn btn-primary w-100 py-2 fw-semibold"
              type="submit"
              disabled={loading}
            >
              {loading ? "Sending..." : "Submit"}
            </button>
          </form>
        </div>
      )}

      {/* Step 2: Enter OTP + New Password */}
      {isEmailSent && (
        <div
          className="rounded-4 p-5 text-center shadow bg-white"
          style={{ width: "100%", maxWidth: "400px" }}
        >
          <h4 className="mb-2">Verify OTP & Reset</h4>
          <p className="mb-4">Enter the OTP sent to your email and set new password</p>

          <form onSubmit={handlePasswordReset}>
            <input
              type="text"
              className="form-control text-center mb-3"
              placeholder="Enter OTP"
              maxLength={6}
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />

            <input
              type="password"
              className="form-control mb-3"
              placeholder="Enter new password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />

            <button
              className="btn btn-primary w-100 py-2 fw-semibold"
              type="submit"
              disabled={loading}
            >
              {loading ? "Resetting..." : "Reset Password"}
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default ResetPassword;
