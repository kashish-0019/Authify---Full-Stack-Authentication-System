package com.example.authenticate.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.authenticate.entity.UserEntity;
import com.example.authenticate.io.ProfileRequest;
import com.example.authenticate.io.ProfileResponse;
import com.example.authenticate.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;
	
	@Override
	public ProfileResponse createProfile(ProfileRequest request) {
		  // check if email already exists
	    if(userRepository.existsByEmailIgnoreCase(request.getEmail())) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email id already exists");
	    }

	    // if email doesn't exist, create new user
	    UserEntity newProfile = convertToUserEntity(request);
	    newProfile = userRepository.save(newProfile);
	    return convertToProfileResponse(newProfile);
	}

	private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        ProfileResponse response = new ProfileResponse();
        response.setName(newProfile.getName());
        response.setEmail(newProfile.getEmail());
        response.setUserId(newProfile.getUserId());
        response.setIsAccountVerified(newProfile.getIsAccountVerified());
        return response;
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setUserId(UUID.randomUUID().toString());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsAccountVerified(false);
        user.setResetOtpExpireAt(0L);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(0L);
        user.setResetOtp(null);
        return user;
    }

	@Override
	public ProfileResponse getProfile(String email) {
		UserEntity existiguser = userRepository.findByEmailIgnoreCase(email)
		.orElseThrow(()-> new UsernameNotFoundException("user not found: "+email));
		return convertToProfileResponse(existiguser);
	}

	@Override
	public void sendResetOtp(String email) {
		UserEntity existingEntity = userRepository.findByEmailIgnoreCase(email)
		.orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
		
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
	
		long expiryTime = System.currentTimeMillis()+(15 * 60 * 1000);
		
		existingEntity.setResetOtp(otp);
		existingEntity.setResetOtpExpireAt(expiryTime);
		
		userRepository.save(existingEntity);
		
		try {
			emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
		}catch(Exception e) {
			throw new RuntimeException("Unable to send email");
		}
		
	}

	@Override
	public void resetPassword(String email, String otp, String newPassword) {
		UserEntity existingUser = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
				
	    if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
	    	    throw new RuntimeException("Invalid OTP");  	
	    }
	    
      	if(existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
    		    throw new RuntimeException("OTP Expired");
      	}
      	
      	existingUser.setPassword(passwordEncoder.encode(newPassword));
      	existingUser.setResetOtp(null);
      	existingUser.setResetOtpExpireAt(0L);
      	
      	userRepository.save(existingUser);
    	
	}

	@Override
	public void sendOtp(String email) {
		UserEntity existingUser =  userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(()->new UsernameNotFoundException("user not found: "+email));
        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()) {
        	      return;
        }
        
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
    	
		long expiryTime = System.currentTimeMillis()+(24 * 60 * 60 * 1000);
		
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);
        
        userRepository.save(existingUser);
        
        try {
        	    emailService.sendOtpEmail(existingUser.getEmail(), otp);
        	    
        }catch(Exception e) {
        	    throw new RuntimeException("Unable to send email");
        }
	}

	@Override
	public void verifyOtp(String email, String otp) {
		UserEntity existingUser =  userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(()->new UsernameNotFoundException("user not found: "+email));
	    
		if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}
		
		if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
			throw new RuntimeException("OTP Expired");
		}
		
		existingUser.setIsAccountVerified(true);
		existingUser.setVerifyOtp(null);
		existingUser.setVerifyOtpExpireAt(0L);
		
		userRepository.save(existingUser);
	}

	@Override
	public String getLoggedInUserId(String email) {
		UserEntity existingUser =  userRepository.findByEmailIgnoreCase(email)
		.orElseThrow(()->new UsernameNotFoundException("user not found: "+email));
	    return existingUser.getUserId();
		
	}

}
