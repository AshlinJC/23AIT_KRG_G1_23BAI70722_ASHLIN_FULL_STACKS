package com.example.healthhub.Service;
import com.example.healthhub.Repository.PatientRepo;
import org.springframework.stereotype.Service;
import com.example.healthhub.DTO.PatientDTO;
import com.example.healthhub.Entity.Patient;

@Service
public class PatientServices {
    
    public final PatientRepo patientRepo;

    public PatientServices(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public Patient addPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setName(patientDTO.getName());
        patient.setAge(patientDTO.getAge());
        patient.setGender(patientDTO.getGender());
        patient.setMedicalHistory(patientDTO.getMedicalHistory());

        return patientRepo.save(patient);
        
    }

    public Patient getPatient(Long id) {
        return patientRepo.findById(id).orElse(null);
    }

    public Patient updatePatient(Long id, PatientDTO patientDTO) {
        Patient patient = patientRepo.findById(id).orElse(null);
        if (patient != null) {
            patient.setName(patientDTO.getName());
            patient.setAge(patientDTO.getAge());
            patient.setGender(patientDTO.getGender());
            patient.setMedicalHistory(patientDTO.getMedicalHistory());
            return patientRepo.save(patient);
        }
        return null;
    }

    public void deletePatient(Long id) {
        patientRepo.deleteById(id);
    }


}
