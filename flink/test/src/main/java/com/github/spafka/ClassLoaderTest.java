package com.github.spafka;

import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderTest {

    @Test
    public void TestClassLoaderClassNotFoundEx() {

        try {
            Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("classLoader ant find class");
        }


    }

    @Test
    public void TestUrlClassLoader() throws MalformedURLException {

        URLClassLoader loader = new URLClassLoader(new URL[]{new File("src/main/extlib/mysql-connector-java.jar").toURL()});
        try {

            Class<?> loadClass = loader.loadClass("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("classLoader ant find class");
        }


    }
}
