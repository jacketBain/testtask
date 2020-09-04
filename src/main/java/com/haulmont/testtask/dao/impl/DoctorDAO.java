package com.haulmont.testtask.dao.impl;

import com.haulmont.testtask.dao.IDao;
import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DoctorDAO implements IDao<Doctor,Long> {

    public Doctor findById(Long id) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        Doctor doctor = session.get(Doctor.class, id);
        tx1.commit();
        session.close();
        return doctor;
    }

    public void save(Doctor doctor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(doctor);
        tx1.commit();
        session.close();
    }

    public void update(Doctor doctor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(doctor);
        tx1.commit();
        session.close();
    }
    public void delete(Doctor doctor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(doctor);
        tx1.commit();
        session.close();
    }

    public List<Doctor> findAll() {
        List<Doctor> doctors = (ArrayList<Doctor>)  HibernateSessionFactoryUtil.getSessionFactory()
                .openSession().createQuery("From Doctor ",Doctor.class).list();
        return doctors;
    }
}
