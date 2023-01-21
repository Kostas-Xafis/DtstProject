package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.Payment;

public interface PaymentDAO {
    Payment findById(Long id);
    Long save(Payment payment);
    void remove(Payment payment);
}
