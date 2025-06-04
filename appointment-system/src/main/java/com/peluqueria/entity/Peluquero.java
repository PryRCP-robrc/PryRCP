package com.peluqueria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "peluqueros")
public class Peluquero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;
    
    @Column(columnDefinition = "TEXT")
    private String especialidad;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "peluquero", cascade = CascadeType.ALL)
    private Set<Cita> citas;
    
    // Constructors
    public Peluquero() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Set<Cita> getCitas() { return citas; }
    public void setCitas(Set<Cita> citas) { this.citas = citas; }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}