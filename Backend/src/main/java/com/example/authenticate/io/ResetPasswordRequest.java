package com.example.authenticate.io;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest
{
	@NotBlank(message = "Password is required")
	private String newPassword;
	@NotBlank(message = "OTP is required")
	private String otp;
	@NotBlank(message = "Email is required")
	private String email;
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ResetPasswordRequest(@NotBlank(message = "Password is required") String newPassword,
			@NotBlank(message = "OTP is required") String otp, @NotBlank(message = "Email is required") String email) {
		super();
		this.newPassword = newPassword;
		this.otp = otp;
		this.email = email;
	}
	public ResetPasswordRequest() {
		super();
	}
	
	
    
}
