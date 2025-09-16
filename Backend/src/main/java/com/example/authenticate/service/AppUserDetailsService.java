package com.example.authenticate.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.authenticate.entity.UserEntity;
import com.example.authenticate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity existingUser = userRepo.findByEmailIgnoreCase(email)
		.orElseThrow(()-> new UsernameNotFoundException("Email not found for the email: "+email));
		
		return new User(existingUser.getEmail(),existingUser.getPassword(),new ArrayList<>());
	}

}
