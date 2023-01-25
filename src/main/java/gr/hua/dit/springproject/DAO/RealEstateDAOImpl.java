package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.RealEstate;
import gr.hua.dit.springproject.Entity.TaxDeclaration;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RealEstateDAOImpl implements RealEstateDAO {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TaxDeclarationDAO taxDeclarationDAO;

    @Override
    @Transactional
    public List<RealEstate> getAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from RealEstate", RealEstate.class);
        return (List<RealEstate>) query.getResultList();
    }

    @Transactional
    public List<RealEstate> getAllAvailable(Long id) {
        Session session = entityManager.unwrap(Session.class);
        // !! THERE IS PROBABLY SOMETHING WRONG WITH THE QUERY...........
        Query query = session.createQuery(
                "from RealEstate as re join fetch TaxDeclaration as td on re.id = td.real_estate.id where td.buyer=null and re.seller.id!=:id", RealEstate.class);
        query.setParameter("id", id);
        return (List<RealEstate>) query.getResultList();
    }


    @Override
    @Transactional
    public RealEstate findById(Long id) {
        return entityManager.find(RealEstate.class, id);
    }

    @Override
    @Transactional
    public Long save(RealEstate realEstate) {
        Long id = entityManager.merge(realEstate).getId();
        if(realEstate.getId().equals(0L)) realEstate.setId(id);
        return id;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RealEstate re = entityManager.find(RealEstate.class, id.intValue());
        entityManager.remove(re);
    }

    @Override
    @Transactional
    public void delete(RealEstate realEstate) {
        TaxDeclaration td = realEstate.getTaxDeclaration();
        if(td != null) {
            taxDeclarationDAO.delete(td);
        }
        entityManager.remove(realEstate);
    }
}
