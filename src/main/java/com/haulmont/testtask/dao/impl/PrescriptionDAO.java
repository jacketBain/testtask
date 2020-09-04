package com.haulmont.testtask.dao.impl;

import com.haulmont.testtask.dao.IDao;
import com.haulmont.testtask.entity.Prescription;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO implements IDao<Prescription,Long> {
    public Prescription findById(Long id) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            Prescription prescription = session.get(Prescription.class, id);
            tx1.commit();
            session.close();
            return prescription;
            }

    public void save(Prescription prescription) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.save(prescription);
            tx1.commit();
            session.close();
            }

    public void update(Prescription prescription) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.update(prescription);
            tx1.commit();
            session.close();
            }
    public void delete(Prescription prescription) {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            session.delete(prescription);
            tx1.commit();
            session.close();
            }

    public List<Prescription> findAll() {
            List<Prescription> prescriptions = (ArrayList<Prescription>)  HibernateSessionFactoryUtil.getSessionFactory()
            .openSession().createQuery("From Prescription ",Prescription.class).list();
            return prescriptions;
            }
}
