package com.haulmont.testtask.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebAppPropertiesListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String rootPath = servletContextEvent.getServletContext().getRealPath("/");
        System.setProperty("webroot", rootPath);
    }

}