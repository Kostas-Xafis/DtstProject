package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
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
    AuthTokenFilter authTokenFilter;

    @Autowired
    PasswordEncoder encoder;

    @Secured("ROLE_ADMIN")
    @GetMapping()
    public List<User> getUsers() {
        return userDAOImpl.getAll();
    }

    @Secured("ROLE_USER")
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateUser(@Valid @RequestHeader HashMap<String, String> request,
                           @Valid @RequestBody   HashMap<String, Object> body){
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(body.containsKey("password"))
            body.put("password", (Object) encoder.encode((String) body.get("password")));
        user.update(body);
        userDAOImpl.save(user);
        return ResponseEntity.ok(new MessageResponse("Updated user successfully"));
    }

    @GetMapping("/{id}")
    public User getUser(@Valid @RequestHeader HashMap<String, String> request,
                        @Valid @PathVariable  Long id) throws Exception {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user.getId().equals(id) || user.hasRole(EnumRole.ROLE_ADMIN)) return userDAOImpl.findById(id);
        else throw new Exception("Bad Request");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@Valid @RequestHeader HashMap<String, String> request,
                           @Valid @PathVariable  Long id) throws Exception {
        User user = authTokenFilter.getUserFromRequestAuth(request);
        if(user.getId().equals(id) || user.hasRole(EnumRole.ROLE_ADMIN)) userDAOImpl.delete(id);
        else throw new Exception("Bad Request");
        return ResponseEntity.ok(new MessageResponse("Deleted user successfully"));
    }

}
