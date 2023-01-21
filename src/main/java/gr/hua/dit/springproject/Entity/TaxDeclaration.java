package gr.hua.dit.springproject.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Tax_Declaration")
public class TaxDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Declaration_id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="Buyer_id")
    @JsonBackReference(value="buyer")
    private User buyer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="Seller_id")
    @JsonBackReference(value="seller")
    private User seller;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="SellerNotary_id")
    @JsonBackReference(value="notary1")
    private User notary1;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="BuyerNotary_id")
    @JsonBackReference(value="notary2")
    private User notary2;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="Payment_id")
    @JsonBackReference(value="Payment")
    private Payment payment;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="Real_estate_id")
    @JsonBackReference(value="RealEstateTaxId")
    private RealEstate real_estate;

    @Column(name="Declaration")
    private String declaration_statement;

    @Column(name="Accepted")
    private Integer accepted = 0;

    @Column(name="Completed")
    private Boolean completed = false;

    public TaxDeclaration() {}

    public TaxDeclaration(Long id, User seller, RealEstate real_estate) {
        this.id = id;
        this.seller = seller;
        this.real_estate = real_estate;
    }

    public TaxDeclaration(Long id, User buyer, User seller, User notary1,
                          User notary2, Payment payment, RealEstate real_estate,
                          String declaration_statement, Integer accepted, Boolean completed) {
        this.id = id;
        this.buyer = buyer;
        this.seller = seller;
        this.notary1 = notary1;
        this.notary2 = notary2;
        this.payment = payment;
        this.real_estate = real_estate;
        this.declaration_statement = declaration_statement;
        this.accepted = accepted;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public RealEstate getReal_estate() {
        return real_estate;
    }

    public void setReal_estate(RealEstate real_estate) {
        this.real_estate = real_estate;
    }

    public String getDeclaration_statement() {
        return declaration_statement;
    }

    public void setDeclaration_statement(String declaration_statement) {
        this.declaration_statement = declaration_statement;
    }

    public User getNotary1() {
        return notary1;
    }

    public void setNotary1(User notary1) {
        this.notary1 = notary1;
    }

    public User getNotary2() {
        return notary2;
    }

    public void setNotary2(User notary2) {
        this.notary2 = notary2;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Integer getAccepted() {
        return accepted;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "TaxDeclaration{" +
                "id=" + id +
                ", buyer=" + buyer +
                ", seller=" + seller +
                ", notary1=" + notary1 +
                ", notary2=" + notary2 +
                ", payment=" + payment +
                ", real_estate=" + real_estate +
                ", declaration_statement='" + declaration_statement + '\'' +
                ", accepted=" + accepted +
                ", completed=" + completed +
                '}';
    }
}