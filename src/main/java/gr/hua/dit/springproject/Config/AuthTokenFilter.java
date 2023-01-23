package gr.hua.dit.springproject.Config;

import gr.hua.dit.springproject.DAO.UserDAOImpl;
import gr.hua.dit.springproject.Entity.User;
import gr.hua.dit.springproject.Service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;


@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDAOImpl userDAOImpl;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.info(request.getMethod() + "::" + request.getRequestURL());
            String jwt = parseJwt(request);
//            User user = getUserFromJwt(jwt);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                String username = user.getUsername();
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            //Passing down the controllers the authenticated user
            //request.setAttribute("❤️IHateJava❤️", user);
            //I guess hibernate won't give me a user object inside the filterchain... oh well,
            // and then I would have the authenticated user in every single
            // request with @Request("❤️IHateJava❤️") User user " in the controller functions.
            // Time debugging because Java is shit: 75%. Time implementing architecture logic: 25%.
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (!StringUtils.hasText(headerAuth)
                || !headerAuth.startsWith("Bearer ")) return null;
        return headerAuth.substring(7);
    }

    public User getUserFromRequestAuth(HashMap<String, String> request) {
        String headerAuth = request.get("authorization");
        if (!StringUtils.hasText(headerAuth) || !headerAuth.startsWith("Bearer ")) return null;
        String jwt = headerAuth.substring(7);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) return null;
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        return userDAOImpl.findByUsername(username);
    }
}
