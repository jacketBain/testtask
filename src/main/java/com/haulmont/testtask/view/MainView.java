package com.haulmont.testtask.view;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.hibernate.Session;

import java.io.File;

@Theme(ValoTheme.THEME_NAME)
public class MainView extends UI {

    @Override
    protected void init(VaadinRequest request) {
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resourceDocImage = new FileResource(new File(basepath +
                "/WEB-INF/images/banner.jpg"));
        FileResource resourcePacImage = new FileResource(new File(basepath +
                "/WEB-INF/images/mainLogo.png"));

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        HorizontalLayout mainBar = new HorizontalLayout();

        //mainBar.setMargin(true);
        //mainBar.addComponent(new Label("База данных врачей, пациентов и рецептов"));
        Image mainLogo = new Image();
        mainLogo.setSource(resourceDocImage);
        mainBar.addComponent(mainLogo);
        layout.addComponent(mainBar);
        layout.setComponentAlignment(mainBar,Alignment.TOP_CENTER);

        TabSheet tabsheet = new TabSheet();
        //tabsheet.setWidth("1000px");
        layout.addComponent(tabsheet);
        layout.setComponentAlignment(tabsheet,Alignment.TOP_CENTER);

        VerticalLayout doctorsTab = new VerticalLayout();
        DoctorView.initAll(this,doctorsTab);
        tabsheet.addTab(doctorsTab, "Врачи");

        VerticalLayout patientsTab = new VerticalLayout();
        PatientView.initAll(this, patientsTab);
        tabsheet.addTab(patientsTab, "Пациенты");

        setContent(layout);
    }
}