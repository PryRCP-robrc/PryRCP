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

    // Crear un nuevo Servicio
    public Servicio crear(Servicio servicio){
        servicio.setId(null);
        servicio.setActivo(true);
        return servicioRepository.save(servicio);
    }


    //Actualizar un Servicio
    public Optional<Servicio> actualizar (long id, Servicio datosActualizados){
        return servicioRepository.findById(id).map(servicio ->{
            servicio.setNombre(datosActualizados.getNombre());
            servicio.setDescripcion(datosActualizados.getDescripcion());
            servicio.setPrecio(datosActualizados.getPrecio());
            servicio.setDuracionMinutos(datosActualizados.getDuracionMinutos());
            servicio.setActivo(datosActualizados.getActivo());
            return servicioRepository.save(servicio);
        });
    }

    //Eliminar un servicio por ID
    public boolean eliminar(long id){
        if(servicioRepository.existsById(id)){
            servicioRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}