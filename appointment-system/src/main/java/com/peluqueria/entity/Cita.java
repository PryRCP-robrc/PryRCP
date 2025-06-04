package com.peluqueria.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codigoUnico;
    
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado = EstadoCita.PROGRAMADA;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
        // NUEVO CAMPO
    private int duracionTotalMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peluquero_id", nullable = false)
    private Peluquero peluquero;
    
    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DetalleCita> detalles;
    
    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL)
    private Boleta boleta;
    
    public enum EstadoCita {
        PROGRAMADA, TERMINADA, CANCELADA
    }
    
    // Constructors
    public Cita() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public Peluquero getPeluquero() { return peluquero; }
    public void setPeluquero(Peluquero peluquero) { this.peluquero = peluquero; }
    
    public Set<DetalleCita> getDetalles() { return detalles; }
    public void setDetalles(Set<DetalleCita> detalles) { this.detalles = detalles; }
    
    public Boleta getBoleta() { return boleta; }
    public void setBoleta(Boleta boleta) { this.boleta = boleta; }

    public int getDuracionTotalMinutos() {
    return duracionTotalMinutos;
}

public void setDuracionTotalMinutos(int duracionTotalMinutos) {
    this.duracionTotalMinutos = duracionTotalMinutos;
}
}