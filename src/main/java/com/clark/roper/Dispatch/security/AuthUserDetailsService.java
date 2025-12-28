package com.clark.roper.Dispatch.security;

import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.entity.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

   public AuthUserDetailsService(UserRepository userRepository)
   {
       this.userRepository = userRepository;
   }

   @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
   {
       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

       return org.springframework.security.core.userdetails.User
               .withUsername(user.getUsername())
               .password(user.getPassword())
               .roles(user.getRole().name())
               .build();




   }


}
