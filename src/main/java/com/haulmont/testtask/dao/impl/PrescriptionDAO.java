package com.haulmont.testtask.dao.impl;

import com.haulmont.testtask.dao.IDao;
import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.entity.Prescription;
import com.haulmont.testtask.utils.HibernateSessionFactoryUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
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
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
            List<Prescription> prescriptions = (ArrayList<Prescription>)  session.createQuery("From Prescription ",Prescription.class).list();
            tx1.commit();
            session.close();
            return prescriptions;
            }
    public List<Prescription> getFilter(Long id, String priority, String description){

            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Prescription> criteriaQuery = criteriaBuilder.createQuery(Prescription.class);
            Root<Prescription> root = criteriaQuery.from(Prescription.class);

            Predicate byId = criteriaBuilder.equal(root.get("patientId"),id);
            Predicate byPriority = criteriaBuilder.equal(root.get("priority"),priority);
            Predicate byDescript = criteriaBuilder.like(root.get("description"),"%" + description + "%");

            ArrayList<Predicate> predicatesArray = new ArrayList<>();
            predicatesArray.add(byId);

            if(!priority.equals("-"))
                    predicatesArray.add(byPriority);
            if(!description.equals(""))
                    predicatesArray.add(byDescript);

            Predicate[] predicates = predicatesArray.toArray(new Predicate[predicatesArray.size()]);
            criteriaQuery.select(root)
                    .where(predicates);

            try {
                    return session.createQuery(criteriaQuery).getResultList();
            } catch (NoResultException e) {
                    e.printStackTrace();
                    return null;
            }

    }

}
