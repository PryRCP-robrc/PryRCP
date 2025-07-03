package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.peluqueria.entity.CustomUserDetails;
import com.peluqueria.entity.Servicio;
import com.peluqueria.service.ServicioService;

@Controller
public class HomeController {

    private ServicioService servicioService;

    @Autowired
    public void ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // PÁGINA PRINCIPAL
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        agregarNombreCompleto(model, authentication);
        return "index";
    }

    // CONTACTO
    @GetMapping("/contacto")
    public String contacto(Model model, Authentication authentication) {
        agregarNombreCompleto(model, authentication);
        return "contacto";
    }

    // SERVICIOS
    @GetMapping("/servicios")
    public String mostrarServicios(Model model, Authentication authentication) {
        agregarNombreCompleto(model, authentication);

        List<Servicio> servicios = servicioService.obtenerServiciosActivos();
        model.addAttribute("servicios", servicios);
        return "servicios";
    }

    // MÉTODO AUXILIAR PARA REUTILIZAR LÓGICA
    private void agregarNombreCompleto(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                String nombreCompleto = userDetails.getNombreCompleto();
                model.addAttribute("nombreCompleto", nombreCompleto);
            }
        }
    }
}
