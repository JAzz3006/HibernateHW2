package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    static {
        try
        {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();
            Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
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
            }else System.out.println("неизвестна");
        }
    }

    public static Session getSession(){
        return sessionFactory != null ? sessionFactory.openSession() : null;
    }

    public static void shutDown(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }

}
