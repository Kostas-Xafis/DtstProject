package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.RealEstate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RealEstateDAOImpl implements RealEstateDAO{

    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public List<RealEstate> getAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from RealEstate", RealEstate.class);
        return (List<RealEstate>) query.getResultList();
    }

    @Override
    @Transactional
    public RealEstate findById(Long id) {
        return entityManager.find(RealEstate.class, id);
    }

    @Override
    @Transactional
    public Long save(RealEstate re) {
        return entityManager.merge(re).getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RealEstate re = entityManager.find(RealEstate.class, id.intValue());
        entityManager.remove(re);
    }
}
