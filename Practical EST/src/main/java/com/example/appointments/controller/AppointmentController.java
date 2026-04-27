package com.example.appointments.controller;

import com.example.appointments.entity.Appointment;
import com.example.appointments.repository.AppointmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public List<Appointment> search(@RequestParam("doctor") String doctor) {
        return repository.findByDoctorName(doctor);
    }
}