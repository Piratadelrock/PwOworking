package com.discaptraining.apidiscapuser.controllers;

import com.discaptraining.apidiscapuser.controllers.dto.AuthenticacionResponse;
import com.discaptraining.apidiscapuser.controllers.dto.AuthenticationRequest;
import com.discaptraining.apidiscapuser.domain.entity.DiscapUser;
import com.discaptraining.apidiscapuser.response.CustomResponse;
import com.discaptraining.apidiscapuser.security.DiscapUserDetailsService;
import com.discaptraining.apidiscapuser.util.MessageSender;
import com.discaptraining.apidiscapuser.web.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DiscapUserDetailsService discapUserDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    private final MessageSender<DiscapUser> messageSenderClient;

    public AuthController(MessageSender<DiscapUser> messageSenderClient) {
        this.messageSenderClient = messageSenderClient;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticacionResponse> createToken(@RequestBody AuthenticationRequest request) {
        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserDetails userDetails = discapUserDetailsService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);
            return new ResponseEntity<>(new AuthenticacionResponse(jwt),HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Object> newUser(@RequestBody DiscapUser newDiscapUser) {
        ResponseEntity<Object> response;
        try{
            messageSenderClient.execute(discapUserDetailsService.saveUser(newDiscapUser), (newDiscapUser.getPersonID().toString()));
            CustomResponse customResponse = new CustomResponse("Creacion del cliente fue exitosa", HttpStatus.OK);
            customResponse.setResults(newDiscapUser);
            response = new ResponseEntity<>(customResponse, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>("Disculpa tenemos un error tratando de crear el cliente" + newDiscapUser, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
