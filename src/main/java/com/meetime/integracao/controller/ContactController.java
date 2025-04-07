package com.meetime.integracao.controller;

import com.meetime.integracao.dto.ContactDTO;
import com.meetime.integracao.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public String createContact(@Valid @RequestBody ContactDTO contactData){
        return contactService.createContact(contactData.toMap());

    }

    @GetMapping
    public Map<String, Object> getAllContacts(){
        return contactService.getAllContacts();
    }

    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody List<Map<String, Object>> webhookData) {
        if (webhookData == null || webhookData.isEmpty()) {
            System.out.println("Webhook recebido sem eventos.");
            return "Nenhum evento para processar.";
        }

        for (Map<String, Object> event : webhookData) {
            Long objectId = Long.valueOf(event.get("objectId").toString());
            String eventType = event.get("subscriptionType").toString();

            System.out.printf("Evento recebido - Tipo: %s, Objeto ID: %d%n", eventType, objectId);
        }

        return "Todos os eventos foram processados com sucesso!";
    }
}
