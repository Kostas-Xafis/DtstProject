package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.TaxDeclaration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TaxDeclarationDAOImpl implements TaxDeclarationDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<TaxDeclarationResponse> getAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("select id, seller.id as seller_id, " +
                "buyer.id as buyer_id, notary1.id as seller_notary_id, notary2.id as buyer_notary_id," +
                " payment.id as payment_id, real_estate.id as real_estate_id, accepted, declaration_content, completed from TaxDeclaration", Object[].class);
        return ((List<Object[]>) query.getResultList()).parallelStream().map(TaxDeclarationResponse::fromObject).toList();
    }

    @Override
    @Transactional
    public TaxDeclaration findById(Long id) {
        return entityManager.find(TaxDeclaration.class, id.intValue());
    }

    @Transactional
    public TaxDeclarationResponse findByIdForResponse(Long id) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("select id, seller.id as seller_id, " +
                "buyer.id as buyer_id, notary1.id as seller_notary_id, notary2.id as buyer_notary_id," +
                " payment.id as payment_id, real_estate.id as real_estate_id, accepted, declaration_content, completed from TaxDeclaration where id=:id", Object[].class);
        query.setParameter("id", id);
        return TaxDeclarationResponse.fromObject((Object[]) query.getSingleResult());
    }

    @Override
    @Transactional
    public TaxDeclaration findByEstateId(Long id) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from TaxDeclaration as td where td.real_estate.id=:id", TaxDeclaration.class);
        query.setParameter("id", id);
        return (TaxDeclaration) query.getSingleResult();
    }

    @Override
    @Transactional
    public Long save(TaxDeclaration taxDeclaration) {
        Long id = entityManager.merge(taxDeclaration).getId();
        if(taxDeclaration.getId().equals(0L)) taxDeclaration.setId(id);
        return id;
    }

    @Override
    @Transactional
    public void reset(TaxDeclaration td) {
        td.setNotary1(null);
        td.setNotary2(null);
        td.setBuyer(null);
        td.setAccepted(0);
        td.setDeclaration_content("");
        td.setPayment(null);
        save(td);
    }

    @Override
    @Transactional
    public void delete(TaxDeclaration td) {
        entityManager.remove(td);
    }

    public static class TaxDeclarationResponse {
        private Long id;
        private Long seller_id;
        private Long buyer_id;
        private Long notary_seller_id;
        private Long notary_buyer_id;
        private Long payment_id;
        private Long real_estate_id;
        private Integer accepted;
        private String declaration_content;
        private Boolean completed;

        public TaxDeclarationResponse(){}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSeller_id() {
            return seller_id;
        }

        public void setSeller_id(Long seller_id) {
            this.seller_id = seller_id;
        }

        public Long getBuyer_id() {
            return buyer_id;
        }

        public void setBuyer_id(Long buyer_id) {
            this.buyer_id = buyer_id;
        }

        public Long getNotary_seller_id() {
            return notary_seller_id;
        }

        public void setNotary_seller_id(Long notary_seller_id) {
            this.notary_seller_id = notary_seller_id;
        }

        public Long getNotary_buyer_id() {
            return notary_buyer_id;
        }

        public void setNotary_buyer_id(Long notary_buyer_id) {
            this.notary_buyer_id = notary_buyer_id;
        }

        public Long getPayment_id() {
            return payment_id;
        }

        public void setPayment_id(Long payment_id) {
            this.payment_id = payment_id;
        }

        public Integer getAccepted() {
            return accepted;
        }

        public Long getReal_estate_id() {
            return real_estate_id;
        }

        public void setReal_estate_id(Long real_estate_id) {
            this.real_estate_id = real_estate_id;
        }

        public void setAccepted(Integer accepted) {
            this.accepted = accepted;
        }

        public String getDeclaration_content() {
            return declaration_content;
        }

        public void setDeclaration_content(String declaration_content) {
            this.declaration_content = declaration_content;
        }

        public Boolean getCompleted() {
            return completed;
        }

        public void setCompleted(Boolean completed) {
            this.completed = completed;
        }

        public static TaxDeclarationResponse fromObject(Object[] obj) {
            TaxDeclarationResponse t = new TaxDeclarationResponse();
            int count = -1;
            t.setId((Long) obj[++count]);
            t.setSeller_id((Long) obj[++count]);
            t.setBuyer_id((Long) obj[++count]);
            t.setNotary_seller_id((Long) obj[++count]);
            t.setNotary_buyer_id((Long) obj[++count]);
            t.setPayment_id((Long) obj[++count]);
            t.setReal_estate_id((Long) obj[++count]);
            t.setAccepted((Integer) obj[++count]);
            t.setDeclaration_content((String) obj[++count]);
            t.setCompleted((Boolean) obj[++count]);
            return t;
        }
    }
}
