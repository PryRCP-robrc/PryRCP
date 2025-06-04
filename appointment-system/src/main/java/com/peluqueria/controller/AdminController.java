package com.peluqueria.controller;

import com.peluqueria.entity.Cita;
import com.peluqueria.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private CitaService citaService;
    
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout,
                              Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada correctamente");
        }
        return "admin-login";
    }
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("citas", citaService.obtenerTodasLasCitas());
        return "admin-dashboard";
    }
    
    @GetMapping("/verificar")
    public String mostrarVerificar() {
        return "admin-verificar";
    }
    
    @PostMapping("/verificar")
    public String verificarCita(@RequestParam String codigo, Model model) {
        Optional<Cita> citaOpt = citaService.buscarPorCodigo(codigo);
        
        if (citaOpt.isPresent()) {
            model.addAttribute("cita", citaOpt.get());
            model.addAttribute("encontrada", true);
        } else {
            model.addAttribute("error", "No se encontró ninguna cita con ese código");
            model.addAttribute("encontrada", false);
        }
        
        return "admin-verificar";
    }
    
    @PostMapping("/cambiar-estado/{citaId}")
    public String cambiarEstadoCita(@PathVariable Long citaId, 
                                   @RequestParam Cita.EstadoCita estado) {
        citaService.cambiarEstado(citaId, estado);
        return "redirect:/admin/dashboard";
    }
}