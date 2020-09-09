package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.services.DoctorService;
import com.vaadin.data.Binder;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.util.List;

public class DoctorView {
    private static Grid<Doctor> gridDoctor;

    private static Button btnAddDoctor;
    private static Button btnEditDoctor;
    private static Button btnDelDoctor;

    private static HorizontalLayout btnsLayout;

    private static List<Doctor> doctors;
    private static DoctorService doctorService;
    private static Doctor selectedDoctor;

    public static void initAll(MainView mainView, VerticalLayout layout) {
        gridDoctor = new Grid<>();
        gridDoctor.setWidthFull();
        gridDoctor.addItemClickListener(event -> {
            selectedDoctor = event.getItem();
        });
        gridDoctor.setHeight("600px");
        btnsLayout = new HorizontalLayout();
        doctors = null;
        selectedDoctor = null;

        btnAddDoctor = new Button("Добавить");
        btnAddDoctor.setStyleName("primary");

        btnEditDoctor = new Button("Изменить");
        btnEditDoctor.setStyleName("friendly");

        btnDelDoctor = new Button("Удалить");
        btnDelDoctor.setStyleName("danger");

        doctorService = new DoctorService();
        btnsLayout.addComponents(btnAddDoctor,btnDelDoctor,btnEditDoctor);

        //Events

        btnAddDoctor.addClickListener(event -> {
            btnAddDoctorClick(mainView);
        });

        fillDoctorsTable();
        layout.addComponents(btnsLayout, gridDoctor);
    }
    private static void fillDoctorsTable()
    {
        doctors = doctorService.findAllDoctors();
        gridDoctor.setItems(doctors);
        gridDoctor.addColumn(Doctor::getFirstName).setCaption("Имя");
        gridDoctor.addColumn(Doctor::getSecondName).setCaption("Фамилия");
        gridDoctor.addColumn(Doctor::getMiddleName).setCaption("Отчество");
        gridDoctor.addColumn(Doctor::getSpecialization).setCaption("Специализация");
    }
    private static void btnAddDoctorClick(MainView mainView){
        Window modalWindow = new Window("Добавление врача");
        modalWindow.setModal(true);

        TextField firstNameTextField = new TextField("Имя");
        firstNameTextField.setStyleName("modalElem");
        TextField secondNameTextField = new TextField("Фамилия");
        secondNameTextField.setStyleName("modalElem");
        TextField middleNameTextField = new TextField("Отчество");
        middleNameTextField.setStyleName("modalElem");
        TextField specTextField = new TextField("Специализация");
        specTextField.setStyleName("modalElem");

        Binder<Doctor> binder = new Binder<>();
        binder.forField(firstNameTextField).asRequired()
                .withValidator(name -> name.length() >= 1 && name.length() <=255,"Некоректная длинна имени.")
                .bind(Doctor::getFirstName, Doctor::setFirstName);
        binder.forField(secondNameTextField).asRequired()
                .withValidator(name -> name.length() >= 1 && name.length() <=255,"Некоректная длинна фамилии.")
                .bind(Doctor::getSecondName, Doctor::setSecondName);
        binder.forField(middleNameTextField).asRequired()
                .withValidator(name -> name.length() ==0 || name.length() >= 1 && name.length() <=255,"Некоректная длинна отчества.")
                .bind(Doctor::getMiddleName, Doctor::setMiddleName);
        binder.forField(specTextField).asRequired()
                .withValidator(name ->  name.length() >= 2 && name.length() <=50 ,"Некоректная длинна специализации.")
                .bind(Doctor::getSpecialization, Doctor::setSpecialization);

        Button btnOK = new Button("ОК");
        btnOK.setStyleName("friendly");
        btnOK.addClickListener(event -> {
            if(binder.validate().isOk()) {
                btnAddDoctorModalOkClick(firstNameTextField.getValue(),
                                    secondNameTextField.getValue(),
                                    middleNameTextField.getValue(),
                                    specTextField.getValue());
                modalWindow.close();
            }
        });

        Button btnCancel = new Button("Отменить");
        btnCancel.setStyleName("danger");
        btnCancel.addClickListener(e -> {
            modalWindow.close();
        });

        VerticalLayout modalWindowLayout = new VerticalLayout();
        HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
        modalWindow.setContent(modalWindowLayout);
        modalWindowLayout.addComponents(
                firstNameTextField,
                secondNameTextField,
                middleNameTextField,
                specTextField,
                buttonModalBar);
        modalWindow.center();
        modalWindowLayout.setWidth("500px");
        mainView.addWindow(modalWindow);

    }
    private static void btnAddDoctorModalOkClick(String firstName, String secondName, String middleName, String specialization){

        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setSecondName(secondName);
        doctor.setMiddleName(middleName);
        doctor.setSpecialization(specialization);
        doctorService.saveDoctor(doctor);
        new Notification(null, "Доктор добавлен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }
}
