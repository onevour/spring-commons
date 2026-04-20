package com.onevour.core.services;

import com.onevour.core.repositories.entities.User;

import java.util.List;

public interface TransactionUserService {

    void createRole();

    void createUser(String username);

    void updateUser(String username, String name);

    void deleteUserRole(String username, String client);

    void deleteUser(String username);

    List<User> transactionCreate() throws InterruptedException;

    List<User> transactionUpdate(List<User> users) throws InterruptedException;

    void transactionDelete(List<User> users) throws InterruptedException;
}
