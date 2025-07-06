package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class App
{
    public static void main( String[] args ){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        try (Session session = sessionFactory.openSession())
        {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PurchaseList> query = builder.createQuery(PurchaseList.class);
            Root<PurchaseList>PLRoot = query.from(PurchaseList.class);
            query.select(PLRoot);
            List<PurchaseList>purchases = session.createQuery(query).getResultList();
            for (PurchaseList purch : purchases) {
                CriteriaQuery<Course> courseQuery = builder.createQuery(Course.class);
                Root<Course>courseRoot = courseQuery.from(Course.class);
                courseQuery.select(courseRoot).where(
                        builder.equal(courseRoot.get("name"), purch.getCourseName())
                );
                int crsId = session.createQuery(courseQuery).getSingleResult().getId();

                CriteriaQuery<Student> studentQuery = builder.createQuery(Student.class);
                Root<Student>studentRoot = studentQuery.from(Student.class);
                studentQuery.select(studentRoot).where(
                        builder.equal(studentRoot.get("name"), purch.getStudentName())
                );
                int stdId = session.createQuery(studentQuery).getSingleResult().getId();

                Transaction tx = session.beginTransaction();
                LinkedPurchaseList lpl = new LinkedPurchaseList();
                lpl.setId(new LinkedPLKey(stdId,crsId));

                tx.commit();
            }















//            Transaction tx = session.beginTransaction();
//            try (Stream<PurchaseList> plStream = session
//                    .createQuery("FROM PurchaseList", PurchaseList.class)
//                    .stream())
//            {
//                plStream.forEach(c -> {
//                    c.
//                });
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        sessionFactory.close();
    }
}
