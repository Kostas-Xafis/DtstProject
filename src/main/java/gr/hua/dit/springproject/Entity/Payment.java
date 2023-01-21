package gr.hua.dit.springproject.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name="Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payment_id")
    private Long id;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name="Payer_id")
    private User payer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="Tax_id")
    @JsonBackReference(value="Payment")
    private TaxDeclaration taxDeclaration;

    @Column(name="Amount")
    private Integer amount;

    @Column(name="Payed")
    private Boolean payed;

    public Payment() {}

    public Payment(Long id, User payer, TaxDeclaration taxDeclaration, Integer amount, Boolean payed) {
        this.id = id;
        this.payer = payer;
        this.taxDeclaration = taxDeclaration;
        this.amount = amount;
        this.payed = payed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaxDeclaration getTaxDeclaration() {
        return taxDeclaration;
    }

    public void setTaxDeclaration(TaxDeclaration taxDeclaration) {
        this.taxDeclaration = taxDeclaration;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", payer=" + payer +
                ", taxDeclaration=" + taxDeclaration +
                ", amount=" + amount +
                ", payed=" + payed +
                '}';
    }
}
