package com.peluqueria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;
    
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false)
    private String telefono;
    
    @Column(nullable = false)
    private String password;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Set<Cita> citas;
    
    // Constructors
    public Cliente() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    public Set<Cita> getCitas() { return citas; }
    public void setCitas(Set<Cita> citas) { this.citas = citas; }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}