package com.haulmont.testtask.entity;


import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@SequenceGenerator(name = "SEQ_PATIENT",sequenceName = "SEQ_PATIENT")
@Entity
@Table(name = "PATIENT")
public class Patient {
    @Id
    @GeneratedValue(generator = "SEQ_PATIENT")
    @Column(name = "ID_PATIENT",unique = true, nullable = false)
    private Long patientId;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "MIDDLENAME")
    private String secondName;

    @Column(name = "LASTNAME", nullable = false)
    private String middleName;

    @Column(name = "PHONENUMBER",unique = true, nullable = false)
    private String phoneNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patientId")
    private List<Prescription> prescriptionList = new LinkedList<Prescription>();

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

    public List<Prescription> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    public String getFullName(){
        if(middleName!=null)
            return secondName+" "+firstName.charAt(0)+"."+middleName.charAt(0)+".";
        else
            return secondName+" "+firstName.charAt(0)+".";
    }
}
