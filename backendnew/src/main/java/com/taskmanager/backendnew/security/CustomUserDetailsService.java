package com.taskmanager.backendnew.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import com.taskmanager.backendnew.entity.User;
import com.taskmanager.backendnew.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // spring security calls this automatically during login attempts

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Step3");
        // check user in the db if missing fail clearly
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // convert your apps role objects into spring securities grandauthority format
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        // package everything into the object type spring security requires and hand it
        // back
        return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
                .password(user.getPassword()).authorities(authorities).build();

    }

}
