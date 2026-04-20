package com.onevour.core.services;

import com.onevour.core.repositories.entities.Role;
import com.onevour.core.repositories.entities.User;
import com.onevour.core.repositories.entities.UserRole;
import com.onevour.core.repositories.repository.RoleRepository;
import com.onevour.core.repositories.repository.UserBranchRepository;
import com.onevour.core.repositories.repository.UserRepository;
import com.onevour.core.repositories.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TransactionUserServiceImpl implements TransactionUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserBranchRepository userBranchRepository;

    String[] roleNames = {"ADMIN", "CLIENT"};

    @Override
    @Transactional
    public void createRole() {
        List<Role> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            if (roleRepository.findById(roleName).isEmpty()) {
                Role role = new Role();
                role.setRole(roleName);
                roles.add(role);
            }
        }
        roleRepository.saveAll(roles);
    }

    @Override
    @Transactional
    public void createUser(String username) {
        User user = new User();
        user.setUsername(username);
        userRepository.save(user);
        List<UserRole> userRoles = new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoles.add(userRole);
        }
        userRoleRepository.saveAll(userRoles);

    }

    @Override
    @Transactional
    public void updateUser(String username, String name) {
        User user = userRepository.findById(username).orElseThrow();
        user.setName(name);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserRole(String username, String roleName) {
        User user = userRepository.findById(username).orElseThrow();
        for (UserRole userRole: user.getUserRoles()){
            if(roleName.equalsIgnoreCase(userRole.getRole().getRole())){
                userRoleRepository.delete(userRole);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findById(username).orElseThrow();
        userRepository.delete(user);
    }

    // @Override
    @Transactional
    public List<User> transactionUpdate() throws InterruptedException {
//        log.info("service test 1");
//        User user = new User();
//        user.setName("john doe");
//        userRepository.save(user);
//        user.setName("john doe cobra");
//        userRepository.save(user);
//        userRepository.delete(user);
//
//        log.info("service test 2");
//        UserBranch branch = new UserBranch();
//        branch.setName("john doe 1");
//        userBranchRepository.save(branch);
//        branch.setName("john doe 2");
//        userBranchRepository.save(branch);
//        userBranchRepository.delete(branch);

        log.info("service test 3");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User tmp = new User();
            tmp.setName("john doe " + i);
            users.add(tmp);
        }
        userRepository.saveAll(users);
        log.info("success");
        return users;

    }


    @Override
    @Transactional
    public List<User> transactionCreate() throws InterruptedException {
        log.info("service test 3");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            User tmp = new User();
            tmp.setName("john doe " + i);
            users.add(tmp);
        }
        userRepository.saveAll(users);
        log.info("success created");
        return users;
    }

    @Override
    @Transactional
    public List<User> transactionUpdate(List<User> users) throws InterruptedException {
        for (User tmp : users) {
            tmp.setName(tmp.getName() + "-1");
        }
        userRepository.saveAll(users);
        log.info("success updated");
        return users;
    }

    @Override
    @Transactional
    public void transactionDelete(List<User> users) throws InterruptedException {
        userRepository.deleteAll(users);
        log.info("success deleted");
    }
}
