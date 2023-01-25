package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
import gr.hua.dit.springproject.DAO.*;
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


    private Boolean assignedNotariesWithUser(TaxDeclaration td, User user) {
        Long sellerNotary_id = td.getNotary1().getId();
        Long buyerNotary_id = td.getNotary2().getId();
        if (td == null || user == null || (!sellerNotary_id.equals(user.getId())
                && !buyerNotary_id.equals(user.getId()))) return false;
        return true;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping()
    public List<TaxDeclarationDAOImpl.TaxDeclarationResponse> getAllTaxDeclarations() {
        return taxDeclarationDAOImpl.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxDeclarationDAOImpl.TaxDeclarationResponse> postTaxDeclaration(@PathVariable Long id) {
        return Response.Body(taxDeclarationDAOImpl.findByIdForResponse(id));
    }

    @Secured("ROLE_USER")
    @PostMapping("/assign_notary")
    public ResponseEntity<MessageResponse> assignNotary(@Valid @RequestHeader HashMap<String, String> request,
                                                        @Valid @RequestBody HashMap<String, Object> body) {
        Long tax_id = getLongFromObject(body.get("tax_declaration_id"));
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if (user == null) {
            return Response.UnauthorizedAccess("User authorization failed");
        }
        User notary = userDAOImpl.findByEmail((String) body.get("notary_email"));
        if (notary == null) {
            return Response.BadRequest("Notary not found from given email");
        }
        if (user.getId().equals(notary.getId())) {
            return Response.BadRequest("Assigned self as notary"); //If notary is not found or user assigns himself
        }

        TaxDeclaration td = taxDeclarationDAOImpl.findById(tax_id);
        if (td == null) {
            return Response.BadRequest("Tax declaration not found");
        }
        if(td.getSeller().getId().equals(notary.getId()) || td.getBuyer().getId().equals(notary.getId()) ||
            td.getNotary1().getId().equals(notary.getId()) || td.getNotary2().getId().equals(notary.getId()) ) {
            return Response.BadRequest("Given notary is already part of the process");
        }

        if (user.getId().equals(td.getSeller().getId())) { // User is seller
            td.setNotary1(notary);
            user.getSellerNotaryList().add(td);
        } else  { // User is buyer
            td.setBuyer(user);
            td.setNotary2(notary);
            user.getBuyerNotaryList().add(td);
            user.getBuyerTaxDeclarationList().add(td);
        }
        taxDeclarationDAOImpl.save(td);
        return Response.Ok("Assigned notary successfully");
    }

    @Secured("ROLE_USER")
    @PostMapping("/set_declaration")
    public ResponseEntity<MessageResponse> setStatement(@Valid @RequestHeader HashMap<String, String> request,
                                                        @Valid @RequestBody HashMap<String, Object> body) {
        TaxDeclaration td = taxDeclarationDAOImpl.findById(getLongFromObject(body.get("tax_declaration_id")));
        User user = authTokenFilter.getUserFromRequestAuth(request);

        if(!assignedNotariesWithUser(td, user)) {
            return Response.UnauthorizedAccess("Cannot access this part of the tax declaration process yet.");
        }

        Long sellerNotary_id = td.getNotary1().getId();
        td.setDeclaration_content((String) body.get("declaration_content"));
        if (sellerNotary_id.equals(user.getId())) {
            Payment payment = new Payment(0L, td.getBuyer(), td, ((Number) body.get("payment_amount")).intValue(), false);
            paymentDAOImpl.save(payment);
            td.setPayment(payment);
        }
        taxDeclarationDAOImpl.save(td);
        return Response.Ok("Posted declaration successfully");
    }

    @Secured("ROLE_USER")
    @PostMapping("/accept_declaration")
    public ResponseEntity<MessageResponse> acceptDeclaration(@Valid @RequestHeader HashMap<String, String> request,
                                                             @Valid @RequestBody HashMap<String, Object> body) {
        TaxDeclaration td = taxDeclarationDAOImpl.findById(getLongFromObject(body.get("tax_declaration_id")));
        Boolean accepted = (Boolean) body.get("acceptance");
        if (accepted == null || td == null) {
            return Response.BadRequest("Request body was not properly defined.");
        }
        if (td.getDeclaration_content() == null) {
            return Response.UnauthorizedAccess("Cannot access this part of the tax declaration process yet.");
        }

        User user = authTokenFilter.getUserFromRequestAuth(request);

        if (user == null) {
            return Response.UnauthorizedAccess("User authorization failed.");
        }

        Long seller_id = td.getSeller().getId();
        Long buyer_id = td.getBuyer().getId();

        if (buyer_id == null || (!seller_id.equals(user.getId()) && !buyer_id.equals(user.getId()))) { // If no buyer is assigned or the user is not the seller or buyer
            return Response.UnauthorizedAccess("Cannot access this part of the tax declaration process yet.");
        }

        if(!accepted) {
            td.setNotary2(null);
            td.setBuyer(null);
            td.setDeclaration_content(null);
            td.setAccepted(0);
        } else {
            boolean isSeller = seller_id.equals(user.getId());
            int currentAccept = td.getAccepted();

            // If seller or buyer have already accepted
            if ((currentAccept == 1 && isSeller) || (currentAccept == 2 && !isSeller) || currentAccept == 3) {
                return Response.BadRequest("Cannot accept the declaration again.");
            }
            currentAccept += isSeller ? 1 : 2;
            td.setAccepted(currentAccept);
        }
        taxDeclarationDAOImpl.save(td);
        return Response.Ok((accepted ? "Accepted": "Declined") + " declaration successfully.");
    }

    @Secured("ROLE_USER")
    @PostMapping("/update_payment")
    public ResponseEntity<MessageResponse> uploadPayment(@Valid @RequestHeader HashMap<String, String> request,
                                                         @Valid @RequestBody HashMap<String, Long> body) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        TaxDeclaration td = taxDeclarationDAOImpl.findById(body.get("tax_declaration_id"));
        Payment payment = td.getPayment();

        if (user == null || td == null || !user.getId().equals(payment.getPayer().getId())) {
            return Response.UnauthorizedAccess("Cannot access this part of the tax declaration process yet.");
        }

        payment.setPayed(true);
        td.setCompleted(true);
        paymentDAOImpl.save(payment);
        taxDeclarationDAOImpl.save(td);
        return Response.Ok("Updated payment successfully");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/reset")
    public ResponseEntity<MessageResponse> resetTaxDeclaration(@Valid @RequestHeader HashMap<String, String> request,
                                                               @Valid @RequestBody HashMap<String, Long> body) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        Long tax_id = body.get("tax_declaration_id");
        TaxDeclaration td = taxDeclarationDAOImpl.findById(tax_id);
        if(user == null || td == null) return Response.BadRequest("Tax with the specific id was not found");
        taxDeclarationDAOImpl.reset(td);
        return Response.Ok("Reset tax declaration successfully");
    }

    private Long getLongFromObject(Object obj) {
        return ((Number) obj).longValue();
    }

}
