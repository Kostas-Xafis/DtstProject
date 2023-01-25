package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {

    @Autowired
    EntityManager entityManager;

    @Autowired
    RealEstateDAOImpl realEstateDAO;

    @Autowired
    TaxDeclarationDAOImpl taxDeclarationDAO;


    @Override
    @Transactional
    public List<User> getAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User", User.class);
        return (List<User>) query.getResultList();
    }

    @Override
    @Transactional()
    public void save(User user) {
        entityManager.merge(user);
    }

    @Override
    @Transactional()
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    @Transactional()
    public User findByEmail(String email) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User as u where u.email = :email", User.class);
        query.setParameter("email", email);
        return (User) query.getResultList().get(0);
    }

    @Override
    @Transactional()
    public User findByUsername(String username) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User as u  where u.username = :username", User.class);
        query.setParameter("username", username);
        return (User) query.getResultList().get(0);
    }

    @Override
    @Transactional()
    public void delete(Long id) {
        User user = entityManager.find(User.class, id);
        delete(user);
    }

    @Override
    @Transactional()
    public void delete(User user) {
        user.getRealEstateList().parallelStream().forEach(realEstateDAO::delete);
        user.getAllTaxes().parallelStream().forEach(taxDeclarationDAO::reset);
        entityManager.remove(user);
    }

    @Override
    @Transactional()
    public List<String> getAuthorities(Long id) {
        return entityManager.find(User.class, id)
                .getRoles().stream().map(role -> role.getName().name()).toList();
    }
}
