package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.services.DoctorService;
import com.vaadin.data.Binder;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.hsqldb.HsqlException;

import java.sql.SQLException;
import java.util.List;

public class DoctorView {
    private static Grid<Doctor> gridDoctor;

    private static Window modalWindow;

    private static List<Doctor> doctors;
    private static DoctorService doctorService;
    private static Doctor selectedDoctor;

    public static void initAll(MainView mainView, VerticalLayout layout) {
        gridDoctor = new Grid<>();
        gridDoctor.setWidthFull();
        gridDoctor.addItemClickListener(event -> selectedDoctor = event.getItem());
        gridDoctor.setHeight("600px");
        HorizontalLayout btnsLayout = new HorizontalLayout();
        doctors = null;
        selectedDoctor = null;

        Button btnAddDoctor = new Button("Добавить");
        btnAddDoctor.setStyleName("friendly");

        Button btnEditDoctor = new Button("Изменить");
        btnEditDoctor.setStyleName("primary");


        Button btnDelDoctor = new Button("Удалить");
        btnDelDoctor.setStyleName("danger");

        doctorService = new DoctorService();
        btnsLayout.addComponents(btnAddDoctor, btnEditDoctor, btnDelDoctor);

        //Events
        btnAddDoctor.addClickListener(event -> openModalAddDoctor(mainView));
        btnEditDoctor.addClickListener(event -> openModalEditDoctor(mainView));
        btnDelDoctor.addClickListener(event -> openModalDeleteDoctor(mainView));

        fillDoctorsTable();
        layout.addComponents(btnsLayout, gridDoctor);
    }
    private static void fillDoctorsTable() {
        doctors = doctorService.findAllDoctors();
        gridDoctor.setItems(doctors);
        gridDoctor.addColumn(Doctor::getFirstName).setCaption("Имя");
        gridDoctor.addColumn(Doctor::getSecondName).setCaption("Фамилия");
        gridDoctor.addColumn(Doctor::getMiddleName).setCaption("Отчество");
        gridDoctor.addColumn(Doctor::getSpecialization).setCaption("Специализация");
    }
    private static void updateDoctorsTable() {
        doctors = doctorService.findAllDoctors();
        gridDoctor.setItems(doctors);
    }
    private static void openModalAddDoctor(MainView mainView){
        modalWindow = new Window("Добавление врача");

        modalWindow.setModal(true);

        TextField firstNameTextField = new TextField("Имя");
        firstNameTextField.setStyleName("modalElem");
        firstNameTextField.setWidth("320px");
        TextField secondNameTextField = new TextField("Фамилия");
        secondNameTextField.setStyleName("modalElem");
        secondNameTextField.setWidth("320px");
        TextField middleNameTextField = new TextField("Отчество");
        middleNameTextField.setStyleName("modalElem");
        middleNameTextField.setWidth("320px");
        TextField specTextField = new TextField("Специализация");
        specTextField.setStyleName("modalElem");
        specTextField.setWidth("320px");
        Label errorMessage = new Label();

        Binder<Doctor> binder = new Binder<>();
        binder.forField(firstNameTextField).asRequired()
                .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна имени.")
                .withStatusLabel(errorMessage)
                .bind(Doctor::getFirstName, Doctor::setFirstName);
        binder.forField(secondNameTextField).asRequired()
                .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна фамилии.")
                .withStatusLabel(errorMessage)
                .bind(Doctor::getSecondName, Doctor::setSecondName);
        binder.forField(specTextField).asRequired()
                .withValidator(name ->  name.length() >= 2 && name.length() <=50 ,"Некоректная длинна специализации.")
                .withStatusLabel(errorMessage)
                .bind(Doctor::getSpecialization, Doctor::setSpecialization);

        Button btnOK = new Button("Добавить");
        btnOK.setStyleName("friendly");
        btnOK.addClickListener(event -> {
            if(binder.validate().isOk()) {
                addDoctor(firstNameTextField.getValue(),
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
            new Notification(null, "Добавление доктора отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        });

        VerticalLayout modalWindowLayout = new VerticalLayout();
        HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
        buttonModalBar.setWidth("350px");
        modalWindow.setContent(modalWindowLayout);
        modalWindowLayout.addComponents(
                firstNameTextField,
                secondNameTextField,
                middleNameTextField,
                specTextField,
                buttonModalBar,
                errorMessage);
        modalWindow.center();
        modalWindowLayout.setWidth("350px");
        mainView.addWindow(modalWindow);


    }
    private static void addDoctor(String firstName, String secondName, String middleName, String specialization){
        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setSecondName(secondName);
        doctor.setMiddleName(middleName);
        doctor.setSpecialization(specialization);
        doctorService.saveDoctor(doctor);
        new Notification(null, "Доктор добавлен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updateDoctorsTable();
    }
    private static void openModalEditDoctor(MainView mainView){
        if(selectedDoctor!=null){
            modalWindow = new Window("Изменение врача");

            modalWindow.setModal(true);

            TextField firstNameTextField = new TextField("Имя");
            firstNameTextField.setStyleName("modalElem");
            firstNameTextField.setWidth("320px");
            firstNameTextField.setValue(selectedDoctor.getFirstName());

            TextField secondNameTextField = new TextField("Фамилия");
            secondNameTextField.setStyleName("modalElem");
            secondNameTextField.setWidth("320px");
            secondNameTextField.setValue(selectedDoctor.getSecondName());

            TextField middleNameTextField = new TextField("Отчество");
            middleNameTextField.setStyleName("modalElem");
            middleNameTextField.setWidth("320px");
            middleNameTextField.setValue(selectedDoctor.getMiddleName());


            TextField specTextField = new TextField("Специализация");
            specTextField.setStyleName("modalElem");
            specTextField.setWidth("320px");
            specTextField.setValue(selectedDoctor.getSpecialization());
            Label errorMessage = new Label();


            Binder<Doctor> binder = new Binder<>();
            binder.forField(firstNameTextField).asRequired()
                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна имени.")
                    .withStatusLabel(errorMessage)
                    .bind(Doctor::getFirstName, Doctor::setFirstName);
            binder.forField(secondNameTextField).asRequired()
                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна фамилии.")
                    .withStatusLabel(errorMessage)
                    .bind(Doctor::getSecondName, Doctor::setSecondName);
            binder.forField(middleNameTextField).asRequired()
                    .withValidator(name -> name.length() <=50,"Некоректная длинна отчества.")
                    .withStatusLabel(errorMessage)
                    .bind(Doctor::getMiddleName, Doctor::setMiddleName);
            binder.forField(specTextField).asRequired()
                    .withValidator(name ->  name.length() >= 2 && name.length() <=50 ,"Некоректная длинна специализации.")
                    .withStatusLabel(errorMessage)
                    .bind(Doctor::getSpecialization, Doctor::setSpecialization);

            Button btnOK = new Button("Изменить");
            btnOK.setStyleName("friendly");
            btnOK.addClickListener(event -> {
                if(binder.validate().isOk()) {
                    editDoctor(
                            selectedDoctor.getDoctortId(),
                            firstNameTextField.getValue(),
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
                new Notification(null, "Изменение доктора отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            });

            VerticalLayout modalWindowLayout = new VerticalLayout();
            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
            buttonModalBar.setWidth("350px");
            modalWindow.setContent(modalWindowLayout);
            modalWindowLayout.addComponents(
                    firstNameTextField,
                    secondNameTextField,
                    middleNameTextField,
                    specTextField,
                    buttonModalBar,
                    errorMessage);
            modalWindow.center();
            modalWindowLayout.setWidth("350px");
            mainView.addWindow(modalWindow);


        }
        else
            new Notification(null, "Выберите доктора", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());

    }
    private static void editDoctor(Long id, String firstName, String secondName, String middleName, String specialization){

        Doctor doctor = doctorService.findDoctor(id);
        doctor.setFirstName(firstName);
        doctor.setSecondName(secondName);
        doctor.setMiddleName(middleName);
        doctor.setSpecialization(specialization);
        doctorService.updateDoctor(doctor);
        new Notification(null, "Доктор изменен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updateDoctorsTable();
        selectedDoctor = null;
    }
    private static void openModalDeleteDoctor(MainView mainView){
        if(selectedDoctor!=null){
            modalWindow = new Window("Удаление врача");
            modalWindow.setModal(true);

            String middleName;
            if (selectedDoctor.getMiddleName()=="")
                middleName = selectedDoctor.getMiddleName();
            else
                middleName = new String("<Отсутствует>");


            Label label = new Label(
                    "Вы уверены что хотите удалить врача: \n" +
                            "<ul>"+
                            "  <li> Имя: "+ selectedDoctor.getFirstName() +" </li> "+
                            "  <li> Фамилия: "+ selectedDoctor.getSecondName() +" </li> "+
                            "  <li> Отчество: "+ middleName +" </li> "+
                            "  <li> Специализация: "+ selectedDoctor.getSpecialization() +" </li> "+
                            "</ul> ",
                    ContentMode.HTML);

            Button btnOK = new Button("Удалить");
            btnOK.setStyleName("friendly");
            btnOK.addClickListener(event -> {
                modalWindow.close();
                deleteDoctor();
            });

            Button btnCancel = new Button("Отменить");
            btnCancel.setStyleName("danger");
            btnCancel.addClickListener(e -> {
                modalWindow.close();
                new Notification(null, "Удаление доктора отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            });
            VerticalLayout modalWindowLayout = new VerticalLayout();
            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
            buttonModalBar.setWidth("500px");
            modalWindow.setContent(modalWindowLayout);
            modalWindowLayout.addComponents(
                    label,
                    buttonModalBar);
            modalWindow.center();
            modalWindowLayout.setWidth("500px");
            mainView.addWindow(modalWindow);

        }
        else
            new Notification(null, "Выберите доктора", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
    }
    private static void deleteDoctor(){

        try {
            doctorService.deleteDoctor(selectedDoctor);
            new Notification(null, "Доктор удален", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
            updateDoctorsTable();
            selectedDoctor = null;
        } catch (Exception e) {
            if(e.getCause() instanceof ConstraintViolationException)
                new Notification(null, "У докотора уже есть рецепты!", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
            else
                new Notification(null, "Произошел взлом жопы", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        }

    }
}
