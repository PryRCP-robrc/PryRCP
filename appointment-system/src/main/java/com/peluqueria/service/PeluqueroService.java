package com.peluqueria.service;

import com.peluqueria.entity.Peluquero;
import com.peluqueria.repository.PeluqueroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeluqueroService {
    
    @Autowired
    private PeluqueroRepository peluqueroRepository;
    
    public List<Peluquero> obtenerPeluquerosActivos() {
        return peluqueroRepository.findByActivoTrue();
    }
    
    public Optional<Peluquero> buscarPorId(Long id) {
        return peluqueroRepository.findById(id);
    }
    
    public List<Peluquero> obtenerTodos() {
        return peluqueroRepository.findAll();
    }
}