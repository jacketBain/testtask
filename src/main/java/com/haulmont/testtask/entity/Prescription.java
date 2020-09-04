package com.haulmont.testtask.entity;

import javax.persistence.*;
import java.util.Date;

@SequenceGenerator(name = "SEQ_PRESCRIPTION",sequenceName = "SEQ_PRESCRIPTION")
@Entity
@Table(name = "PRESCRIPTION")
public class Prescription {
    @Id
    @GeneratedValue(generator = "SEQ_PRESCRIPTION")
    @Column(name = "ID_PRESCRIPTION",unique = true, nullable = false)
    private Long prescriptionId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_DOCTOR")
    private Doctor doctorId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn (name = "ID_PATIENT")
    private Patient patientId;

    @Column(name = "DATE", nullable = false)
    private Date firstName;

    @Column(name = "DURATION", nullable = false)
    private Long duration;

    @Column(name = "PRIORITY", nullable = false)
    private Integer priority;

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Doctor getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Doctor doctorId) {
        this.doctorId = doctorId;
    }

    public Patient getPatientId() {
        return patientId;
    }

    public void setPatientId(Patient patientId) {
        this.patientId = patientId;
    }

    public Date getFirstName() {
        return firstName;
    }

    public void setFirstName(Date firstName) {
        this.firstName = firstName;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
