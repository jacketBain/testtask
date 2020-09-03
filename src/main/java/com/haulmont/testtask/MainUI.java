package com.haulmont.testtask;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.hibernate.Session;

@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        layout.addComponent(new Label("Main UI"));

        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Patient pat = new Patient();
        pat.setPatientId(new Long(1));
        pat.setFirstName("Nick");
        pat.setSecondName("Kuprin");
        pat.setMiddleName("Sergeevic");
        pat.setPhoneNumber("+79379871945");
        session.save(pat);
        session.getTransaction().commit();
        session.close();

        setContent(layout);
    }
}