package com.example.healthhub.Controller;
import com.example.healthhub.Service.PatientServices;
import org.springframework.web.bind.annotation.*;
import com.example.healthhub.DTO.PatientDTO;
import com.example.healthhub.Entity.Patient;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/patients")
public class PatientController {
    
    public final PatientServices patientServices;
    public PatientController(PatientServices patientServices) {
        this.patientServices = patientServices;
    }

    @PostMapping("/add")
    public Patient addPatient(@Valid @RequestBody PatientDTO patientDTO ) {
        return patientServices.addPatient(patientDTO);
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Long id) {
        return patientServices.getPatient(id);
    }

    @PutMapping("/update/{id}")
    public Patient updatePatient(@PathVariable Long id , @Valid @RequestBody PatientDTO patientDTO) {
        return patientServices.updatePatient(id, patientDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientServices.deletePatient(id);
    }
}
