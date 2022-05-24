package com.example.mybookshopapp.config.security;

import com.example.mybookshopapp.service.security.BookstoreUserDetailsService;
import com.example.mybookshopapp.util.security.JWTRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter filter;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService, JWTRequestFilter filter) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.filter = filter;
    }

    @Bean
    @SuppressWarnings("squid:S5344")
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String signInUrl = "/signin";
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile", "/pay", "/viewed").authenticated()
                .antMatchers("/users/**", "/books/new").hasAuthority("ADMIN")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage(signInUrl).failureUrl(signInUrl)
                .and().logout().logoutUrl("/logout").logoutSuccessUrl(signInUrl).deleteCookies("token")
                .and().oauth2Login()
                .and().oauth2Client();

            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

    }
}
