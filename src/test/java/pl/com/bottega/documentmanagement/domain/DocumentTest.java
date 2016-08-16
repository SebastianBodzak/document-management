package pl.com.bottega.documentmanagement.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static pl.com.bottega.documentmanagement.domain.DocumentStatus.DRAFT;

/**
 * Created by Dell on 2016-07-31.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentTest {

    private final static Long EPS = 2L * 1000L; //epsilon
    private String anyContent = "test content";
    private String anyTitle = "test title";
    private String newTitle = "new title";
    private String newContent = "new content";
    private Document document;

    @Mock
    private DocumentNumber anyNumber;

    @Mock
    private Employee anyEmployee;

    @Mock
    private Employee anyVerificator;

    @Before
    public void setUp() {
        document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);
    }

    @Test
    public void shouldCreateDocumentWithInitialState() {
        //then
        assertEquals(anyNumber, document.getNumber());
        assertEquals(anyContent, document.getContent());
        assertEquals(anyTitle, document.getTitle());
        assertEquals(anyEmployee, document.getCreator());
        assertFalse(document.getDeleted());
        assertEquals(DRAFT, document.getDocumentStatus());
    }

    @Test
    public void shouldVerifiedDocument() {
        //when
        document.verify(anyVerificator);

        //then
        assertEquals(anyVerificator, document.getVerificator());
        assertEquals(DocumentStatus.VERIFIED, document.getDocumentStatus());
        assertNotNull(document.getCreateAt());
        assertTrue(Math.abs(new Date().getTime() - document.getVerifiedAt().getTime()) < EPS);
    }

    @Test
    public void shouldRequireVerificator() {
        //when
        try {
            document.verify(null);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");

        //then
    }

    @Test
    public void shouldEditDocument() {
        document.change(newTitle, newContent);

        assertEquals(newTitle, document.getTitle());
        assertEquals(newContent, document.getContent());
        assertTrue(Math.abs(new Date().getTime() - document.getUpdatedAt().getTime()) < EPS);
        assertTrue(document.getDocumentStatus() == DRAFT);
    }

    @Test
    public void shouldEditDocumentsTitle() {
        document.change(newTitle, null);

        assertEquals(newTitle, document.getTitle());
        assertNull(document.getContent());
        assertTrue(Math.abs(new Date().getTime() - document.getUpdatedAt().getTime()) < EPS);
        assertTrue(document.getDocumentStatus() == DRAFT);
    }

    @Test
    public void shouldEditDocumentsContent() {
        document.change(null, newContent);

        assertNull(document.getTitle());
        assertEquals(newContent, document.getContent());
        assertTrue(Math.abs(new Date().getTime() - document.getUpdatedAt().getTime()) < EPS);
        assertTrue(document.getDocumentStatus() == DRAFT);
    }

    @Test
    public void shouldEditDocumentAfterVerification() {
        document.verify(anyEmployee);

        document.change(newTitle, newContent);

        assertEquals(newTitle, document.getTitle());
        assertEquals(newContent, document.getContent());
        assertTrue(Math.abs(new Date().getTime() - document.getUpdatedAt().getTime()) < EPS);
        assertTrue(document.getDocumentStatus() == DRAFT);
    }

    @Test
    public void shouldRequireTitleOrContentForEdit() {
        try {
            document.change(null, null);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");

        //then
    }

    @Test
    public void shouldDeleteDocument() {
        document.delete(anyEmployee);

        assertTrue(document.getDeleted());
        assertNotNull(document.getDeletedBy());
    }

    @Test
    public void shouldRequireDeletor() {
        try {
            document.delete(null);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");
    }
}
