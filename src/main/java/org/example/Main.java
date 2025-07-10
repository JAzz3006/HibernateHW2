package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME)
                .build();
        ){
            Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
            try(SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
                Session session = sessionFactory.openSession()) {



//                List<Teacher> teachers = session.createNativeQuery("select * from teachers", Teacher.class).getResultList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void testConnection(){

    }

    public static List<String> teacherNameInfo (Session s){
        List<Course> courses = s.createNativeQuery("select * from courses", Course.class).getResultList();
        return courses.stream()
                .map(c -> String.join(" - ", String.valueOf(c.getId()), c.getName(), c.getTeacher().getName()))
                .collect(Collectors.toList());
    }
}
