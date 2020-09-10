package com.haulmont.testtask.services;

import com.haulmont.testtask.dao.impl.PatientDAO;
import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.entity.Patient;

import java.util.List;

public class PatientService {

    private PatientDAO patientDAO = new PatientDAO();

    public PatientService() {
    }

    public Patient findPatient(Long id) {
        return patientDAO.findById(id);
    }

    public void savePatient(Patient patient){
        patientDAO.save(patient);
    }

    public void deletePatient(Patient patient) {
        patientDAO.delete(patient);
    }

    public void updatePatient(Patient patient) {
        patientDAO.update(patient);
    }

    public List<Patient> findAllPatients() {
        return patientDAO.findAll();
    }
}