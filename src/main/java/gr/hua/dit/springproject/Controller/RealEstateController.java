package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
import gr.hua.dit.springproject.DAO.RealEstateDAOImpl;
import gr.hua.dit.springproject.DAO.TaxDeclarationDAOImpl;
import gr.hua.dit.springproject.DAO.UserDAOImpl;
import gr.hua.dit.springproject.Entity.RealEstate;
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
@RequestMapping("/real_estate")
public class RealEstateController {

    @Autowired
    RealEstateDAOImpl realEstateDAOImpl;

    @Autowired
    TaxDeclarationDAOImpl taxDeclarationDAOImpl;

    @Autowired
    UserDAOImpl userDAOImpl;

    @Autowired
    AuthTokenFilter authTokenFilter;

    @GetMapping()
    public List<RealEstate> getAllRealEstates(@Valid @RequestHeader HashMap<String, String> request) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        return realEstateDAOImpl.getAllAvailable(user.getId());
    }

    @GetMapping("/{id}")
    public RealEstate getRealEstateById(@PathVariable Long id) {
        return realEstateDAOImpl.findById(id);
    }

    @Secured("ROLE_USER")
    @PostMapping()
    public ResponseEntity<ReturnID> postRealEstate(@Valid @RequestHeader HashMap<String, String> request,
                                                   @Valid @RequestBody RealEstate estate) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        estate.setId(0L);
        estate.setSeller(user);
        Long estate_id = realEstateDAOImpl.save(estate);
        estate.setId(estate_id);

        TaxDeclaration td = new TaxDeclaration(0L, user, estate);
        return Response.Body(new ReturnID(taxDeclarationDAOImpl.save(td)));
    }

    @Secured("ROLE_USER")
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateRealEstate(@Valid @RequestHeader HashMap<String, String> request,
                                 @Valid @RequestBody   HashMap<String, Object> body) {
        Long estate_id = getLongFromObject(body.get("id"));
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user == null || !user.hasRealEstate(estate_id)) return Response.UnauthorizedAccess("User authorization failed");
        RealEstate re = user.getRealEstate(estate_id);
        re.update(body);
        realEstateDAOImpl.save(re);
        return Response.Ok("Updated real estate successfully");
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{estate_id}")
    public ResponseEntity<MessageResponse> deleteRealEstate(@Valid @RequestHeader HashMap<String, String> request, @PathVariable Long estate_id) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user == null || !user.hasRealEstate(estate_id)) return Response.UnauthorizedAccess("User authorization failed");

        RealEstate re = user.getRealEstate(estate_id);
        user.getRealEstateList().remove(re);
        taxDeclarationDAOImpl.delete(taxDeclarationDAOImpl.findByEstateId(estate_id).getId());
        realEstateDAOImpl.delete(estate_id);
        return Response.Ok("Deleted real estate successfully");
    }

    private static class ReturnID {
        private Long id;

        public ReturnID(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    private Long getLongFromObject(Object obj) {
        return (Long) ((Number) obj).longValue();
    }

}
