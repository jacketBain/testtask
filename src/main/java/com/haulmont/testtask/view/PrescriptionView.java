package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.entity.Prescription;
import com.haulmont.testtask.services.DoctorService;
import com.haulmont.testtask.services.PatientService;
import com.haulmont.testtask.services.PrescriptionService;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class PrescriptionView {
    private static Grid<Prescription> gridPrescription;

    private static Window modalWindow;

    private static List<Prescription> prescriptions;
    private static PrescriptionService prescriptionService;
    private static DoctorService doctorService;
    private static PatientService patientService;
    private static Prescription selectedPrescription;

    public static void initAll(MainView mainView, VerticalLayout layout) {
        gridPrescription = new Grid<>();
        gridPrescription.setWidthFull();
        gridPrescription.addItemClickListener(event -> selectedPrescription = event.getItem());
        gridPrescription.setHeight("600px");
        HorizontalLayout btnsLayout = new HorizontalLayout();
        prescriptions = null;
        selectedPrescription = null;

        Button btnAddPrescription = new Button("Добавить");
        btnAddPrescription.setStyleName("friendly");

        Button btnEditPrescription = new Button("Изменить");
        btnEditPrescription.setStyleName("primary");

        Button btnDelPrescription = new Button("Удалить");
        btnDelPrescription.setStyleName("danger");

        prescriptionService = new PrescriptionService();

        btnsLayout.addComponents(btnAddPrescription, btnEditPrescription, btnDelPrescription);

        //Events

        btnAddPrescription.addClickListener(event -> openModalAddPrescription(mainView));
//        btnEditPrescription.addClickListener(event -> openModalEditPrescription(mainView));
//        btnDelPrescription.addClickListener(event -> openModalDeletePrescription(mainView));

        fillPrescriptionsTable();

        layout.addComponents(btnsLayout, gridPrescription);
    }
    private static void fillPrescriptionsTable() {
        prescriptions = prescriptionService.findAllPrescriptions();
        gridPrescription.setItems(prescriptions);
        gridPrescription.addColumn(Prescription::getFullNameDoctor).setCaption("ФИО врача");
        gridPrescription.addColumn(Prescription::getFullNamePatient).setCaption("ФИО пациента");
        gridPrescription.addColumn(Prescription::getDescription).setCaption("Описание");
        gridPrescription.addColumn(Prescription::getPriority).setCaption("Приоритет");
        gridPrescription.addColumn(Prescription::getDateStartToString).setCaption("Дата начала");
        gridPrescription.addColumn(Prescription::getDateStopToString).setCaption("Дата окончания");
    }
    private static void updatePrescriptionsTable() {
        prescriptions = prescriptionService.findAllPrescriptions();
        gridPrescription.setItems(prescriptions);
    }
    private static void openModalAddPrescription(MainView mainView){
        modalWindow = new Window("Добавление рецепта");

        modalWindow.setModal(true);

        TextField descriptionTextField = new TextField("Описание");
        descriptionTextField.setWidth("320px");

        doctorService = new DoctorService();
        patientService = new PatientService();

        ComboBox<Doctor> doctorComboBox = new ComboBox<>("Врач");
        try{
            List<Doctor> doctors = doctorService.findAllDoctors();
            doctorComboBox.setItemCaptionGenerator(Doctor::getFullName);
            doctorComboBox.setItems(doctors);
            doctorComboBox.setEmptySelectionAllowed(false);
            doctorComboBox.setValue(doctors.get(0));
        } catch (Exception ex) {
            new Notification(null, "Ошибка получения списка врачей", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        }

        ComboBox<Patient> patientComboBox = new ComboBox<>("Врач");
        try{
            List<Patient> patients = patientService.findAllPatients();
            patientComboBox.setItemCaptionGenerator(Patient::getFullName);
            patientComboBox.setItems(patients);
            patientComboBox.setEmptySelectionAllowed(false);
            patientComboBox.setValue(patients.get(0));
        } catch (Exception ex) {
            new Notification(null, "Ошибка получения списка пациентов", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        }

        ComboBox<String> priorityComboBox = new ComboBox<>("Приоритет");
        priorityComboBox.setItems("Нормальный","Cito","Statim");
        priorityComboBox.setValue("Нормальный");
        priorityComboBox.setEmptySelectionAllowed(false);

        DateField startDateField = new DateField("Дата начала действия");
        startDateField.setValue(LocalDate.now());

        TextField durationTextField = new TextField("Срок действия (в днях)");
        descriptionTextField.setWidth("320px");

        Label errorMessage = new Label();

        Binder<Prescription> binder = new Binder<>();
        binder.forField(descriptionTextField).asRequired()
                .withValidator(text -> text.length() >= 2 && text.length() <=255,"Некорректная длинна описания.")
                .withStatusLabel(errorMessage)
                .bind(Prescription::getDescription, Prescription::setDescription);
        binder.forField(doctorComboBox).asRequired()
                .withValidator(Objects::nonNull,"Не выбран врач")
                .withStatusLabel(errorMessage)
                .bind(Prescription::getDoctorId, Prescription::setDoctorId);
        binder.forField(patientComboBox).asRequired()
                .withValidator(Objects::nonNull,"Не выбран пациент")
                .withStatusLabel(errorMessage)
                .bind(Prescription::getPatientId, Prescription::setPatientId);
        binder.forField(startDateField).asRequired("Некорректный формат даты")
                .bind(Prescription::getDateStartInLocalDate, Prescription::setDateStartInLocal);
        binder.forField(durationTextField).asRequired()
                .withConverter(new StringToLongConverter("Введите число дней"))
                .withValidator(dur -> dur > 0, "Срок действия должен быть больше 0")
                .bind(Prescription::getDuration, Prescription::setDuration);
        binder.forField(priorityComboBox).asRequired();

        Button btnOK = new Button("Добавить");
        btnOK.setStyleName("friendly");
        btnOK.addClickListener(event -> {
            if(binder.validate().isOk()) {
                addPrescription(descriptionTextField.getValue(),
                        doctorComboBox.getSelectedItem().get(),
                        patientComboBox.getSelectedItem().get(),
                        Date.valueOf(startDateField.getValue()),
                        Long.parseLong(durationTextField.getValue()),
                        priorityComboBox.getValue());
                modalWindow.close();
            }
        });

        Button btnCancel = new Button("Отменить");
        btnCancel.setStyleName("danger");
        btnCancel.addClickListener(e -> {
            modalWindow.close();
            new Notification(null, "Добавление рецепта отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        });

        VerticalLayout modalWindowLayout = new VerticalLayout();
        HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
        buttonModalBar.setWidth("350px");
        modalWindow.setContent(modalWindowLayout);
        modalWindowLayout.addComponents(
                descriptionTextField,
                doctorComboBox,
                patientComboBox,
                startDateField,
                durationTextField,
                priorityComboBox,
                buttonModalBar,
                errorMessage);
        modalWindow.center();
        modalWindowLayout.setWidth("350px");
        mainView.addWindow(modalWindow);

    }
    private static void addPrescription(String description, Doctor doctor, Patient patient,Date date, Long duration, String priority){
        Prescription prescription = new Prescription();
        prescription.setDescription(description);
        prescription.setDoctorId(doctor);
        prescription.setPatientId(patient);
        prescription.setDateStart(date);
        prescription.setDuration(duration);
        prescription.setPriority(priority);
        prescriptionService.savePrescription(prescription);
        new Notification(null, "Рецепт добавлен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updatePrescriptionsTable();
    }
//    private static void openModalEditPrescription(MainView mainView){
//        if(selectedPrescription!=null){
//            modalWindow = new Window("Изменение пациента");
//
//            modalWindow.setModal(true);
//
//            TextField firstNameTextField = new TextField("Имя");
//            firstNameTextField.setStyleName("modalElem");
//            firstNameTextField.setWidth("320px");
//            firstNameTextField.setValue(selectedPrescription.getFirstName());
//
//            TextField secondNameTextField = new TextField("Фамилия");
//            secondNameTextField.setStyleName("modalElem");
//            secondNameTextField.setWidth("320px");
//            secondNameTextField.setValue(selectedPrescription.getSecondName());
//
//            TextField middleNameTextField = new TextField("Отчество");
//            middleNameTextField.setStyleName("modalElem");
//            middleNameTextField.setWidth("320px");
//            middleNameTextField.setValue(selectedPrescription.getMiddleName());
//
//            Label phoneFormatLabel = new Label("Формат телефона +7xxxxxxxxxx");
//            TextField phoneTextField = new TextField("Телефон");
//            phoneTextField.setStyleName("modalElem");
//            phoneTextField.setValue(selectedPrescription.getPhoneNumber());
//            phoneTextField.setWidth("320px");
//            Label errorMessage = new Label();
//
//
//            Binder<Prescription> binder = new Binder<>();
//            binder.forField(firstNameTextField).asRequired()
//                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна имени.")
//                    .withStatusLabel(errorMessage)
//                    .bind(Prescription::getFirstName, Prescription::setFirstName);
//            binder.forField(secondNameTextField).asRequired()
//                    .withValidator(name -> name.length() >= 2 && name.length() <=50,"Некоректная длинна фамилии.")
//                    .withStatusLabel(errorMessage)
//                    .bind(Prescription::getSecondName, Prescription::setSecondName);
//            binder.forField(middleNameTextField).asRequired()
//                    .withValidator(name -> name.length() <=50,"Некоректная длинна отчества.")
//                    .withStatusLabel(errorMessage)
//                    .bind(Prescription::getMiddleName, Prescription::setMiddleName);
//            binder.forField(phoneTextField).asRequired()
//                    .withValidator(name -> name.length() > 10 && name.substring(0, 2).equals("+7") && name.length() <= 12, "Неверный формат номера")
//                    .withStatusLabel(errorMessage)
//                    .bind(Prescription::getPhoneNumber, Prescription::setPhoneNumber);
//
//            Button btnOK = new Button("Изменить");
//            btnOK.setStyleName("friendly");
//            btnOK.addClickListener(event -> {
//                if(binder.validate().isOk()) {
//                    editPrescription(
//                            selectedPrescription.getPrescriptionId(),
//                            firstNameTextField.getValue(),
//                            secondNameTextField.getValue(),
//                            middleNameTextField.getValue(),
//                            phoneTextField.getValue());
//                    modalWindow.close();
//                }
//            });
//
//            Button btnCancel = new Button("Отменить");
//            btnCancel.setStyleName("danger");
//            btnCancel.addClickListener(e -> {
//                modalWindow.close();
//                new Notification(null, "Изменение пациента отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
//            });
//
//            VerticalLayout modalWindowLayout = new VerticalLayout();
//            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
//            buttonModalBar.setWidth("350px");
//            modalWindow.setContent(modalWindowLayout);
//            modalWindowLayout.addComponents(
//                    firstNameTextField,
//                    secondNameTextField,
//                    middleNameTextField,
//                    phoneTextField,
//                    buttonModalBar);
//            modalWindow.center();
//            modalWindowLayout.setWidth("350px");
//            mainView.addWindow(modalWindow);
//
//
//        }
//        else
//            new Notification(null, "Выберите пациента", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
//
//    }
//    private static void editPrescription(Long id, String firstName, String secondName, String middleName, String phoneNumber){
//
//        Prescription Prescription = PrescriptionService.findPrescription(id);
//        Prescription.setFirstName(firstName);
//        Prescription.setSecondName(secondName);
//        Prescription.setMiddleName(middleName);
//        Prescription.setPhoneNumber(phoneNumber);
//        PrescriptionService.updatePrescription(Prescription);
//        new Notification(null, "Доктор изменен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
//        updatePrescriptionsTable();
//        selectedPrescription = null;
//    }
//    private static void openModalDeletePrescription(MainView mainView){
//        if(selectedPrescription!=null){
//            modalWindow = new Window("Удаление пациента");
//            modalWindow.setModal(true);
//
//            Label label = new Label(
//                    "Вы уверены что хотите удалить врача: \n" +
//                            "<ul>"+
//                            "  <li> Имя: "+ selectedPrescription.getFirstName() +" </li> "+
//                            "  <li> Фамилия: "+ selectedPrescription.getSecondName() +" </li> "+
//                            "  <li> Отчество: "+ selectedPrescription.getMiddleName() +" </li> "+
//                            "  <li> Номер телефона: "+ selectedPrescription.getPhoneNumber() +" </li> "+
//                            "</ul> ",
//                    ContentMode.HTML);
//
//            Button btnOK = new Button("Удалить");
//            btnOK.setStyleName("friendly");
//            btnOK.addClickListener(event -> {
//                modalWindow.close();
//                deletePrescription();
//            });
//
//            Button btnCancel = new Button("Отменить");
//            btnCancel.setStyleName("danger");
//            btnCancel.addClickListener(e -> {
//                modalWindow.close();
//                new Notification(null, "Удаление пациента отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
//            });
//            VerticalLayout modalWindowLayout = new VerticalLayout();
//            HorizontalLayout buttonModalBar = new HorizontalLayout(btnOK,btnCancel);
//            buttonModalBar.setWidth("500px");
//            modalWindow.setContent(modalWindowLayout);
//            modalWindowLayout.addComponents(
//                    label,
//                    buttonModalBar);
//            modalWindow.center();
//            modalWindowLayout.setWidth("500px");
//            mainView.addWindow(modalWindow);
//
//        }
//        else
//            new Notification(null, "Выберите пациента", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
//    }
//    private static void deletePrescription(){
//        PrescriptionService.deletePrescription(selectedPrescription);
//        new Notification(null, "Пациент удален", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
//        updatePrescriptionsTable();
//        selectedPrescription = null;
//    }
}
