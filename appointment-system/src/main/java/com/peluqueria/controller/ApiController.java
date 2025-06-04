package com.peluqueria.controller;

import com.peluqueria.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private CitaService citaService;
    
    @GetMapping("/horarios-disponibles")
    public ResponseEntity<List<String>> obtenerHorariosDisponibles(
            @RequestParam Long peluqueroId,
            @RequestParam String fecha,
            @RequestParam(required = false) List<Long> serviciosIds) {
        try {
            List<String> horarios = citaService.obtenerHorariosDisponibles(peluqueroId, fecha, serviciosIds);
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
