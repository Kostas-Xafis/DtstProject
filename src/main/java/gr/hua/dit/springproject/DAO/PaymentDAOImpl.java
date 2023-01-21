package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class PaymentDAOImpl implements PaymentDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public Payment findById(Long id) {
        return entityManager.find(Payment.class, id);
    }

    @Override
    @Transactional
    public Long save(Payment payment) {
        return entityManager.merge(payment).getId();
    }

    @Override
    @Transactional
    public void remove(Payment payment) {
        entityManager.remove(payment);
    }
}
