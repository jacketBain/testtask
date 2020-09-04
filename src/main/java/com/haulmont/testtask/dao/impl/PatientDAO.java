package com.haulmont.testtask.dao.impl;

import com.haulmont.testtask.dao.IDao;

import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements  IDao<Patient,Long> {

    public Patient findById(Long id) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            tx1.commit();
            session.close();
            return patient;
            }

    public void save(Patient patient) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.save(patient);
            tx1.commit();
            session.close();
            }

    public void update(Patient patient) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.update(patient);
            tx1.commit();
            session.close();
            }
    public void delete(Patient patient) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.delete(patient);
            tx1.commit();
            session.close();
            }

    public List<Patient> findAll() {
            List<Patient> patients = (ArrayList<Patient>)  HibernateSessionFactoryUtil.getSessionFactory()
                    .openSession().createQuery("From Patient ",Patient.class).list();
            return patients;
            }

}
