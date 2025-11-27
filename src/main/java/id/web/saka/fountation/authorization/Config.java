package id.web.saka.fountation.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class Config {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.withUsername("user2")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .authorities("profile", "openid")
                .build();

        UserDetails admin = User.withUsername("testuser")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .authorities("profile", "openid", "message.read", "message.write")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }

    // Define the password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
