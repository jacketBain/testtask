package com.haulmont.testtask.utils;

import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateSessionFactoryUtil {

    private static SessionFactory sessionFactory;

    public HibernateSessionFactoryUtil() {
    }

    public static SessionFactory getSessionFactory()
    {
        if(sessionFactory==null)
        {
            try
            {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
