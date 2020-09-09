package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.services.PatientService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class PatientView {
    private static Grid<Patient> grid;
    private static Button btnAddPatient;
    private static Button btnEditPatient;
    private static Button btnDelPatient;
    private static HorizontalLayout btnsLayout;

    private static List<Patient> patients;
    private static PatientService patientService;
    private static Patient selectedPatient;

    public static void initAll(MainView ui, VerticalLayout layout) {
        grid = new Grid<>();
        grid.setWidthFull();
        grid.addItemClickListener(e -> {
            selectedPatient = e.getItem();
        });
        grid.setHeight("600px");
        btnsLayout = new HorizontalLayout();
        patients = null;
        selectedPatient = null;
        btnAddPatient = new Button("Добавить");
        btnEditPatient = new Button("Изменить");
        btnDelPatient = new Button("Удалить");
        patientService = new PatientService();
        btnsLayout.addComponents(btnAddPatient,btnDelPatient,btnEditPatient);
        fillPatientsTable();
        layout.addComponents(btnsLayout, grid);
    }
    private static void fillPatientsTable(){
        patients = patientService.findAllPatients();
        grid.setItems(patients);
        grid.addColumn(Patient::getFirstName).setCaption("Имя");
        grid.addColumn(Patient::getMiddleName).setCaption("Фамилия");
        grid.addColumn(Patient::getSecondName).setCaption("Отчество");
        grid.addColumn(Patient::getPhoneNumber).setCaption("Телефон");
    }
}
