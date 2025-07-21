package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Cliente;
import com.peluqueria.entity.CustomUserDetails;
import com.peluqueria.service.CitaService;
import com.peluqueria.service.ClienteService;

@Controller
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CitaService citaService;

    // Mostrar formulario de registro
    @GetMapping("/cliente-registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-registro";
    }

    // Procesar registro
    @PostMapping("/cliente-registro")
    public String registrarCliente(@ModelAttribute Cliente cliente) {
    clienteService.registrarCliente(cliente); 
        return "redirect:/cliente-login";
}


    // Mostrar formulario de login (si usas una vista personalizada)
    @GetMapping("/cliente-login")
    public String mostrarFormularioLogin() {
        return "cliente-login"; // crea esta vista si aún no la tienes
    }

@GetMapping("/historial-citas")
public String mostrarHistorialCitas(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    Cliente cliente = userDetails.getCliente(); // Aquí obtienes el cliente autenticado

    List<Cita> citas = citaService.obtenerCitasPorCliente(cliente.getId());
    model.addAttribute("citas", citas);

    return "historial-citas";
}

}
