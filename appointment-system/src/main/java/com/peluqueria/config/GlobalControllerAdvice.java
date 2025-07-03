package com.peluqueria.config;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.peluqueria.entity.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void agregarNombreUsuarioAutenticado(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                String nombreCompleto = userDetails.getNombreCompleto();
                model.addAttribute("nombreCompleto", nombreCompleto);
            }
        }
    }
}
