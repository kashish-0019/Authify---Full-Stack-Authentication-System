package com.example.authenticate.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



public class ProfileRequest 
{
	@NotBlank(message = "Name should not be empty")
	private String name;
	@Email(message = "Enter valid email")
	@NotNull(message = "Email should not be empty")
	private String email;
    @Size(min = 6, message = "Password must be atleast 6 characters long")
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ProfileRequest(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}
	public ProfileRequest() {
		super();
	}
	
	

}
