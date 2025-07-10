package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class App{
    public static void main( String[] args ){
       Session session = null;
        try {
           session = HibernateUtil.getSession();
            if (session == null){
                System.out.println("Сессия не инициализирована. Выполнение программы остановлено");
                return;
            }

            editTeacher(session);
            getTeachers(session).forEach(System.out::println);

       } catch (Exception e) {
            System.out.println("Не удалось открыть сессию: " + e.getClass().getName() +
                    "\nСообщение: " + e.getMessage());
       }finally {
            if (session != null) session.close();
            HibernateUtil.shutDown();
        }

    }

    public static void maxEarnings(Session session){
        List<LinkedPurchaseList> purchases =
                session.createQuery("SELECT * FROM LinkedPurchaseList", LinkedPurchaseList.class)
                        .getResultList();
        Map<Integer, List<LinkedPurchaseList>> groupedMap = purchases.stream()
                .collect(Collectors.groupingBy(
                        lpl -> lpl.getCourse().getId())//,
                        //Collectors.flatMapping((lpl -> {
                          //  Map.Entry<>
                        //} )
                );


    }

    public static void addTeacher(Session session){
        Transaction transaction = null;
        try{
           transaction = session.beginTransaction();
           if (transaction == null){
               System.out.println("Не удалось создать транзакцию");
               return;
           }
           session.createNativeQuery("INSERT INTO teachers (age, salary, name) VALUES (25, 150000, 'John Doe')")
                   .executeUpdate();
           transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            System.out.println("Что-то пошло не так :(\n" + e.getMessage());
        }
    }

    public static void editTeacher(Session session){
        Transaction transaction = null;
        String userInput = "";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя учителя");
        try {
            while (true){
                userInput = scanner.nextLine();
                if (!userInput.isEmpty()){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Что-то пошло не так (1) :(\n" + e.getMessage());
        }
        try{
            transaction = session.beginTransaction();
            if (transaction == null){
                System.out.println("Транзакция не создана");
                return;
            }
            session.createQuery("UPDATE Teacher t SET name = :name WHERE id = :id")
                    .setParameter("name", userInput)
                    .setParameter("id", 51)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            System.out.println("Что-то пошло не так (2) :(\n" + e.getMessage());
        }
    }

    public static List<String> getTeachers(Session session){
        List<Teacher> teachers = session.createNativeQuery("SELECT * FROM teachers",Teacher.class).getResultList();
        return teachers.stream()
                .map(t -> String.join(" - ", String.valueOf(t.getId()), t.getName()))
                .collect(Collectors.toList());
    }

    public static void businessLogic1 (Session session){
        List<LinkedPurchaseList> linkedPurchases = session.createNativeQuery("select * from linked_purchase_list order by student_id", LinkedPurchaseList.class)
                .getResultList();
        List<String> purchInfo = linkedPurchases.stream().map(lpl -> String.join(" - ",
                String.valueOf(lpl.getCourse().getId()), lpl.getCourse().getName(), lpl.getStudent().getName()))
                .collect(Collectors.toList());
        purchInfo.forEach(System.out::println);
    }

    public static void businessLogic (Session session){
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
                HibernateUtil.shutDown();
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
        }
    }

}
