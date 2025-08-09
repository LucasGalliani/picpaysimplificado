package com.picpaysimplificado.service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dto.UserDTO;
import com.picpaysimplificado.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {

        if (sender.getType() == UserType.MERCHANT) {
            throw new Exception("Usuário do tipo lojista não esta autorizado a realizar transação!");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo insuficiente!");
        }
    }

    public User findUserById(Long id) throws Exception {

        return userRepository.findById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User createUser(UserDTO dto) {

        User newUser = new User(dto);
        saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}
