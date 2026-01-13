package datart.server.controller;

import datart.core.base.annotations.SkipLogin;
import datart.server.base.dto.ResponseData;
import datart.server.controller.response.OAuthClientResponse;
import datart.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Api
@Slf4j
@RestController
@RequestMapping(value = "/tpa")
public class ThirdPartyAuthController extends BaseController {

    private final UserService userService;

    public ThirdPartyAuthController(UserService userService) {
        this.userService = userService;
    }

    private ClientRegistrationRepository clientRegistrationRepository;

    @ApiOperation(value = "Get Oauth2 clients")
    @GetMapping(value = "getOauth2Clients", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @SkipLogin
    public ResponseData<List<OAuthClientResponse>> getOauth2Clients(HttpServletRequest request) {
        if (clientRegistrationRepository == null) {
            return ResponseData.success(Collections.emptyList());
        }
        Iterable<ClientRegistration> clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;

        List<OAuthClientResponse> clients = StreamSupport.stream(clientRegistrations.spliterator(), false)
                .map(registration -> OAuthClientResponse.builder()
                        .clientId(registration.getClientId())
                        .clientName(registration.getClientName())
                        .authorizationUri(String.format("%s/%s?redirect_url=/", OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI, registration.getRegistrationId()))
                        .build())
                .collect(Collectors.toList());

        return ResponseData.success(clients);
    }

    @Autowired(required = false)
    public void setClientRegistrationRepository(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
}
