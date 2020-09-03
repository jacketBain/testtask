package com.haulmont.testtask.entity;


import javax.persistence.*;

@SequenceGenerator(name = "SEQ_PATIENT",sequenceName = "SEQ_PATIENT")
@Entity
@Table(name = "Patient")
public class Patient {
    @Id
    @GeneratedValue(generator = "SEQ_PATIENT")
    @Column(name = "id_patient",unique = true, nullable = false)
    private Long patientId;

    @Column(name = "firstname", nullable = false)
    private String firstName;

    @Column(name = "lastname", nullable = false)
    private String secondName;

    @Column(name = "middlename", nullable = false)
    private String middleName;

    @Column(name = "phonenumber",unique = true, nullable = false)
    private String phoneNumber;

    public Patient() {
    }

    public Patient(String firstName, String secondName, String middleName, String phoneNumber) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.middleName = middleName;
        this.phoneNumber = phoneNumber;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
