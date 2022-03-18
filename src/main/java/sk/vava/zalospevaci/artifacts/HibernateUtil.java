package sk.vava.zalospevaci.artifacts;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    static {
        try {
            Configuration cfg = new Configuration();
            cfg.setProperty("hibernate.connection.password", System.getenv("SPRING_DATASOURCE_PASSWORD"));
            cfg.setProperty("hibernate.connection.username", System.getenv("SPRING_DATASOURCE_USERNAME"));
            cfg.setProperty("hibernate.connection.url", System.getenv("SPRING_DATASOURCE_URL"));
            sessionFactory = cfg.configure()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
