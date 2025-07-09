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

                List<Course> courses = session.createNativeQuery("select * from courses", Course.class).getResultList();
                List<Teacher> teachers = session.createNativeQuery("select * from teachers", Teacher.class).getResultList();
                List<String> listWithTeachers = new ArrayList<>();
                for (Course course : courses){
                    for (Teacher teacher : teachers){
                        if (course.getTeacher().getId() == teacher.getId()){
                            String elementOfList = String.join(" - ",
                                    String.valueOf(course.getId()),
                                    course.getName(),
                                    teacher.getName());
                            listWithTeachers.add(elementOfList);
                            break;
                        }
                    }
                }
                listWithTeachers.forEach(System.out::println);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void testConnection(){

    }
}
