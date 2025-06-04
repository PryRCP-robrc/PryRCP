package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.peluqueria.entity.Servicio;
import com.peluqueria.service.ServicioService;

@Controller
public class HomeController {

    private ServicioService servicioService;

    @Autowired
    public void ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }
    
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/servicios")
    public String mostrarServicios(Model model){
        List<Servicio> servicios = servicioService.obtenerServiciosActivos();
        model.addAttribute("servicios", servicios);
        return "servicios";  // Aquí defines la vista que usará los servicios
    }
}
