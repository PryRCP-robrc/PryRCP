package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Cliente;
import com.peluqueria.service.CitaService;
import com.peluqueria.service.ClienteService;

import jakarta.servlet.http.HttpSession;

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

    @PostMapping("/cliente-login")
public String loginCliente(@ModelAttribute Cliente cliente, Model model, HttpSession session) {
    Cliente clienteBD = clienteService.buscarPorEmail(cliente.getEmail()).orElse(null);
    if (clienteBD != null && clienteBD.getPassword().equals(cliente.getPassword())) {
        session.setAttribute("clienteLogueado", clienteBD);
        return "redirect:/citas/agendar";
    } else {
        model.addAttribute("error", "Email o contraseña incorrectos");
        return "cliente-login";
    }
}

@GetMapping("/panel")
public String mostrarPanel(Model model) {
    // Puedes agregar datos del cliente si lo necesitas
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Cliente cliente = clienteService.buscarPorEmail(email).orElse(null);
    model.addAttribute("cliente", cliente);
    return "panel-cliente"; // crea la vista panel-cliente.html
}

@PostMapping("/panel")
public String editarPerfilDesdePanel(@ModelAttribute Cliente cliente, Model model) {
    clienteService.actualizarCliente(cliente);

    model.addAttribute("cliente", cliente);
    model.addAttribute("mensaje", "Perfil actualizado correctamente");

    List<Cita> citas = citaService.buscarPorCliente(cliente);
    model.addAttribute("citas", citas);

    return "panel-cliente";
}

}
