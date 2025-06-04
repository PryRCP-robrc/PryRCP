package com.peluqueria.service;

import com.peluqueria.entity.*;
import com.peluqueria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DetalleCitaRepository detalleCitaRepository;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    @Autowired
    private PeluqueroRepository peluqueroRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<String> obtenerHorariosDisponibles(Long peluqueroId, String fecha, List<Long> serviciosIds) {
        Peluquero peluquero = peluqueroRepository.findById(peluqueroId)
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));

        LocalDateTime fechaInicio = LocalDateTime.parse(fecha + "T08:00:00");
        LocalDateTime fechaFin = LocalDateTime.parse(fecha + "T18:00:00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Calcular duración total
        int duracionTotal = 30;
        if (serviciosIds != null && !serviciosIds.isEmpty()) {
            List<Servicio> servicios = servicioRepository.findAllById(serviciosIds);
            duracionTotal = servicios.stream().mapToInt(Servicio::getDuracionMinutos).sum();
        }

        // Obtener todas las citas del peluquero en el rango de tiempo
        List<Cita> citas = citaRepository.findByPeluqueroAndFechaBetween(peluquero, fechaInicio, fechaFin);

        // Generar horarios posibles
        List<String> horariosDisponibles = new ArrayList<>();
        LocalDateTime cursor = fechaInicio;

        while (!cursor.plusMinutes(duracionTotal).isAfter(fechaFin)) {
            LocalDateTime bloqueInicio = cursor;
            LocalDateTime bloqueFin = cursor.plusMinutes(duracionTotal);

            boolean solapa = false;
            for (Cita cita : citas) {
                LocalDateTime citaInicio = cita.getFechaHora();
                LocalDateTime citaFin = citaInicio.plusMinutes(cita.getDuracionTotalMinutos());

                // Si se solapan los rangos, este bloque no está disponible
                if (!(bloqueFin.isEqual(citaInicio) || bloqueFin.isBefore(citaInicio) ||
                        bloqueInicio.isEqual(citaFin) || bloqueInicio.isAfter(citaFin))) {
                    solapa = true;
                    break;
                }
            }

            if (!solapa && (cursor.getMinute() == 0 || cursor.getMinute() == 60)) {
                horariosDisponibles.add(cursor.format(formatter));
            }

            cursor = cursor.plusMinutes(1);
        }

        return horariosDisponibles;
    }

    public Cita crearCita(Cliente cliente, Peluquero peluquero, List<Servicio> servicios,
            LocalDateTime fechaHora) {

        int duracionTotal = servicios.stream().mapToInt(Servicio::getDuracionMinutos).sum();

        // Validar que no haya solapamiento antes de crear la cita
        if (!esBloqueDisponible(peluquero, fechaHora, duracionTotal)) {
            throw new RuntimeException("El horario seleccionado se solapa con otra cita.");
        }

        Cita cita = new Cita();
        cita.setCodigoUnico(generarCodigoUnico());
        cita.setCliente(cliente);
        cita.setPeluquero(peluquero);
        cita.setFechaHora(fechaHora);
        cita.setEstado(Cita.EstadoCita.PROGRAMADA);

        cita = citaRepository.save(cita);

        // Crear detalles de la cita
        Set<DetalleCita> detalles = new HashSet<>();
        BigDecimal totalCita = BigDecimal.ZERO;

        for (Servicio servicio : servicios) {
            DetalleCita detalle = new DetalleCita();
            detalle.setCita(cita);
            detalle.setServicio(servicio);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(servicio.getPrecio());
            detalle.setSubtotal(servicio.getPrecio());

            detalle = detalleCitaRepository.save(detalle);
            detalles.add(detalle);

            totalCita = totalCita.add(servicio.getPrecio());
        }

        cita.setDetalles(detalles);
        cita.setDuracionTotalMinutos(duracionTotal);

        // Crear boleta
        Boleta boleta = crearBoleta(cita, totalCita);
        cita.setBoleta(boleta);

        return citaRepository.save(cita);
    }

    private boolean esBloqueDisponible(Peluquero peluquero, LocalDateTime inicio, int duracion) {
        LocalDateTime fin = inicio.plusMinutes(duracion);

        List<Cita> citas = citaRepository.findByPeluqueroAndFechaBetween(
                peluquero,
                inicio.toLocalDate().atStartOfDay(),
                fin.toLocalDate().atTime(23, 59));

        for (Cita existente : citas) {
            LocalDateTime citaInicio = existente.getFechaHora();
            LocalDateTime citaFin = citaInicio.plusMinutes(existente.getDuracionTotalMinutos());

            // Validación de solapamiento
            if (!(fin.isBefore(citaInicio) || inicio.isAfter(citaFin))) {
                return false;
            }
        }

        return true;
    }

    private Boleta crearBoleta(Cita cita, BigDecimal subtotal) {
        Boleta boleta = new Boleta();
        boleta.setNumero(generarNumeroBoleta());
        boleta.setCita(cita);
        boleta.setSubtotal(subtotal);

        // Calcular IGV (18%)
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));
        boleta.setIgv(igv);
        boleta.setTotal(subtotal.add(igv));

        boleta = boletaRepository.save(boleta);

        // Crear detalles de boleta
        Set<DetalleBoleta> detallesBoleta = new HashSet<>();
        for (DetalleCita detalleCita : cita.getDetalles()) {
            DetalleBoleta detalleBoleta = new DetalleBoleta();
            detalleBoleta.setBoleta(boleta);
            detalleBoleta.setDescripcion(detalleCita.getServicio().getNombre());
            detalleBoleta.setCantidad(detalleCita.getCantidad());
            detalleBoleta.setPrecioUnitario(detalleCita.getPrecioUnitario());
            detalleBoleta.setSubtotal(detalleCita.getSubtotal());

            detalleBoletaRepository.save(detalleBoleta);
            detallesBoleta.add(detalleBoleta);
        }

        boleta.setDetalles(detallesBoleta);
        return boleta;
    }

    public Optional<Cita> buscarPorCodigo(String codigo) {
        return citaRepository.findByCodigoUnico(codigo);
    }

    public List<Cita> obtenerTodasLasCitas() {
        return citaRepository.findAllOrderByFechaCreacionDesc();
    }

    public Cita cambiarEstado(Long citaId, Cita.EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    private String generarCodigoUnico() {
        return "CITA-" + System.currentTimeMillis() + "-" +
                (int) (Math.random() * 1000);
    }

    private String generarNumeroBoleta() {
        return "B" + String.format("%08d", System.currentTimeMillis() % 100000000);
    }
}