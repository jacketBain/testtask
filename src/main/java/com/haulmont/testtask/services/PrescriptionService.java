package com.haulmont.testtask.services;

import com.haulmont.testtask.dao.impl.PrescriptionDAO;
import com.haulmont.testtask.entity.Prescription;

import java.util.List;

public class PrescriptionService {
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    public PrescriptionService() {
    }

    public Prescription findPrescription(Long id) {
        return prescriptionDAO.findById(id);
    }


    public void deletePrescription(Prescription patient) {
        prescriptionDAO.delete(patient);
    }

    public void updatePrescription(Prescription patient) {
        prescriptionDAO.update(patient);
    }

    public List<Prescription> findAllPrescriptions() {
        return prescriptionDAO.findAll();
    }
}
