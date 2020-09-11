package com.haulmont.testtask.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@SequenceGenerator(name = "SEQ_DOCTOR",sequenceName = "SEQ_DOCTOR")
@Entity
@Table(name = "DOCTOR")
public class Doctor {
    @Id
    @GeneratedValue(generator = "SEQ_DOCTOR")
    @Column(name = "ID_DOCTOR",unique = true, nullable = false)
    private Long doctortId;

    @Column(name = "FIRSTNAME", nullable = false, length = 50)
    private String firstName;

    @Column(name = "SECONDNAME", nullable = false, length = 50)
    private String secondName;

    @Column(name = "MIDDLENAME", length = 50)
    private String middleName;

    @Column(name = "SPECIALIZATION", nullable = false, length = 50)
    private String specialization;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "doctorId")
    private List<Prescription> prescriptionList = new LinkedList<Prescription>();

    public Long getDoctortId() {
        return doctortId;
    }

    public void setDoctortId(Long doctortId) {
        this.doctortId = doctortId;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<Prescription> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    public String getFullName(){
        if(!middleName.equals(""))
            return secondName+" "+firstName.charAt(0)+"."+middleName.charAt(0)+".";
        else
            return secondName+" "+firstName.charAt(0)+".";
    }
    public int getCountOfPrescriptions(){
        return prescriptionList.size();
    }
}
