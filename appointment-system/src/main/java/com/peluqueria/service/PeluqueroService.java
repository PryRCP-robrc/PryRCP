package com.peluqueria.service;


import com.peluqueria.entity.Peluquero;
import com.peluqueria.repository.PeluqueroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional()
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


    // Crear un nuevo peluquero
    public Peluquero crear (Peluquero peluquero){
        peluquero.setId(null);
        
        return peluqueroRepository.save(peluquero);
    }


    //Actualizar un peluquero
    public Optional<Peluquero> actualizar (Long id, Peluquero datosActualizados){
        return peluqueroRepository.findById(id).map(peluquero -> {
            peluquero.setNombre(datosActualizados.getNombre());
            peluquero.setApellido(datosActualizados.getApellido());
            peluquero.setEspecialidad(datosActualizados.getEspecialidad());
            peluquero.setActivo(datosActualizados.getActivo());
            return peluqueroRepository.save(peluquero);
        });
    }


    //Eliminar un peluquero por ID
    public boolean eliminar(Long id) {
        if (peluqueroRepository.existsById(id)){
            peluqueroRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}
