package com.discaptraining.apidiscapuser.web.security;

import com.discaptraining.apidiscapuser.security.DiscapUserDetailsService;
import com.discaptraining.apidiscapuser.web.security.filter.JwtFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@SuppressWarnings("deprecation")
@EnableWebSecurity
public class Securityconfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DiscapUserDetailsService discapUserDetailsService;

    @Autowired
    private JwtFilterRequest jwtFilterRequest;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder

        auth.userDetailsService(discapUserDetailsService).passwordEncoder(bcryptEncoder);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(discapUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests().antMatchers("/**/authenticate","/**/register").permitAll()
                .anyRequest().authenticated().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilterRequest, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
