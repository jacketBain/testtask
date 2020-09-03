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

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "MIDDLENAME", nullable = false)
    private String secondName;

    @Column(name = "LASTNAME", nullable = false)
    private String middleName;

    @Column(name = "SPECIALIZATION", nullable = false)
    private String specialization;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctorId")
    private List<Prescription> prescriptionList = new LinkedList<Prescription>();

}
