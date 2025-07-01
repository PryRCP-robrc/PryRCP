package com.peluqueria.controller;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Peluquero;
import com.peluqueria.service.CitaService;
import com.peluqueria.service.PeluqueroService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private PeluqueroService peluqueroService;

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
    public String mostrarDashboard(@RequestParam(value = "codigo", required = false) String codigo,
            Model model) {
        List<Cita> citas = citaService.obtenerTodasLasCitas();
        model.addAttribute("citas", citas);

        List<Peluquero> peluqueros = peluqueroService.obtenerPeluquerosActivos();
        model.addAttribute("peluqueros", peluqueros);

        // Puedes agregar aquí estadísticas/resúmenes si quieres
        model.addAttribute("totalCitas", citas.size());
        model.addAttribute("citasCanceladas", citaService.contarPorEstado(Cita.EstadoCita.CANCELADA));
        model.addAttribute("citasTerminadas", citaService.contarPorEstado(Cita.EstadoCita.TERMINADA));
        model.addAttribute("citasProgramadas", citaService.contarPorEstado(Cita.EstadoCita.PROGRAMADA));

        return "admin-dashboard";
    }

    @PostMapping("/verificar")
    public String verificarDesdeFormulario(@RequestParam String codigo, RedirectAttributes redirectAttributes) {
        Optional<Cita> citaOpt = citaService.buscarPorCodigo(codigo);

        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();

            cita.getCliente().getNombreCompleto(); // fuerza la carga
            redirectAttributes.addFlashAttribute("citaEncontrada", cita);
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró ninguna cita con ese código" + " " + codigo);
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/cambiar-estado/{citaId}")
    public String cambiarEstadoCita(@PathVariable Long citaId,
            @RequestParam Cita.EstadoCita estado) {
        citaService.cambiarEstado(citaId, estado);
        return "redirect:/admin/dashboard";
    }

    // -----------------------
    // GESTIÓN DE PELUQUEROS
    // -----------------------

    // Crear Peluquero
    @PostMapping("/dashboard/peluquero")
    public String crearPeluquero(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String especialidad,
            RedirectAttributes redirectAttributes) {
        Peluquero nuevo = new Peluquero();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setEspecialidad(especialidad);
        nuevo.setActivo(true);

        peluqueroService.crear(nuevo);
        redirectAttributes.addFlashAttribute("mensajePeluquero", "Peluquero creado correctamente");
        return "redirect:/admin/dashboard#peluqueros";
    }

    // Actualizar Peluquero
    @PostMapping("/dashboard/peluquero/{id}")
    public String actualizarPeluquero(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String especialidad,
            @RequestParam(required = false, defaultValue = "true") boolean activo,
            RedirectAttributes redirectAttributes) {
        Peluquero actualizado = new Peluquero();
        actualizado.setNombre(nombre);
        actualizado.setApellido(apellido);
        actualizado.setEspecialidad(especialidad);
        actualizado.setActivo(activo);

        Optional<Peluquero> resultado = peluqueroService.actualizar(id, actualizado);

        if (resultado.isPresent()) {
            redirectAttributes.addFlashAttribute("mensajePeluquero", "Peluquero actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("errorPeluquero", "Error al actualizar: Peluquero no encontrado");
        }

        return "redirect:/admin/dashboard#peluqueros";
    }

    // Eliminar Peluquero
    @GetMapping("/dashboard/peluquero/eliminar/{id}")
    public String eliminarPeluquero(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        boolean eliminado = peluqueroService.eliminar(id);
        if (eliminado) {
            redirectAttributes.addFlashAttribute("mensajePeluquero", "Peluquero eliminado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("errorPeluquero", "Error al eliminar: Peluquero no encontrado");
        }
        return "redirect:/admin/dashboard#peluqueros";
    }
}
