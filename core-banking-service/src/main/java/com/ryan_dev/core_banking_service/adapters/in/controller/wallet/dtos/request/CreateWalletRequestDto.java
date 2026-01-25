package com.ryan_dev.core_banking_service.adapters.in.controller.wallet.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWalletRequestDto(
        @NotNull(message = "Name cannot be null")
        @Size(min = 3, max = 255, message = "Name must have at least 3 characters and no more than 255 characters")
        String fullName,

        @NotNull(message = "CPF/CNPJ cannot be null")
        @Size(min = 11, max = 14, message = "CPF/CNPJ must have at least 11 characters and no more than 14 characters")
        @Pattern(regexp = "^\\d{11}$|^\\d{14}$", message = "Invalid CPF/CNPJ format")
        String cpfCnpj,

        @NotNull(message = "Email cannot be null")
        @Email(message = "Invalid email format")
        @Size(min = 3, max = 255, message = "Email must have at least 3 characters and no more than 255 characters")
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password
) {
}
