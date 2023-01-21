package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.User;

import java.util.List;

public interface UserDAO {
    List<User> getAll();
    void save(User user);
    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);
    void delete(Long id);
    List<String> getAuthorities(Long id);
}
