package pl.com.bottega.documentmanagement.infrastructure;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.documentmanagement.domain.Document;
import pl.com.bottega.documentmanagement.domain.DocumentNumber;
import pl.com.bottega.documentmanagement.domain.repositories.DocumentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Dell on 2016-06-29.
 */
@Repository
//@Transactional
public class JPADocumentRepository implements DocumentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Document document) {
        entityManager.merge(document);
    }

    @Override
    public Document load(DocumentNumber documentNumber) {
        return entityManager.
                createQuery("FROM Document " +
                        "WHERE number=:number", Document.class).
                setParameter("number", documentNumber).getSingleResult();
    }
}