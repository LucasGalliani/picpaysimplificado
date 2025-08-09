package com.picpaysimplificado.dto;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email,
                      String password, UserType userType) {

    public UserDTO(User newUser) {
        this(
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getDocument(),
                newUser.getBalance(),
                newUser.getEmail(),
                newUser.getPassword(),
                newUser.getType()
        );
    }
}
