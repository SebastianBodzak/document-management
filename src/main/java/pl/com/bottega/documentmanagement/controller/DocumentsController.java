package pl.com.bottega.documentmanagement.controller;

import org.springframework.web.bind.annotation.*;
import pl.com.bottega.documentmanagement.api.*;
import pl.com.bottega.documentmanagement.domain.Document;
import pl.com.bottega.documentmanagement.domain.DocumentNumber;

/**
 * Created by Dell on 2016-07-03.
 */
@RestController
@RequestMapping("/documents")
public class DocumentsController {

    private DocumentFlowProcess documentFlowProcess;
    private DocumentsCatalog documentsCatalog;

    public DocumentsController(DocumentFlowProcess documentFlowProcess, DocumentsCatalog documentsCatalog) {
        this.documentFlowProcess = documentFlowProcess;
        this.documentsCatalog = documentsCatalog;
    }

    @PutMapping
    public DocumentNumber create(@RequestBody DocumentRequest documentRequest) {
        return documentFlowProcess.create(documentRequest.getTitle(), documentRequest.getContent());
    }

    @PostMapping ("/{documentNumber}")
    public void change(@PathVariable String documentNumber, @RequestBody DocumentRequest documentRequest) {
        documentFlowProcess.change(new DocumentNumber(documentNumber), documentRequest.getTitle(), documentRequest.getContent());
    }

    @GetMapping("/{documentNumber}")
    public DocumentDto show(@PathVariable String documentNumber) {
        return documentsCatalog.get(new DocumentNumber(documentNumber));
    }

    // /documents?query=hospital&createdBy=40&verifiedBy=76&...&...
    @GetMapping
    public Iterable<DocumentDto> index(DocumentCriteria documentCriteria) {
        return documentsCatalog.find(documentCriteria);
    }
}
