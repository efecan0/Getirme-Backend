package com.example.getirme.config;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.User;
import com.example.getirme.repository.UserRepository;
import jakarta.persistence.DiscriminatorValue;
import jakarta.transaction.Transactional;
import org.apache.catalina.filters.CorsFilter;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.getirme.exception.MessageType.NO_RECORD_EXIST;
import static com.example.getirme.exception.MessageType.UNAUTHORIZED;

@Configuration
public class AppConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String phoneNumber){
                Optional<User> optional = userRepository.findByPhoneNumber(phoneNumber);
                if(optional.isPresent()) {
                    User user = optional.get();

                    // Hibernate Proxy'i bypass ederek gerçek sınıfı buluyoruz
                    Class<?> realClass = Hibernate.getClass(user);
                    if (realClass.isAnnotationPresent(DiscriminatorValue.class)) {
                        String userType = realClass.getAnnotation(DiscriminatorValue.class).value();
                        user.setUserType(userType);
                    }
                    return user;
                }
                throw new BaseException(new ErrorMessage(UNAUTHORIZED , null));
            }

        };
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
