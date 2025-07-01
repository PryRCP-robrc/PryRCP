package com.peluqueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.peluqueria.entity.Cliente;
import com.peluqueria.service.ClienteService;

@Controller
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Mostrar formulario de registro
    @GetMapping("/cliente-registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-registro";
    }

    // Procesar registro
    @PostMapping("/cliente-registro")
    public String registrarCliente(@ModelAttribute Cliente cliente) {
    clienteService.registrarCliente(cliente); // ← usa este
        return "redirect:/cliente-login";
}


    // Mostrar formulario de login (si usas una vista personalizada)
    @GetMapping("/cliente-login")
    public String mostrarFormularioLogin() {
        return "cliente-login"; // crea esta vista si aún no la tienes
    }
}
