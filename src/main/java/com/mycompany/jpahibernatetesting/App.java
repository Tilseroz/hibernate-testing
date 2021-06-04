package com.mycompany.jpahibernatetesting;

import entity.Book;
import entity.Movie;
import entity.Review;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.sf.ehcache.CacheManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

//PROČ TOHLE NEFUNGUJE?
//        Book book = new Book(1l, "O Bartolomeji Konvalinkovi");
//        em.persist(book);
//        
//        Review review = new Review(1l, "Vynikající knížka", book);
//        em.persist(review);

/**
 * Hello motherfuckin' world!
 *
 */
public class App 
{
    
    private static final String TILSEROZ_PERSISTENCE = "TilserozPersistence";
    
    public static void main( String[] args )
    {


        
//        workWithEntityManager();
        
        workWithSessions();
        
    }

    private static void orphanRemovalTest(EntityManager em) {
//        mažeme asociaci z parenta na child, pokud máme orphan removal, Hibernate smaže child z DB
        Book book = em.find(Book.class, 3L);
        book.getReviews().remove(0);
 
    }
    
    private static void createMovie(EntityManager em) {
        Movie movie = new Movie("O hovně");
        Movie movie2 = new Movie("O hovně 2");
        em.persist(movie);
        em.persist(movie2);
    }

    private static void createBookAndReview(EntityManager em) {
        Book book = new Book("O Bartolomeji Konvalinkovi 2");
        Review review = new Review("Vynikající knížka 3", book);
        Review review2 = new Review("Vynikající knížka 4", book);
        List<Review> reviewsList = new ArrayList<>(5);
        reviewsList.addAll(Arrays.asList(review, review2));
        book.setReviews(reviewsList);
        em.persist(book);
    }
    
    private static Long getBookId(EntityManager em) {
        Query query = em.createNativeQuery("select min(book_id) from book");
        List<Long> ids = query.getResultList();
        if (ids.isEmpty()) {
            throw new RuntimeException("VOLE MÁŠ PRÁZDNOUT DB!!!!!!!!!!!!");
        } else {
            return ids.get(0);
        }
    }
    
    private static void workWithEntityManager() {
        EntityManager em = createEntityManager();
        
        em.getTransaction().begin();
        
        createBookAndReview(em);

//        Book book = em.find(Book.class, 3L);


//        em.remove(book);
//        createBookAndReview(em);
//        createMovie(em);
//        orphanRemovalTest(em);
        
        em.getTransaction().commit();
        em.close();
        
    }

    private static void workWithSessions() {
        Movie movie;
        
        SessionFactory sf = createSessionFactory();
        
        Session session1 = sf.openSession();
        session1.beginTransaction();
        
        movie = (Movie) session1.get(Movie.class, 1L);
        System.out.println(movie);
        movie.setMovieTitle("Petrova povídka 3");
        
        session1.getTransaction().commit();
        session1.close();

        
        Session session2 = sf.openSession();
        session2.beginTransaction();
        
        movie = (Movie) session2.get(Movie.class, 1L);
        System.out.println(movie);
        
        session2.getTransaction().commit();
        session2.close();
    }

    private static SessionFactory createSessionFactory() {
        Configuration conf = new Configuration().configure().addAnnotatedClass(Movie.class);
        ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
        return conf.buildSessionFactory(sr);
    }
    
    private static EntityManager createEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(TILSEROZ_PERSISTENCE);
        return entityManagerFactory.createEntityManager();
    }

}
