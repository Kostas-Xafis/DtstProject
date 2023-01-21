package gr.hua.dit.springproject.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String authMessage = authException.getMessage();
        logger.error("Unauthorized error: {}", authMessage);
        if (authMessage.equals("Unauthorized Access")) response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        else response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: Bad Request");
    }


}
