package com.peluqueria.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre;
    
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    private Set<Cliente> clientes;
    
    // Constructors
    public Rol() {}
    
    public Rol(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Set<Cliente> getClientes() { return clientes; }
    public void setClientes(Set<Cliente> clientes) { this.clientes = clientes; }
}