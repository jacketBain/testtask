package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.services.PatientService;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.Binder;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

@StyleSheet("style.css")
public class PatientView {
    private static Grid<Patient> gridPatient;

    private static Window modalWindow;

    private static List<Patient> patients;
    private static PatientService patientService;
    private static Patient selectedPatient;

    public static void initAll(MainView mainView, VerticalLayout layout) {
        gridPatient = new Grid<>();
        gridPatient.setWidthFull();
        gridPatient.addItemClickListener(event -> selectedPatient = event.getItem());
        gridPatient.setHeight("600px");
        HorizontalLayout btnsLayout = new HorizontalLayout();
        patients = null;
        selectedPatient = null;

        Button btnAddPatient = new Button("Добавить");
        btnAddPatient.setStyleName("friendly");

        Button btnEditPatient = new Button("Изменить");
        btnEditPatient.setStyleName("primary");


        Button btnDelPatient = new Button("Удалить");
        btnDelPatient.setStyleName("danger");

        patientService = new PatientService();
        btnsLayout.addComponents(btnAddPatient, btnEditPatient, btnDelPatient);

        //Events
        btnAddPatient.addClickListener(event -> openModalAddPatient(mainView));
        btnEditPatient.addClickListener(event -> openModalEditPatient(mainView));
        btnDelPatient.addClickListener(event -> openModalDeletePatient(mainView));

        fillPatientsTable();
        layout.addComponents(btnsLayout, gridPatient);
    }
    private static void fillPatientsTable() {
        patients = patientService.findAllPatients();
        gridPatient.setItems(patients);
        gridPatient.addColumn(Patient::getFirstName).setCaption("Имя");
        gridPatient.addColumn(Patient::getSecondName).setCaption("Фамилия");
        gridPatient.addColumn(Patient::getMiddleName).setCaption("Отчество");
        gridPatient.addColumn(Patient::getPhoneNumber).setCaption("Номер телефона");
    }
    private static void updatePatientsTable() {
        patients = patientService.findAllPatients();
        gridPatient.setItems(patients);
    }
    private static void openModalAddPatient(MainView mainView){
        modalWindow = new Window("Добавление пациента");

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
        Label phoneFormatLabel = new Label("Формат телефона +7xxxxxxxxxx");
        TextField phoneTextField = new TextField("Номер телефона");
        phoneTextField.setStyleName("modalElem");
        phoneTextField.setWidth("320px");
        Label errorMessage = new Label();


        Binder<Patient> binder = new Binder<>();
        binder.forField(firstNameTextField).asRequired()
                .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна имени.")
                .withStatusLabel(errorMessage)
                .bind(Patient::getFirstName, Patient::setFirstName);
        binder.forField(secondNameTextField).asRequired()
                .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна фамилии.")
                .withStatusLabel(errorMessage)
                .bind(Patient::getSecondName, Patient::setSecondName);
        binder.forField(phoneTextField).asRequired()
                .withValidator(name -> name.length() > 10 && name.substring(0, 2).equals("+7") && name.length() <= 12, "Неверный формат номера")
                .withStatusLabel(errorMessage)
                .bind(Patient::getPhoneNumber, Patient::setPhoneNumber);

        Button btnOK = new Button("Ок");
        btnOK.setStyleName("friendly");
        btnOK.addClickListener(event -> {
            if(binder.validate().isOk()) {
                addPatient(firstNameTextField.getValue(),
                        secondNameTextField.getValue(),
                        middleNameTextField.getValue(),
                        phoneTextField.getValue());
                modalWindow.close();
            }
        });

        Button btnCancel = new Button("Отменить");
        btnCancel.setStyleName("danger");
        btnCancel.addClickListener(e -> {
            modalWindow.close();
            new Notification(null, "Добавление пациента отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        });

        VerticalLayout modalWindowLayout = new VerticalLayout();
        HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
        buttonModalBar.setWidth("350px");
        modalWindow.setContent(modalWindowLayout);
        modalWindowLayout.addComponents(
                firstNameTextField,
                secondNameTextField,
                middleNameTextField,
                phoneTextField,
                phoneFormatLabel,
                buttonModalBar,
                errorMessage);
        modalWindow.center();

        modalWindowLayout.setComponentAlignment(firstNameTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(secondNameTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(middleNameTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(phoneFormatLabel,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(phoneTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(buttonModalBar,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(errorMessage,Alignment.TOP_CENTER);
        buttonModalBar.setComponentAlignment(btnOK,Alignment.TOP_LEFT);
        buttonModalBar.setComponentAlignment(btnCancel,Alignment.TOP_RIGHT);

        mainView.addWindow(modalWindow);


    }
    private static void addPatient(String firstName, String secondName, String middleName, String phoneNumber){
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setSecondName(secondName);
        patient.setMiddleName(middleName);
        patient.setPhoneNumber(phoneNumber);
        patientService.savePatient(patient);
        new Notification(null, "Пациент добавлен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updatePatientsTable();
    }
    private static void openModalEditPatient(MainView mainView){
        if(selectedPatient!=null){
            modalWindow = new Window("Изменение пациента");

            modalWindow.setModal(true);

            TextField firstNameTextField = new TextField("Имя");
            firstNameTextField.setStyleName("modalElem");
            firstNameTextField.setWidth("320px");
            firstNameTextField.setValue(selectedPatient.getFirstName());

            TextField secondNameTextField = new TextField("Фамилия");
            secondNameTextField.setStyleName("modalElem");
            secondNameTextField.setWidth("320px");
            secondNameTextField.setValue(selectedPatient.getSecondName());

            TextField middleNameTextField = new TextField("Отчество");
            middleNameTextField.setStyleName("modalElem");
            middleNameTextField.setWidth("320px");
            middleNameTextField.setValue(selectedPatient.getMiddleName());

            Label phoneFormatLabel = new Label("Формат телефона +7xxxxxxxxxx");
            TextField phoneTextField = new TextField("Телефон");
            phoneTextField.setStyleName("modalElem");
            phoneTextField.setValue(selectedPatient.getPhoneNumber());
            phoneTextField.setWidth("320px");
            Label errorMessage = new Label();


            Binder<Patient> binder = new Binder<>();
            binder.forField(firstNameTextField).asRequired()
                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна имени.")
                    .withStatusLabel(errorMessage)
                    .bind(Patient::getFirstName, Patient::setFirstName);
            binder.forField(secondNameTextField).asRequired()
                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна фамилии.")
                    .withStatusLabel(errorMessage)
                    .bind(Patient::getSecondName, Patient::setSecondName);
            binder.forField(phoneTextField).asRequired()
                    .withValidator(name -> name.length() > 10 && name.substring(0, 2).equals("+7") && name.length() <= 12, "Неверный формат номера")
                    .withStatusLabel(errorMessage)
                    .bind(Patient::getPhoneNumber, Patient::setPhoneNumber);

            Button btnOK = new Button("Ок");
            btnOK.setStyleName("friendly");
            btnOK.addClickListener(event -> {
                if(binder.validate().isOk()) {
                    editPatient(
                            selectedPatient.getPatientId(),
                            firstNameTextField.getValue(),
                            secondNameTextField.getValue(),
                            middleNameTextField.getValue(),
                            phoneTextField.getValue());
                    modalWindow.close();
                }
            });

            Button btnCancel = new Button("Отменить");
            btnCancel.setStyleName("danger");
            btnCancel.addClickListener(e -> {
                modalWindow.close();
                new Notification(null, "Изменение пациента отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            });

            VerticalLayout modalWindowLayout = new VerticalLayout();
            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
            buttonModalBar.setWidth("350px");
            modalWindow.setContent(modalWindowLayout);
            modalWindowLayout.addComponents(
                    firstNameTextField,
                    secondNameTextField,
                    middleNameTextField,
                    phoneTextField,
                    phoneFormatLabel,
                    buttonModalBar,
                    errorMessage);
            modalWindow.center();

            modalWindowLayout.setComponentAlignment(firstNameTextField,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(secondNameTextField,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(middleNameTextField,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(phoneFormatLabel,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(phoneTextField,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(buttonModalBar,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(errorMessage,Alignment.TOP_CENTER);
            buttonModalBar.setComponentAlignment(btnOK,Alignment.TOP_LEFT);
            buttonModalBar.setComponentAlignment(btnCancel,Alignment.TOP_RIGHT);

            mainView.addWindow(modalWindow);


        }
        else
            new Notification(null, "Выберите пациента", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());

    }
    private static void editPatient(Long id, String firstName, String secondName, String middleName, String phoneNumber){

        Patient patient = patientService.findPatient(id);
        patient.setFirstName(firstName);
        patient.setSecondName(secondName);
        patient.setMiddleName(middleName);
        patient.setPhoneNumber(phoneNumber);
        patientService.updatePatient(patient);
        new Notification(null, "Доктор изменен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updatePatientsTable();
        selectedPatient = null;
    }
    private static void openModalDeletePatient(MainView mainView){
        if(selectedPatient!=null){
            modalWindow = new Window("Удаление пациента");
            modalWindow.setModal(true);

            String middleName;
            if (!selectedPatient.getMiddleName().equals(""))
                middleName = selectedPatient.getMiddleName();
            else
                middleName = "<Отсутствует>";

            Label label = new Label(
                    "Вы уверены что хотите удалить врача: \n" +
                            "<ul>"+
                            "  <li> Имя: "+ selectedPatient.getFirstName() +" </li> "+
                            "  <li> Фамилия: "+ selectedPatient.getSecondName() +" </li> "+
                            "  <li> Отчество: "+ middleName +" </li> "+
                            "  <li> Номер телефона: "+ selectedPatient.getPhoneNumber() +" </li> "+
                            "</ul> ",
                    ContentMode.HTML);

            Button btnOK = new Button("Удалить");
            btnOK.setStyleName("friendly");
            btnOK.addClickListener(event -> {
                modalWindow.close();
                deletePatient();
            });

            Button btnCancel = new Button("Отменить");
            btnCancel.setStyleName("danger");
            btnCancel.addClickListener(e -> {
                modalWindow.close();
                new Notification(null, "Удаление пациента отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            });
            VerticalLayout modalWindowLayout = new VerticalLayout();
            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
            buttonModalBar.setWidth("450px");
            modalWindow.setContent(modalWindowLayout);
            modalWindowLayout.addComponents(
                    label,
                    buttonModalBar);
            modalWindow.center();
            modalWindowLayout.setComponentAlignment(label,Alignment.TOP_CENTER);
            modalWindowLayout.setComponentAlignment(buttonModalBar,Alignment.TOP_CENTER);
            buttonModalBar.setComponentAlignment(btnOK,Alignment.TOP_LEFT);
            buttonModalBar.setComponentAlignment(btnCancel,Alignment.TOP_RIGHT);
            modalWindowLayout.setWidth("500px");
            mainView.addWindow(modalWindow);

        }
        else
            new Notification(null, "Выберите пациента", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
    }
    private static void deletePatient(){
        try {
            patientService.deletePatient(selectedPatient);
            new Notification(null, "Пациент удален", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
            updatePatientsTable();
            selectedPatient = null;
        } catch (Exception e) {
            if(e.getCause() instanceof ConstraintViolationException)
                new Notification(null, "У пациента уже есть рецепты!", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
            else
                new Notification(null, "Неизвестная ошибка", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
        }
    }
}
