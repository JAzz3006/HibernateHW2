package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.List;

public class App{
    public static void main( String[] args ){
        SessionFactory sessionFactory = null;
        Session session = null;
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();
            Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
            session = sessionFactory.openSession();
        } catch (Exception e) {
            System.out.println("Возникла ошибка при инициализации Hibernate: " + e.getClass().getName() +
                    "\nСообщение: " + e.getMessage() + "\nВозможная причина:");
            if (e.getMessage().contains("Unknown database")){
                System.out.println("Проверьте наименование базы данных в src/main/resources/hibernate.cfg.xml");
            }else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("SQL-сервер не активен или Неверно указан порт в src/main/resources/hibernate.cfg.xml");
            }else if (e.getMessage().contains("Access denied")) {
                System.out.println("Проверьте имя пользователя и/или пароль в src/main/resources/hibernate.cfg.xml");
            }else if (e.getMessage().contains("Public Key Retrieval is not allowed")) {
                System.out.println("Проверьте имя пользователя и/или пароль в src/main/resources/hibernate.cfg.xml");
            } else if (e.getMessage().contains("No suitable driver found")) {
                System.out.println("Проверьте подключение sql-драйвера в pom.xml\nПроверьте корректность url " +
                        "в src/main/resources/hibernate.cfg.xml");
            }else if (e.getMessage().contains("does not belong to the same persistence unit")) {
                System.out.println("Проверьте мапинг классов в src/main/resources/hibernate.cfg.xml");
            }else if (e.getMessage().contains("which is not an '@Entity' type")) {
                System.out.println("Убедитесь в наличии аннотации @Entity у всех классов, соответствующих таблицам в БД");
            }
        }

        if (session == null){
            System.out.println("Сессия не инициализирована. Выполнение программы остановлено");
            return;
        }

        Transaction tx = session.beginTransaction();

        try{
            session.createMutationQuery("delete from LinkedPurchaseList").executeUpdate();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PurchaseList> query = builder.createQuery(PurchaseList.class);
            Root<PurchaseList>PLRoot = query.from(PurchaseList.class);
            query.select(PLRoot);
            List<PurchaseList>purchases = session.createQuery(query).getResultList();
            if (purchases.isEmpty()){
                System.out.println("БД не заполнена, загрузите данные. Выполнение остановлено");
                session.close();
                sessionFactory.close();
                return;
            }
            for (PurchaseList purch : purchases) {
                CriteriaQuery<Course> courseQuery = builder.createQuery(Course.class);
                Root<Course>courseRoot = courseQuery.from(Course.class);
                courseQuery.select(courseRoot).where(
                        builder.equal(courseRoot.get("name"), purch.getCourseName())
                );
                Course course;
                List<Course> courses = session.createQuery(courseQuery).getResultList();
                if (courses.isEmpty()){
                    System.out.println("Курс " + purch.getCourseName() + " не найден в списке курсов, запись обработана не  будет");
                    continue;
                }else course = courses.get(0);

                CriteriaQuery<Student> studentQuery = builder.createQuery(Student.class);
                Root<Student>studentRoot = studentQuery.from(Student.class);
                studentQuery.select(studentRoot).where(
                        builder.equal(studentRoot.get("name"), purch.getStudentName())
                );
                Student student;
                List<Student> students = session.createQuery(studentQuery).getResultList();
                if (students.isEmpty()){
                    System.out.println("Студент " + purch.getStudentName() + " не найден в списке студентов, запись обработана не  будет");
                    continue;
                }else student = students.get(0);

                LinkedPLKey key = new LinkedPLKey();
                key.setCourseId(course.getId());
                key.setStudentId(student.getId());
                LinkedPurchaseList lpl = new LinkedPurchaseList();
                lpl.setId(key);
                lpl.setStudent(student);
                lpl.setCourse(course);
                session.persist(lpl);
            }
            tx.commit();
        }catch (Exception e){
            tx.rollback();
            System.out.println("Возникла ошибка при выполнении кода: " + e.getClass().getName() +
                    "\nСообщение: " + e.getMessage());
            System.out.println("Транзакция не выполнена");
        }finally {
            session.close();
            sessionFactory.close();
        }
    }
}
