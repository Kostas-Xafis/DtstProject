package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Config.AuthTokenFilter;
import gr.hua.dit.springproject.Config.JwtUtils;
import gr.hua.dit.springproject.Entity.EnumRole;
import gr.hua.dit.springproject.Entity.Role;
import gr.hua.dit.springproject.Entity.User;
import gr.hua.dit.springproject.Payload.Request.LoginRequest;
import gr.hua.dit.springproject.Payload.Request.SignupRequest;
import gr.hua.dit.springproject.Payload.Response.JwtResponse;
import gr.hua.dit.springproject.Payload.Response.MessageResponse;
import gr.hua.dit.springproject.Repository.RoleRepository;
import gr.hua.dit.springproject.Repository.UserRepository;
import gr.hua.dit.springproject.Service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${app.adminSecret}")
    private String adminSecret;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Response.Body(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        ResponseEntity<?> exists = checkIfExists(signUpRequest);
        if(exists != null) return exists;
        createUser(signUpRequest, EnumRole.ROLE_USER);
        return Response.Ok("User registered successfully!");
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        ResponseEntity<?> exists = checkIfExists(signUpRequest);
        if(exists != null) return exists;
        if(signUpRequest.getAdminPassword().isEmpty() || !signUpRequest.getAdminPassword().equals(adminSecret)) {
            return Response.UnauthorizedAccess("Incorrect password: Authorization denied");
        }
        createUser(signUpRequest, EnumRole.ROLE_ADMIN);

        return Response.Ok("User registered successfully!");
    }

    private ResponseEntity<?> checkIfExists(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        return null;
    }

    private void createUser(SignupRequest signUpRequest, EnumRole role) {
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail());
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
        user.setRoles(roles);
        userRepository.save(user);
    }
}

