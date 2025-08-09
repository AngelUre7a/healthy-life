package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.service.UserService;

import java.util.List;

@Controller
public class UserResolver {

    @Autowired
    private UserService userService;

    @QueryMapping
    public List<User> allUsers() {
        return userService.findAll();
    }

    @QueryMapping
    public User userById(@Argument Long id) {
        return userService.findById(id);
    }

    @QueryMapping
    public User userByEmail(@Argument String email) {
        return userService.findByEmail(email);
    }

    @QueryMapping
    public User login(@Argument String email, @Argument String password) {
        return userService.login(email, password);
    }

    @MutationMapping
    public User createUser(@Argument String name, @Argument String email, 
                          @Argument String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return userService.save(user);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument String name, 
                          @Argument String email, @Argument String password) {
        return userService.update(id, name, email, password);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userService.deleteById(id);
    }
}
