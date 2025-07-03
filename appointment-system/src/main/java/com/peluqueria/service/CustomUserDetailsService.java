package com.peluqueria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.peluqueria.entity.Cliente;
import com.peluqueria.repository.ClienteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Cliente cliente = clienteRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + cliente.getRol().getNombre()));

    //  usamos CustomUserDetails
    return new com.peluqueria.entity.CustomUserDetails(cliente, authorities);
}

}