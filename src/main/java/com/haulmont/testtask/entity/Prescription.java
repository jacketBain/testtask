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

}
