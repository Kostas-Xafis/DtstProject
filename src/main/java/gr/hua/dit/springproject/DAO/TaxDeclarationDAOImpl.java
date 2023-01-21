package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.TaxDeclaration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TaxDeclarationDAOImpl implements TaxDeclarationDAO{

    @Autowired
    private EntityManager entityManager;


    @Override
    @Transactional
    public List<TaxDeclaration> getAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from TaxDeclaration", TaxDeclaration.class);
        return (List<TaxDeclaration>) query.getResultList();
    }

    @Override
    @Transactional
    public TaxDeclaration findById(Long id) {
        return entityManager.find(TaxDeclaration.class, id);
    }

    @Override
    public TaxDeclaration findByEstateId(Long id) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from TaxDeclaration as td where td.real_estate.id=:id", TaxDeclaration.class);
        query.setParameter("id", id);
        return (TaxDeclaration) query.getSingleResult();
    }

    @Override
    @Transactional
    public Long save(TaxDeclaration taxDeclaration) {
        return entityManager.merge(taxDeclaration).getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaxDeclaration td = entityManager.find(TaxDeclaration.class, id.intValue());
        entityManager.remove(td);
    }
}
