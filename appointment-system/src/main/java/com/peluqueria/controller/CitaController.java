package com.peluqueria.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Cliente;
import com.peluqueria.entity.CustomUserDetails;
import com.peluqueria.entity.Peluquero;
import com.peluqueria.entity.Servicio;
import com.peluqueria.service.CitaService;
import com.peluqueria.service.ClienteService;
import com.peluqueria.service.PDFService;
import com.peluqueria.service.PeluqueroService;
import com.peluqueria.service.ServicioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private PeluqueroService peluqueroService;

    @Autowired
    private CitaService citaService;

    @Autowired
    private PDFService pdfService;

    @GetMapping("/agendar")
    public String mostrarFormularioAgendamiento(Model model, HttpSession session) {
        // Limpiar sesión al iniciar nuevo proceso
        session.removeAttribute("clienteTemp");
        session.removeAttribute("serviciosSeleccionados");
        session.removeAttribute("peluqueroSeleccionado");
        session.removeAttribute("fechaHoraSeleccionada");

        // Obtener cliente autenticado desde el contexto de seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Cliente cliente = userDetails.getCliente();

        session.setAttribute("clienteTemp", cliente);
        model.addAttribute("cliente", cliente);

        return "agendar-paso1";
    }

    @PostMapping("/paso1")
    public String procesarPaso1(@Valid @ModelAttribute Cliente cliente,
                                 BindingResult result,
                                 HttpSession session,
                                 Model model) {
        if (result.hasErrors()) {
            return "agendar-paso1";
        }

        // Guardar cliente en sesión
        session.setAttribute("clienteTemp", cliente);

        // Cargar servicios para el paso 2
        model.addAttribute("servicios", servicioService.obtenerServiciosActivos());
        return "agendar-paso2";
    }

    @PostMapping("/paso2")
    public String procesarPaso2(@RequestParam List<Long> serviciosIds,
                                 HttpSession session,
                                 Model model) {
        if (serviciosIds == null || serviciosIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un servicio");
            model.addAttribute("servicios", servicioService.obtenerServiciosActivos());
            return "agendar-paso2";
        }

        List<Servicio> serviciosSeleccionados = new ArrayList<>();
        for (Long id : serviciosIds) {
            servicioService.buscarPorId(id).ifPresent(serviciosSeleccionados::add);
        }

        session.setAttribute("serviciosSeleccionados", serviciosSeleccionados);

        // Cargar peluqueros para el paso 3
        model.addAttribute("peluqueros", peluqueroService.obtenerPeluquerosActivos());
        return "agendar-paso3";
    }

    @PostMapping("/paso3")
    public String procesarPaso3(@RequestParam Long peluqueroId,
                                 HttpSession session,
                                 Model model) {
        Peluquero peluquero = peluqueroService.buscarPorId(peluqueroId)
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));

        session.setAttribute("peluqueroSeleccionado", peluquero);

        return "agendar-paso4";
    }

    @PostMapping("/paso4")
    public String procesarPaso4(@RequestParam String fecha,
                                 @RequestParam String hora,
                                 HttpSession session,
                                 Model model) {
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora + ":00");
            session.setAttribute("fechaHoraSeleccionada", fechaHora);

            // Mostrar resumen
            Cliente cliente = (Cliente) session.getAttribute("clienteTemp");
            @SuppressWarnings("unchecked")
            List<Servicio> servicios = (List<Servicio>) session.getAttribute("serviciosSeleccionados");
            Peluquero peluquero = (Peluquero) session.getAttribute("peluqueroSeleccionado");

            model.addAttribute("cliente", cliente);
            model.addAttribute("servicios", servicios);
            model.addAttribute("peluquero", peluquero);
            model.addAttribute("fechaHora", fechaHora);

            return "agendar-paso5";
        } catch (Exception e) {
            model.addAttribute("error", "Fecha y hora inválidas");
            return "agendar-paso4";
        }
    }

    @PostMapping("/confirmar")
    public String confirmarCita(HttpSession session, Model model) {
        try {
            Cliente clienteTemp = (Cliente) session.getAttribute("clienteTemp");
            @SuppressWarnings("unchecked")
            List<Servicio> servicios = (List<Servicio>) session.getAttribute("serviciosSeleccionados");
            Peluquero peluquero = (Peluquero) session.getAttribute("peluqueroSeleccionado");
            LocalDateTime fechaHora = (LocalDateTime) session.getAttribute("fechaHoraSeleccionada");

            // Registrar cliente si no existe
            Cliente cliente;
            if (clienteService.buscarPorEmail(clienteTemp.getEmail()).isPresent()) {
                cliente = clienteService.buscarPorEmail(clienteTemp.getEmail()).get();
            } else {
                // Generar password temporal
                clienteTemp.setPassword("temp123");
                cliente = clienteService.registrarCliente(clienteTemp);
            }

            // Crear la cita
            Cita cita = citaService.crearCita(cliente, peluquero, servicios, fechaHora);

            // Limpiar sesión
            session.removeAttribute("clienteTemp");
            session.removeAttribute("serviciosSeleccionados");
            session.removeAttribute("peluqueroSeleccionado");
            session.removeAttribute("fechaHoraSeleccionada");

            model.addAttribute("cita", cita);
            return "cita-confirmada";

        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la cita: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/comprobante/{codigoCita}")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable String codigoCita) {
        try {
            Cita cita = citaService.buscarPorCodigo(codigoCita)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            byte[] pdfBytes = pdfService.generarComprobantePDF(cita);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "comprobante-" + codigoCita + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
