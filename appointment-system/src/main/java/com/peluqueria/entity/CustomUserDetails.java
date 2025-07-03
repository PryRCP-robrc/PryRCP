package com.peluqueria.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

    private final Cliente cliente;

    public CustomUserDetails(Cliente cliente, Collection<? extends GrantedAuthority> authorities) {
        super(cliente.getEmail(), cliente.getPassword(), authorities);
        this.cliente = cliente;
    }

    public String getNombreCompleto() {
        return cliente.getNombre() + " " + cliente.getApellido();
    }

    public Cliente getCliente() {
        return cliente;
    }
}

