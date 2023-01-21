package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
import gr.hua.dit.springproject.DAO.PaymentDAOImpl;
import gr.hua.dit.springproject.DAO.RealEstateDAOImpl;
import gr.hua.dit.springproject.DAO.TaxDeclarationDAOImpl;
import gr.hua.dit.springproject.DAO.UserDAOImpl;
import gr.hua.dit.springproject.Entity.Payment;
import gr.hua.dit.springproject.Entity.TaxDeclaration;
import gr.hua.dit.springproject.Entity.User;
import gr.hua.dit.springproject.Payload.Response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/tax_declaration")
public class TaxDeclarationController {

    @Autowired
    UserDAOImpl userDAOImpl;

    @Autowired
    TaxDeclarationDAOImpl taxDeclarationDAOImpl;

    @Autowired
    RealEstateDAOImpl realEstateDAOImpl;

    @Autowired
    PaymentDAOImpl paymentDAOImpl;

    @Autowired
    AuthTokenFilter authTokenFilter;


    @Secured("ROLE_ADMIN")
    @GetMapping()
    public List<TaxDeclaration> getAllTaxDeclarations() {
        return taxDeclarationDAOImpl.getAll();
    }

    @GetMapping("/{id}")
    public TaxDeclaration postTaxDeclaration(@PathVariable Long id) {
        return taxDeclarationDAOImpl.findById(id);
    }

    @Secured("ROLE_USER")
    @PostMapping("/assign_notary")
    public ResponseEntity<MessageResponse> assignNotary(@Valid @RequestHeader HashMap<String, String> request,
                                       @Valid @RequestBody   HashMap<String, Object> body) throws Exception {
        Long estate_id = getLongFromObject(body.get("real_estate_id"));
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user == null) throw new Exception("Unauthorized Access");
        User notary = userDAOImpl.findByEmail((String) body.get("notary_email"));
        if(notary == null || user.getId().equals(notary.getId())) throw new Exception("Bad Request"); //If notary is not found or user assigns himself
        TaxDeclaration td = taxDeclarationDAOImpl.findByEstateId(estate_id);
        if(td == null) throw new Exception("Bad Request");

        if(user.hasRealEstate(estate_id)){ // User is seller
            if(td.getNotary1() != null) throw new Exception("Bad Request");
            td.setNotary1(notary);
            user.getSellerNotaryList().add(td);
        } else { // User is buyer
            if(td.getNotary2() != null) throw new Exception("Bad Request");
            td.setBuyer(user);
            td.setNotary2(notary);
            user.getBuyerNotaryList().add(td);
            user.getBuyerTaxDeclarationList().add(td);
        }
        taxDeclarationDAOImpl.save(td);
        return ResponseEntity.ok(new MessageResponse("Assigned notary successfully"));
    }

    @Secured("ROLE_USER")
    @PostMapping("/set_declaration")
    public ResponseEntity<MessageResponse> setStatement(@Valid @RequestHeader HashMap<String, String> request,
                             @Valid @RequestBody   HashMap<String, Object> body) throws Exception {
        TaxDeclaration td = taxDeclarationDAOImpl.findById(getLongFromObject(body.get("tax_declaration_id")));
        User user = authTokenFilter.getUserFromRequestAuth(request);
        Long sellerNotary_id = td.getNotary1().getId();
        Long buyerNotary_id  = td.getNotary2().getId();
        if(td == null || user == null || sellerNotary_id == null || buyerNotary_id == null
                //If the user is not a notary return
                || (!sellerNotary_id.equals(user.getId())
                && !buyerNotary_id.equals(user.getId()))) throw new Exception("Unauthorized Access");

        td.setDeclaration_statement((String) body.get("statement"));
        if(sellerNotary_id.equals(user.getId())) {
            Payment payment = new Payment(0L, td.getBuyer(), td, ((Number) body.get("payment_amount")).intValue(), false);
            payment.setId(paymentDAOImpl.save(payment));
            td.setPayment(payment);
        }
        taxDeclarationDAOImpl.save(td);
        return ResponseEntity.ok(new MessageResponse("Posted declaration successfully"));
    }

    @Secured("ROLE_USER")
    @PostMapping("/accept_declaration")
    public ResponseEntity<MessageResponse> acceptDeclaration(@Valid @RequestHeader HashMap<String, String> request,
                                  @Valid @RequestBody   HashMap<String, Long>   body) throws Exception {
        TaxDeclaration td = taxDeclarationDAOImpl.findById(body.get("tax_declaration_id"));
        if(td == null || td.getDeclaration_statement() == null) throw new Exception("Bad Request");;
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user == null) throw new Exception("Unauthorized Access");

        Long seller_id = td.getSeller().getId();
        Long buyer_id = td.getBuyer().getId();
        if(buyer_id == null // If no buyer is assigned or the user is not the seller or buyer
                || (!seller_id.equals(user.getId())
                && !buyer_id.equals(user.getId()))) throw new Exception("Bad Request");

        boolean isSeller = seller_id.equals(user.getId());
        int currentAccept = td.getAccepted();

        // If seller or buyer have already accepted
        if(currentAccept == 1 && !isSeller) throw new Exception("Bad Request");
        if(currentAccept == 2 && isSeller) throw new Exception("Bad Request");

        td.setAccepted((int) (currentAccept + Math.pow(2, isSeller ? 1 : 0)));
        taxDeclarationDAOImpl.save(td);
        return ResponseEntity.ok(new MessageResponse("Accepted declaration successfully"));
    }

    @Secured("ROLE_USER")
    @PostMapping("/update_payment")
    public ResponseEntity<MessageResponse> uploadPayment(@Valid @RequestHeader HashMap<String, String> request,
                              @Valid @RequestBody   HashMap<String, Long>   body) throws Exception {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        TaxDeclaration td = taxDeclarationDAOImpl.findById(getLongFromObject(body.get("tax_declaration_id")));
        Payment payment = td.getPayment();
        if(td == null || user == null || payment == null || !user.getId().equals(payment.getPayer().getId())) throw new Exception("Unauthorized Access");

        payment.setPayed(true);
        td.setCompleted(true);
        paymentDAOImpl.save(payment);
        taxDeclarationDAOImpl.save(td);
        realEstateDAOImpl.delete(td.getReal_estate().getId()); //Remove real estate from being available for purchase
        return ResponseEntity.ok(new MessageResponse("Updated payment successfully"));
    }

    private Long getLongFromObject(Object obj) {
        return ((Number) obj).longValue();
    }
}
