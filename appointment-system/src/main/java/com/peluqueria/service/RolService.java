package com.peluqueria.service;

import com.peluqueria.entity.Rol;
import com.peluqueria.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Transactional
    public Rol crear(Rol rol) {
        return rolRepository.save(rol);
    }

    @Transactional
    public Optional<Rol> actualizar(Long id, Rol datosActualizados) {
        return rolRepository.findById(id).map(rol -> {
            rol.setNombre(datosActualizados.getNombre());
            return rolRepository.save(rol);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
