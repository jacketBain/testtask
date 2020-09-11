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
import com.vaadin.shared.ui.ContentMode;
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
        patientService = new PatientService();

        HorizontalLayout filterBar = new HorizontalLayout();
        ComboBox<Patient> patientComboBox = new ComboBox<>("Пациент");
        try{
            List<Patient> patients = patientService.findAllPatients();
            patientComboBox.setItemCaptionGenerator(Patient::getFullName);
            patientComboBox.setItems(patients);
            patientComboBox.setEmptySelectionAllowed(false);
            patientComboBox.setValue(patients.get(0));
        } catch (Exception ex) {
        }

        TextField descriptionTextField = new TextField("Описание");
        descriptionTextField.setWidth("320px");

        ComboBox<String> priorityComboBox = new ComboBox<>("Приоритет");
        priorityComboBox.setItems("-","Нормальный","Cito","Statim");
        priorityComboBox.setValue("-");
        priorityComboBox.setEmptySelectionAllowed(false);

        Button btnApplyFilter = new Button("Применить");
        btnApplyFilter.setStyleName("friendly");
        Button btnOffFilter = new Button("Отменить");
        btnOffFilter.setStyleName("danger");

        filterBar.addComponents(patientComboBox,priorityComboBox,descriptionTextField,btnApplyFilter,btnOffFilter);

        filterBar.setComponentAlignment(btnApplyFilter,Alignment.BOTTOM_CENTER);
        filterBar.setComponentAlignment(btnOffFilter,Alignment.BOTTOM_CENTER);

        btnsLayout.addComponents(btnAddPrescription, btnEditPrescription, btnDelPrescription);

        /* Events */

        btnAddPrescription.addClickListener(event -> openModalAddPrescription(mainView));
        btnEditPrescription.addClickListener(event -> openModalEditPrescription(mainView));
        btnDelPrescription.addClickListener(event -> openModalDeletePrescription(mainView));
        btnApplyFilter.addClickListener(event -> applyFilter(patientComboBox.getSelectedItem().get(),priorityComboBox.getValue(),descriptionTextField.getValue()));
        btnOffFilter.addClickListener(event -> updatePrescriptionsTable());

        fillPrescriptionsTable();

        layout.addComponents(btnsLayout, gridPrescription, filterBar);
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
        doctorComboBox.setWidth("320px");

        ComboBox<Patient> patientComboBox = new ComboBox<>("Пациент");
        try{
            List<Patient> patients = patientService.findAllPatients();
            patientComboBox.setItemCaptionGenerator(Patient::getFullName);
            patientComboBox.setItems(patients);
            patientComboBox.setEmptySelectionAllowed(false);
            patientComboBox.setValue(patients.get(0));
        } catch (Exception ex) {
            new Notification(null, "Ошибка получения списка пациентов", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        }
        patientComboBox.setWidth("320px");

        ComboBox<String> priorityComboBox = new ComboBox<>("Приоритет");
        priorityComboBox.setItems("Нормальный","Cito","Statim");
        priorityComboBox.setValue("Нормальный");
        priorityComboBox.setEmptySelectionAllowed(false);
        patientComboBox.setWidth("320px");

        DateField startDateField = new DateField("Дата начала действия");
        startDateField.setValue(LocalDate.now());
        startDateField.setWidth("320px");

        TextField durationTextField = new TextField("Срок действия (в днях)");
        durationTextField.setWidth("320px");

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

        Button btnOK = new Button("Ок");
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

        modalWindowLayout.setComponentAlignment(descriptionTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(doctorComboBox,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(patientComboBox,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(startDateField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(durationTextField,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(priorityComboBox,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(buttonModalBar,Alignment.TOP_CENTER);
        modalWindowLayout.setComponentAlignment(errorMessage,Alignment.TOP_CENTER);
        buttonModalBar.setComponentAlignment(btnOK,Alignment.TOP_LEFT);
        buttonModalBar.setComponentAlignment(btnCancel,Alignment.TOP_RIGHT);

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
    private static void openModalEditPrescription(MainView mainView){
       if(selectedPrescription!=null){
           modalWindow = new Window("Изменение рецепта");

           modalWindow.setModal(true);

           TextField descriptionTextField = new TextField("Описание");
           descriptionTextField.setWidth("320px");
           descriptionTextField.setValue(selectedPrescription.getDescription());

           doctorService = new DoctorService();
           patientService = new PatientService();

           ComboBox<Doctor> doctorComboBox = new ComboBox<>("Врач");
           try{
               List<Doctor> doctors = doctorService.findAllDoctors();
               doctorComboBox.setItemCaptionGenerator(Doctor::getFullName);
               doctorComboBox.setItems(doctors);
               doctorComboBox.setEmptySelectionAllowed(false);
               doctorComboBox.setSelectedItem(selectedPrescription.getDoctorId());
           } catch (Exception ex) {
               new Notification(null, "Ошибка получения списка врачей", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
           }
           doctorComboBox.setWidth("320px");

           ComboBox<Patient> patientComboBox = new ComboBox<>("Пациент");
           try{
               List<Patient> patients = patientService.findAllPatients();
               patientComboBox.setItemCaptionGenerator(Patient::getFullName);
               patientComboBox.setItems(patients);
               patientComboBox.setEmptySelectionAllowed(false);
               patientComboBox.setSelectedItem(selectedPrescription.getPatientId());
           } catch (Exception ex) {
               new Notification(null, "Ошибка получения списка пациентов", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
           }
           patientComboBox.setWidth("320px");

           ComboBox<String> priorityComboBox = new ComboBox<>("Приоритет");
           priorityComboBox.setItems("Нормальный","Cito","Statim");
           priorityComboBox.setValue("Нормальный");
           priorityComboBox.setEmptySelectionAllowed(false);
           priorityComboBox.setSelectedItem(selectedPrescription.getPriority());
           priorityComboBox.setWidth("320px");

           DateField startDateField = new DateField("Дата начала действия");
           startDateField.setValue(selectedPrescription.getDateStart().toLocalDate());
           startDateField.setWidth("320px");


           TextField durationTextField = new TextField("Срок действия (в днях)");
           durationTextField.setValue(selectedPrescription.getDuration().toString());
           descriptionTextField.setWidth("320px");
           durationTextField.setWidth("320px");

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

           Button btnOK = new Button("Ок");
           btnOK.setStyleName("friendly");
           btnOK.addClickListener(event -> {
               if(binder.validate().isOk()) {
                   editPrescription(selectedPrescription.getPrescriptionId(),
                           descriptionTextField.getValue(),
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
               new Notification(null, "Изменение рецепта отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
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

           modalWindowLayout.setComponentAlignment(descriptionTextField,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(doctorComboBox,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(patientComboBox,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(startDateField,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(durationTextField,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(priorityComboBox,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(buttonModalBar,Alignment.TOP_CENTER);
           modalWindowLayout.setComponentAlignment(errorMessage,Alignment.TOP_CENTER);
           buttonModalBar.setComponentAlignment(btnOK,Alignment.TOP_LEFT);
           buttonModalBar.setComponentAlignment(btnCancel,Alignment.TOP_RIGHT);

           mainView.addWindow(modalWindow);


       }
       else
            new Notification(null, "Выберите рецепт", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
   }
    private static void editPrescription(Long id,String description, Doctor doctor, Patient patient,Date date, Long duration, String priority){

        Prescription prescription = prescriptionService.findPrescription(id);
        prescription.setDescription(description);
        prescription.setDoctorId(doctor);
        prescription.setPatientId(patient);
        prescription.setDateStart(date);
        prescription.setDuration(duration);
        prescription.setPriority(priority);
        prescriptionService.updatePrescription(prescription);
        new Notification(null, "Рецепт изменен", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        updatePrescriptionsTable();
        selectedPrescription = null;
    }
    private static void openModalDeletePrescription(MainView mainView){
        if(selectedPrescription!=null){
            modalWindow = new Window("Удаление рецепта");
            modalWindow.setModal(true);

            Label label = new Label(
                    "Вы уверены что хотите удалить рецепт: \n" +
                            "<ul>"+
                            "  <li> ФИО врача: "+ selectedPrescription.getDoctorId().getFullName() +" </li> "+
                            "  <li> ФИО пациента: "+ selectedPrescription.getPatientId().getFullName() +" </li> "+
                            "  <li> Приоритет: "+ selectedPrescription.getPriority() +" </li> "+
                            "  <li> Дата начала: "+ selectedPrescription.getDateStartToString() +" </li> "+
                            "  <li> Дата конца: "+ selectedPrescription.getDateStopToString() +" </li> "+
                            "</ul> ",
                    ContentMode.HTML);

            Button btnOK = new Button("Удалить");
            btnOK.setStyleName("friendly");
            btnOK.addClickListener(event -> {
                modalWindow.close();
                deletePrescription();
            });

            Button btnCancel = new Button("Отменить");
            btnCancel.setStyleName("danger");
            btnCancel.addClickListener(e -> {
                modalWindow.close();
                new Notification(null, "Удаление рецепта отменено", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
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
            new Notification(null, "Выберите рецепт", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
    }
    private static void deletePrescription(){
        try {
            prescriptionService.deletePrescription(selectedPrescription);
            new Notification(null, "Рецепт удален", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
            updatePrescriptionsTable();
            selectedPrescription = null;
        } catch (Exception e) {
            e.printStackTrace();
            new Notification(null, "Неизвестная ошибка удаления", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        }
    }
    private static void applyFilter(Patient patient, String priority, String description){
        prescriptions = prescriptionService.getFilter(patient.getPatientId(),priority,description);
        gridPrescription.setItems(prescriptions);
        selectedPrescription = null;
    }
}
