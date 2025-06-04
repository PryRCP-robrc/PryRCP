package com.peluqueria.config;

import com.peluqueria.entity.*;
import com.peluqueria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private PeluqueroRepository peluqueroRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

        @Override
    public void run(String... args) throws Exception {
        // Crear roles
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            rolRepository.save(new Rol("ADMIN"));
        }
        if (rolRepository.findByNombre("CLIENTE").isEmpty()) {
            rolRepository.save(new Rol("CLIENTE"));
        }
        
        // Crear usuario administrador
        if (clienteRepository.findByEmail("admin@peluqueria.com").isEmpty()) {
            Cliente admin = new Cliente();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@peluqueria.com");
            admin.setTelefono("999999999");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(rolRepository.findByNombre("ADMIN").get());
            clienteRepository.save(admin);
        }
        
        // Crear servicios de ejemplo
        if (servicioRepository.count() == 0) {
            Servicio corte = new Servicio();
            corte.setNombre("Corte de Cabello");
            corte.setDescripcion("Corte de cabello profesional");
            corte.setPrecio(new BigDecimal("25.00"));
            corte.setDuracionMinutos(30);
            servicioRepository.save(corte);
            
            Servicio lavado = new Servicio();
            lavado.setNombre("Lavado y Peinado");
            lavado.setDescripcion("Lavado y peinado completo");
            lavado.setPrecio(new BigDecimal("15.00"));
            lavado.setDuracionMinutos(20);
            servicioRepository.save(lavado);
            
            Servicio tinte = new Servicio();
            tinte.setNombre("Tinte de Cabello");
            tinte.setDescripcion("Aplicación de tinte profesional");
            tinte.setPrecio(new BigDecimal("50.00"));
            tinte.setDuracionMinutos(60);
            servicioRepository.save(tinte);
        }
        
        // Crear peluqueros de ejemplo
        if (peluqueroRepository.count() == 0) {
            Peluquero peluquero1 = new Peluquero();
            peluquero1.setNombre("María");
            peluquero1.setApellido("González");
            peluquero1.setEspecialidad("Cortes modernos y tintes");
            peluqueroRepository.save(peluquero1);
            
            Peluquero peluquero2 = new Peluquero();
            peluquero2.setNombre("Carlos");
            peluquero2.setApellido("Rodríguez");
            peluquero2.setEspecialidad("Cortes clásicos y barba");
            peluqueroRepository.save(peluquero2);
        }
    }
}