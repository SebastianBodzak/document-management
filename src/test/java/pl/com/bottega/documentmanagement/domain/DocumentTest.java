package pl.com.bottega.documentmanagement.domain;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.documentmanagement.domain.events.DocumentListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.com.bottega.documentmanagement.domain.DocumentStatus.DRAFT;
import static pl.com.bottega.documentmanagement.domain.DocumentStatus.PUBLISHED;

/**
 * Created by Dell on 2016-07-31.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentTest {

    private final static Long EPS = 2L * 1000L; //epsilon
    private final String anyContent = "test content";
    private final String anyTitle = "test title";
    private final String newTitle = "new title";
    private final String newContent = "new content";
    private Document document;

    @Mock
    private DocumentNumber anyNumber;

    @Mock
    private Employee anyEmployee;

    @Mock
    private Employee anyVerificator;

    @Mock
    private Reader anyReader;

    @Mock
    private EmployeeId anyEmployeeId;

    @Mock
    private PrintingCostCalculator printingCostCalculator;

    @Mock
    private DocumentListener documentListener;

    @Mock
    private DocumentListener documentListener2;

    @Before
    public void setUp() {
        document = new Document(anyNumber, anyContent, anyTitle, anyEmployee, printingCostCalculator);
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
        try {
            document.verify(null);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");
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

//    @Test
    public void shouldPublishDocument() {
        Set<Employee> readers = new HashSet<>(Arrays.asList(anyEmployee, anyEmployee));

        document.publish(anyEmployee, readers);

        assertEquals(PUBLISHED, document.getDocumentStatus());
        assertEquals(readers, document.getReaders());
        assertEquals(anyEmployee, document.getPublishedBy());
        assertTrue(Math.abs(new Date().getTime() - document.getPublishedAt().getTime()) < EPS);
    }

//    @Test
    public void shouldNotifyAboutPublishing() {
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee, printingCostCalculator);
        document.verify(anyEmployee);
        DocumentListener firstListener = mock(DocumentListener.class);
        DocumentListener secondListener = mock(DocumentListener.class);
        document.subscribeDocumentListener(firstListener);
        document.subscribeDocumentListener(secondListener);

        document.publish(anyEmployee, Sets.newHashSet(anyEmployee));

        verify(firstListener).published(document);
        verify(secondListener).published(document);
    }

    @Test
    public void shouldRequirePublisher() {
        try {
            document.publish(null, null);
        }
        catch (NullPointerException ex) {
            return;
        }
        fail("NullPointerException excpected");
    }

//    @Test
    public void shouldConfirmDocumentReading() {
        Reader reader = new Reader(document, anyEmployee);
        Set<Employee> readers = new HashSet<>(Arrays.asList(anyEmployee, anyEmployee));
        document.publish(anyEmployee, readers);

        document.confirm(anyEmployee);

        assertTrue(reader.isConfirmed());
        assertFalse(anyReader.isConfirmed());
        assertTrue(Math.abs(new Date().getTime() - reader.getConfirmedAt().getTime()) < EPS);
        assertNull(reader.getConfirmedBy());
        assertEquals(anyEmployee, reader.getEmployee());
    }

    @Test
    public void shouldFailConfirmBecauseEmployeeIsNotAReader() {
        Employee notReader = new Employee(anyEmployeeId);
        Reader reader = new Reader(document, anyEmployee);
        Set<Employee> readers = new HashSet<>(Arrays.asList(anyEmployee, anyEmployee));
        document.publish(anyEmployee, readers);


        try {
            document.confirm(notReader);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");
    }

//    @Test
    public void shouldConfirmByOtherEmployeeDocumentReading() {
        Employee confirmatorManager = new Employee(anyEmployeeId);
        Reader reader = new Reader(document, anyEmployee);
        Set<Employee> readers = new HashSet<>(Arrays.asList(anyEmployee, anyEmployee));
        document.publish(anyEmployee, readers);

        document.confirm(confirmatorManager, anyEmployee);

        assertTrue(reader.isConfirmed());
        assertFalse(anyReader.isConfirmed());
        assertTrue(Math.abs(new Date().getTime() - reader.getConfirmedAt().getTime()) < EPS);
        assertEquals(confirmatorManager, reader.getConfirmedBy());
        assertEquals(anyEmployee, reader.getEmployee());
    }

    @Test
    public void shouldFailConfirmDocumentReadingByOtherEmployeeBecauseOtherEmployeeIsNotAReader() {
        Employee notReader = new Employee(anyEmployeeId);
        Reader reader = new Reader(document, anyEmployee);
        Set<Employee> readers = new HashSet<>(Arrays.asList(anyEmployee, anyEmployee));
        document.publish(anyEmployee, readers);

        try {
            document.confirm(anyEmployee, notReader);
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentException excpected");
    }
}
