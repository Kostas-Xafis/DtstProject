package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
import gr.hua.dit.springproject.DAO.RealEstateDAO;
import gr.hua.dit.springproject.DAO.UserDAOImpl;
import gr.hua.dit.springproject.Entity.EnumRole;
import gr.hua.dit.springproject.Entity.User;
import gr.hua.dit.springproject.Payload.Response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserDAOImpl userDAOImpl;

    @Autowired
    RealEstateDAO realEstateDAO;

    @Autowired
    AuthTokenFilter authTokenFilter;

    @Autowired
    PasswordEncoder encoder;

    @Secured("ROLE_ADMIN")
    @GetMapping()
    public List<User> getUsers() {
        return userDAOImpl.getAll();
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateUser(@Valid @RequestHeader HashMap<String, String> request,
                           @Valid @RequestBody HashMap<String, Object> body){
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(body.containsKey("password"))
            body.put("password", (Object) encoder.encode((String) body.get("password")));
        user.update(body);
        userDAOImpl.save(user);
        return Response.Ok("Updated user successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@Valid @RequestHeader HashMap<String, String> request,
                        @Valid @PathVariable  Long id) {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(!isOwnerOrAdmin(user, id)) {
            return Response.UnauthorizedAccess("Unauthorized user access");
        }
        return Response.Body(userDAOImpl.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@Valid @RequestHeader HashMap<String, String> request,
                           @Valid @PathVariable  Long id) throws Exception {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(!isOwnerOrAdmin(user, id)) {
            return Response.UnauthorizedAccess("Unauthorized user access");
        }
        if(user.getId().equals(id)) userDAOImpl.delete(user);
        else userDAOImpl.delete(id);

        return Response.Ok("Deleted user successfully");
    }

    private Boolean isOwnerOrAdmin(User user, Long id) {
        return user != null && (user.getId().equals(id) || user.hasRole(EnumRole.ROLE_ADMIN));
    }

}
