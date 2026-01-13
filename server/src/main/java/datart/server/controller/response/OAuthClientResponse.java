package datart.server.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthClientResponse {
    private String clientId;
    private String clientName;
    private String authorizationUri;
}
