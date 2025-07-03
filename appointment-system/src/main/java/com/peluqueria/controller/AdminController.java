package com.peluqueria.controller;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Peluquero;
import com.peluqueria.entity.Rol;
import com.peluqueria.entity.Servicio;
import com.peluqueria.service.CitaService;
import com.peluqueria.service.PeluqueroService;
import com.peluqueria.service.RolService;
import com.peluqueria.service.ServicioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private PeluqueroService peluqueroService;

    @Autowired
    private RolService rolService;

    @Autowired
    private ServicioService servicioService;

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

        List<Servicio> servicios = servicioService.obtenerServiciosActivos();
        model.addAttribute("servicios", servicios);

        List<Rol> roles = rolService.listarTodos();
        model.addAttribute("roles", roles);

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

    // --------------------
    // GESTIÓN DE ROLES
    // --------------------

    // Crear Rol
    @PostMapping("/dashboard/rol")
    public String crearRol(@RequestParam String nombre, RedirectAttributes redirectAttributes) {
        Rol nuevo = new Rol();
        nuevo.setNombre(nombre);
        rolService.crear(nuevo);
        redirectAttributes.addFlashAttribute("mensajeRol", "Rol creado correctamente");
        return "redirect:/admin/dashboard#roles";
    }

    // Actualizar Rol
    @PostMapping("/dashboard/rol/{id}")
    public String actualizarRol(@PathVariable Long id,
            @RequestParam String nombre,
            RedirectAttributes redirectAttributes) {
        Rol actualizado = new Rol();
        actualizado.setNombre(nombre);
        Optional<Rol> resultado = rolService.actualizar(id, actualizado);
        if (resultado.isPresent()) {
            redirectAttributes.addFlashAttribute("mensajeRol", "Rol actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("mensajeRol", "Error al actualizar: Rol no encontrado");
        }
        return "redirect:/admin/dashboard#roles";
    }

    // Eliminar Rol
    @GetMapping("/dashboard/rol/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        boolean eliminado = rolService.eliminar(id);
        if (eliminado) {
            redirectAttributes.addFlashAttribute("mensajeRol", "Rol eliminado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("errorRol", "Error al eliminar: Rol no encontrado");
        }
        return "redirect:/admin/dashboard#roles";
    }

    // --------------------
    // GESTIÓN DE SERVICIOS
    // --------------------

    @PostMapping("/dashboard/servicio")
    public String crearServicio(@RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam Integer duracionMinutos,
            RedirectAttributes redirectAttributes){
        Servicio nuevo = new Servicio();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        nuevo.setPrecio(null);
        nuevo.setDuracionMinutos(null);
        nuevo.setActivo(true);

        servicioService.crear(nuevo);
        redirectAttributes.addFlashAttribute("mensajeServicio", "Servicio creado correctamente");
        return "redirect:/admin/dashboard#servicios";
    }

    // Actualizar Servicio
    @PostMapping("/dashboard/servicio/{id}")
    public String actualizarServicio(@PathVariable long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam String duracionMinutos,
            RedirectAttributes redirectAttributes){
        Servicio actualizado = new Servicio();
        actualizado.setNombre(nombre);
        actualizado.setDescripcion(descripcion);
        actualizado.setPrecio(null);
        actualizado.setDuracionMinutos(null);
        actualizado.setActivo(true);

        Optional<Servicio> resultado = servicioService.actualizar(id, actualizado);

        if(resultado.isPresent()){
            redirectAttributes.addFlashAttribute("mensajeServicio", "Servicio Actualizado Correctamente");
        }else{
            redirectAttributes.addFlashAttribute("errorServicio", "Error al actualizar: Servicio no encontrado");
        }
        return "redirect:/admin/dashboard#servicios";
    } 

    // Eliminar Servicio
    @GetMapping("/dashboard/servicio/eliminar/{id}")
    public String eliminarServicio(@PathVariable long id,
            RedirectAttributes redirectAttributes){
        boolean eliminado=servicioService.eliminar(id);
        if(eliminado){
            redirectAttributes.addFlashAttribute("mensajeServicio", "Servicio Eliminado Correctamente");
        }else{
            redirectAttributes.addFlashAttribute("errorServicio", "Error al eliminar: Servicio no encontrado");
        }
        return "redirect:/admin/dashboard#servicios";
    }

}
