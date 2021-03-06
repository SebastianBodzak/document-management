package pl.com.bottega.documentmanagement.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.documentmanagement.domain.*;
import pl.com.bottega.documentmanagement.domain.repositories.DocumentRepository;
import pl.com.bottega.documentmanagement.domain.repositories.EmployeeRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maciuch on 12.06.16.
 */
@Service
public class DocumentFlowProcess {

    private DocumentRepository documentRepository;
    private UserManager userManager;
    private DocumentFactory documentFactory;
    private EmployeeRepository employeeRepository;

    public DocumentFlowProcess(DocumentRepository documentRepository, UserManager userManager, DocumentFactory documentFactory, EmployeeRepository employeeRepository) {
        this.documentRepository = documentRepository;
        this.userManager = userManager;
        this.documentFactory = documentFactory;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    @RequiresAuth(roles = "EDITOR")
    public DocumentNumber create(String title, String content) {
        checkNotNull(title);
        checkNotNull(content);

        Document document = documentFactory.create(content, title);
        documentRepository.save(document);
        return document.getNumber();
    }

    @Transactional
    @RequiresAuth(roles = "EDITOR")
    public void change(DocumentNumber documentNumber, String newTitle, String newContent) {
        checkNotNull(documentNumber);
        checkNotNull(newTitle);
        checkNotNull(newContent);

        Document document = documentRepository.load(documentNumber);
        document.change(newTitle, newContent);
        documentRepository.save(document);
    }

    @Transactional
    @RequiresAuth(roles = "MANAGER")
    public void verify(DocumentNumber documentNumber) {
        checkNotNull(documentNumber);

        Document document = documentRepository.load(documentNumber);
        document.verify(userManager.currentEmployee());
        documentRepository.save(document);
    }

    @Transactional
    @RequiresAuth(roles = "MANAGER")
    public void publish(DocumentNumber documentNumber, Set<EmployeeId> ids) {
        checkNotNull(documentNumber);

        Document document = documentRepository.load(documentNumber);
        document.publish(userManager.currentEmployee(), getEmployees(ids));
        documentRepository.save(document);
    }

    private Collection<Employee> getEmployees(Set<EmployeeId> ids) {
        Collection<Employee> employees = employeeRepository.findByEmployeeIds(ids);
        ids.forEach((id) -> {
        if (!employees.stream().anyMatch((employee) -> employee.getEmployeeId().equals(id)))
            employees.add(new Employee(id));
            });
        return employees;
    }

    private Employee createDigitalExcludedEmployee(EmployeeId employeeId) {
        userManager.signup(employeeId);
        return employeeRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    @RequiresAuth(roles = "EDITOR")
    public void archive(DocumentNumber documentNumber) {
        checkNotNull(documentNumber);

        Document document = documentRepository.load(documentNumber);
        document.delete(userManager.currentEmployee());
        documentRepository.save(document);
    }

    public DocumentNumber createNewVersion(DocumentNumber documentNumber) {
        checkNotNull(documentNumber);

        return null;
    }

}
