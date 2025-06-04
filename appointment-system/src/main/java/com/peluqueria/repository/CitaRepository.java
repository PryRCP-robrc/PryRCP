package com.peluqueria.repository;

import com.peluqueria.entity.Cita;
import com.peluqueria.entity.Peluquero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    Optional<Cita> findByCodigoUnico(String codigoUnico);
    
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.servicio WHERE c.peluquero = :peluquero AND c.fechaHora BETWEEN :inicio AND :fin AND c.estado != 'CANCELADA'")
    List<Cita> findByPeluqueroAndFechaBetween(@Param("peluquero") Peluquero peluquero,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.cliente LEFT JOIN FETCH c.peluquero LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.servicio ORDER BY c.fechaCreacion DESC")
    List<Cita> findAllOrderByFechaCreacionDesc();
    
    @Query("SELECT c FROM Cita c WHERE c.fechaHora >= :fecha ORDER BY c.fechaHora ASC")
    List<Cita> findCitasFuturas(@Param("fecha") LocalDateTime fecha);
}