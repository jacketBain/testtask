package com.haulmont.testtask.entity;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

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
    private Date dateStart;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "DURATION", nullable = false)
    private Long duration;

    @Column(name = "PRIORITY", nullable = false)
    private String priority;

    @Transient
    private SimpleDateFormat normalDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Doctor getDoctorId() {
        return doctorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Date getDateStart() {
        return dateStart;
    }

    public LocalDate getDateStartInLocalDate() {
        return dateStart.toLocalDate();
    }

    public String getDateStartToString() {
        return normalDateFormat.format(dateStart);
    }


    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateStartInLocal(LocalDate dateStart) {
        this.dateStart = Date.valueOf(dateStart);
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFullNameDoctor(){
        if(doctorId.getMiddleName()!=null)
            return doctorId.getSecondName()+" "+doctorId.getFirstName().charAt(0)+"."+doctorId.getMiddleName().charAt(0)+".";
        else
            return doctorId.getSecondName()+" "+doctorId.getFirstName().charAt(0)+".";
    }
    public String getFullNamePatient(){
        if(patientId.getMiddleName()!=null)
            return patientId.getSecondName()+" "+patientId.getFirstName().charAt(0)+"."+patientId.getMiddleName().charAt(0)+".";
        else
            return patientId.getSecondName()+" "+patientId.getFirstName().charAt(0)+".";
    }
    public LocalDate getDateStop(){
        return dateStart.toLocalDate().plusDays(duration);
    }
    public String getDateStopToString(){
        return normalDateFormat.format(Date.valueOf(this.getDateStop()));
    }
}
