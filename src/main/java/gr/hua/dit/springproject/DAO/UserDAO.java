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
    void delete(User user);
    List<String> getAuthorities(Long id);
}
