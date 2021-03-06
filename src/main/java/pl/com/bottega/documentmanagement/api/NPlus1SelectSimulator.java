package pl.com.bottega.documentmanagement.api;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.documentmanagement.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dell on 2016-07-30.
 */
@Service
public class NPlus1SelectSimulator {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PrintingCostCalculator printingCostCalculator;

    public NPlus1SelectSimulator() {
    }


    @Transactional
    public void insertTestData() {
        Employee employee = new Employee(randomString(), randomString(), new EmployeeId(140L));
        entityManager.persist(employee);
        for (int i = 0; i < 1000; i++) {
            Document d = new Document(new DocumentNumber(randomString()), randomString(), randomString(), employee, printingCostCalculator);
            d.tag(Sets.newHashSet(new Tag("One"), new Tag("two"), new Tag("three")));
            entityManager.persist(d);
        }
    }

    @Transactional
    public void simulate() {
        Query query = entityManager.createQuery("FROM Document d JOIN FETCH d.tags", Document.class).setMaxResults(13);
        List<Document> documents = query.getResultList();
        for (Document d : documents) {
            System.out.print(d.toString() + " ");
            for (Tag t : d.tags()) {
                System.out.println(d.tags() + " ");
            }
            System.out.println();
        }
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

//    @Transactional
//    public Document getDocument() {
//
//        return;
//    }
}
