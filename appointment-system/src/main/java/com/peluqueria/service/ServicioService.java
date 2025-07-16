package com.peluqueria.service;


import com.peluqueria.entity.Servicio;
import com.peluqueria.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional()
public class ServicioService {


    @Autowired
    private ServicioRepository servicioRepository;


    // Obtener todos los servicios
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }


    // Obtener solo servicios activos
    public List<Servicio> obtenerServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }


    // Buscar por ID
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }


    // Crear un nuevo servicio
    @Transactional
    public Servicio crear(Servicio servicio) {
        return servicioRepository.save(servicio);
    }


    // Actualizar un servicio existente
    @Transactional
    public Servicio actualizar(Long id, Servicio servicioActualizado) {
        Servicio existente = servicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));


        existente.setNombre(servicioActualizado.getNombre());
        existente.setDescripcion(servicioActualizado.getDescripcion());
        existente.setPrecio(servicioActualizado.getPrecio());
        existente.setDuracionMinutos(servicioActualizado.getDuracionMinutos());
        existente.setActivo(servicioActualizado.getActivo());


        return servicioRepository.save(existente);
    }


    // Eliminar un servicio por ID
    @Transactional
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new EntityNotFoundException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }
}
