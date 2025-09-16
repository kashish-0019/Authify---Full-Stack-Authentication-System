package com.example.authenticate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
	@Autowired
	private JavaMailSender javaMailSender;
    @Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
    
    
    public void sendWelcomeEmail(String toEmail, String name) {
    	    SimpleMailMessage message = new SimpleMailMessage();
     	message.setTo(toEmail);
     	message.setFrom(fromEmail);
     	message.setSubject("Welcome to our platform!");
     	message.setText("Hello "+name+",\n\nThanks for registering with us!\n\nRegards,\nAuthify Team");
     	javaMailSender.send(message);
    	 	
    }
    
    public void sendResetOtpEmail(String toEmail, String otp) {
     	SimpleMailMessage message = new SimpleMailMessage();
     	message.setTo(toEmail);
     	message.setFrom(fromEmail);
      	message.setSubject("Password reset OTP!");
        	message.setText("Your otp for resetting your password is "+otp+". Use this OTP to proceed with resetting your password");
     	javaMailSender.send(message);
     	  
    }
    
    public void sendOtpEmail(String toEmail, String otp) {
    	SimpleMailMessage message = new SimpleMailMessage();
     	message.setTo(toEmail);
     	message.setFrom(fromEmail);
     	message.setSubject("Account Verification OTP");
     	message.setText("Your otp is "+otp+". Verify your account using this OTP");
 	    javaMailSender.send(message);
 	 
    }
}
