package com.peluqueria.service;

import com.peluqueria.entity.Servicio;
import com.peluqueria.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<Servicio> obtenerServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }
    
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }
    
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }
}