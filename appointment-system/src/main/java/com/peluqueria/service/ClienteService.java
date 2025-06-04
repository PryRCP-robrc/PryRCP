package com.peluqueria.service;

import com.peluqueria.entity.Cliente;
import com.peluqueria.entity.Rol;
import com.peluqueria.repository.ClienteRepository;
import com.peluqueria.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Cliente registrarCliente(Cliente cliente) {
        // Verificar si el email ya existe
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar contraseña
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        
        // Asignar rol de cliente por defecto
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        cliente.setRol(rolCliente);
        
        return clienteRepository.save(cliente);
    }
    
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
    
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}