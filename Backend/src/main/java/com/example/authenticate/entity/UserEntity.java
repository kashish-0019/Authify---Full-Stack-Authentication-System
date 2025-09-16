package com.example.authenticate.entity;

import java.security.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="users")
public class UserEntity 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(unique = true)
	private String userId;
	@Column(unique = true)
	private String email;
	private String password;
	private String verifyOtp;
	private Boolean isAccountVerified;
	private Long verifyOtpExpireAt;
	private String resetOtp;
	private Long resetOtpExpireAt;
    
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getVerifyOtp() {
		return verifyOtp;
	}
	public void setVerifyOtp(String verifyOtp) {
		this.verifyOtp = verifyOtp;
	}
	public Boolean getIsAccountVerified() {
		return isAccountVerified;
	}
	public void setIsAccountVerified(Boolean isAccountVerified) {
		this.isAccountVerified = isAccountVerified;
	}
	public Long getVerifyOtpExpireAt() {
		return verifyOtpExpireAt;
	}
	public void setVerifyOtpExpireAt(Long verifyOtpExpireAt) {
		this.verifyOtpExpireAt = verifyOtpExpireAt;
	}
	public String getResetOtp() {
		return resetOtp;
	}
	public void setResetOtp(String resetOtp) {
		this.resetOtp = resetOtp;
	}
	public Long getResetOtpExpireAt() {
		return resetOtpExpireAt;
	}
	public void setResetOtpExpireAt(Long resetOtpExpireAt) {
		this.resetOtpExpireAt = resetOtpExpireAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public UserEntity(Long id, String name, String userId, String email, String password, String verifyOtp,
			Boolean isAccountVerified, Long verifyOtpExpireAt, String resetOtp, Long resetOtpExpireAt,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.verifyOtp = verifyOtp;
		this.isAccountVerified = isAccountVerified;
		this.verifyOtpExpireAt = verifyOtpExpireAt;
		this.resetOtp = resetOtp;
		this.resetOtpExpireAt = resetOtpExpireAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public UserEntity() {
		super();
	}
	
	

}
