package com.haulmont.testtask.services;

import com.haulmont.testtask.dao.impl.DoctorDAO;
import com.haulmont.testtask.entity.Doctor;

import java.util.List;

public class DoctorService {

    private DoctorDAO doctorDAO = new DoctorDAO();

    public DoctorService() {
    }

    public Doctor findDoctor(Long id) {
        return doctorDAO.findById(id);
    }

    public void saveDoctor(Doctor doctor){
        doctorDAO.save(doctor);
    }

    public void deleteDoctor(Doctor doctor) {
        doctorDAO.delete(doctor);
    }

    public void updateDoctor(Doctor doctor) {
        doctorDAO.update(doctor);
    }

    public List<Doctor> findAllDoctors() {
        return doctorDAO.findAll();
    }
}
