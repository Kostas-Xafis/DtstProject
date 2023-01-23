package gr.hua.dit.springproject.Controller;

import gr.hua.dit.springproject.Payload.Response.MessageResponse;
import org.springframework.http.ResponseEntity;

public class Response {
    public static ResponseEntity<MessageResponse> BadRequest(String errorMessage) {
        return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
    }

    public static ResponseEntity<MessageResponse> UnauthorizedAccess(String errorMessage) {
        return ResponseEntity.status(401).body(new MessageResponse(errorMessage));
    }

    public static ResponseEntity<MessageResponse> Ok(String responseMessage) {
        return ResponseEntity.ok(new MessageResponse(responseMessage));
    }

    public static <T> ResponseEntity<T> Body(T body) {
        return ResponseEntity.ok().body(body);
    }
}
