package in.yogesh.removebg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.yogesh.removebg.entity.UserEntity;
import in.yogesh.removebg.repository.UserRepository;
import in.yogesh.removebg.response.RemoveBgResponse;
import in.yogesh.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/webhooks/clerk")
@RequiredArgsConstructor
public class ClerkWebHook {

    @Value("${clerk.webhook.secret}")
    private String clerkWebhookSecret;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> handleClerkWebhook(
            @RequestHeader("svix-id") String svixId,
            @RequestHeader("svix-timestamp") String svixTimestamp,
            @RequestHeader("svix-signature") String svixSignature,
            @RequestBody String payload) {

        RemoveBgResponse response;

        try {
            boolean isValid = verifyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);
            if (!isValid) {
                response = RemoveBgResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED)
                        .data("Invalid Webhook Signature")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
                default:
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            response = RemoveBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong: " + e.getMessage())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {

            return true;

        }

    private void handleUserCreated(JsonNode data) {
        String id = data.path("id").asText();
        String email = data.path("email_addresses").get(0).path("email_address").asText();
    }

    public void handleUserUpdated(JsonNode data) {

        String clerkId = data.path("id").asText();
        String email = data.path("email_addresses").get(0).path("email_address").asText("");
        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String photoUrl = data.path("image_url").asText("");

        UserEntity user = userRepository.findByClerkid(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found: " + clerkId));

        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhotoUrl(photoUrl);

        userRepository.save(user);

    }

    private void handleUserDeleted(JsonNode data) {
        String id = data.path("id").asText();
        userService.deleteUserByClerkId(id);
    }
}
