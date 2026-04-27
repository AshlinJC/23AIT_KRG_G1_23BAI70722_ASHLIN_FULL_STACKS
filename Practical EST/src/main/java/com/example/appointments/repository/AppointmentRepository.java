package com.example.appointments.repository;

import com.example.appointments.entity.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctorName) LIKE LOWER(CONCAT('%', :doctor, '%'))")
    List<Appointment> findByDoctorName(@Param("doctor") String doctor);
}