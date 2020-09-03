package com.haulmont.testtask;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class TestMain {
    public static void main(String[] args)
    {
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

    }
}
