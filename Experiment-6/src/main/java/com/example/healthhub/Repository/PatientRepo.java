package com.example.healthhub.Repository;
import com.example.healthhub.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  PatientRepo extends JpaRepository<Patient, Long> {
    
}
