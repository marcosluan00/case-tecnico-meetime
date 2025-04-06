package com.meetime.integracao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class ContactDTO {
    @NotBlank(message="Primeiro nome é obrigatorio")
    private String firstName;

    @NotBlank(message = "Ultimo nome é obrigatorio")
    private String lastName;

    @NotBlank(message = "Email é obrigatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "properties", Map.of( // Enviar tudo dentro do campo "properties"
                        "firstname", this.firstName,
                        "lastname", this.lastName,
                        "email", this.email
                )
        );
    }
}

