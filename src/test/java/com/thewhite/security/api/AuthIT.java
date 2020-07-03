package com.thewhite.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest;
import com.jupiter.tools.spring.test.web.annotation.EnableRestTest;
import com.thewhite.util.test.matcher.CustomAssertion;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.annotation.PostConstruct;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Konstantin Khudin
 */
@EnableRestTest
@EnablePostgresIntegrationTest
public class AuthIT {

    protected final String tokenEndpoint = "/oauth/token";

    @Value("demo")
    protected String clientId;

    @Value("demo")
    protected String clientSecret;

    @Value("web")
    private String scope;

    @Value("user")
    protected String name;

    @Value("password")
    protected String password;

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper mapper = new ObjectMapper();

    protected String authHeader;

    @PostConstruct
    public void initialize() {
        authHeader = Base64.encodeBase64String(String.join(":", clientId, clientSecret).getBytes());
    }

    @Test
    void testGetAssessTokenWithExitingUserAndReturnRefreshToken() throws Exception {

        OAuth2AccessToken response = verifyAuthorization(name, password);
        // Проверяем получение refresh_token
        MvcResult result = mockMvc.perform(post(tokenEndpoint)
                                                   .param("client_id", clientId)
                                                   .param("client_secret", clientSecret)
                                                   .param("refresh_token", response.getRefreshToken().getValue())
                                                   .param("grant_type", "refresh_token")
                                                   .param("scope", scope)
                                                   .header("Authorization", "Basic " + authHeader))

                                  .andExpect(status().isOk())
                                  .andReturn();

        response = mapper.readValue(result.getResponse().getContentAsString(), OAuth2AccessToken.class);

        CustomAssertion.assertThat(response)
                       .lazyMatch(OAuth2AccessToken::getValue, Matchers.notNullValue())
                       .lazyMatch(OAuth2AccessToken::getRefreshToken, Matchers.notNullValue())
                       .lazyMatch(OAuth2AccessToken::getScope, Matchers.contains(scope))
                       .lazyMatch(OAuth2AccessToken::getExpiresIn, Matchers.notNullValue())
                       .lazyCheck(OAuth2AccessToken::getTokenType, "bearer")
                       .check();
    }

    protected OAuth2AccessToken verifyAuthorization(String name, String password) throws Exception {

        //Act
        MvcResult result = mockMvc.perform(post(tokenEndpoint)
                                                   .param("client_id", clientId)
                                                   .param("client_secret", clientSecret)
                                                   .param("grant_type", "password")
                                                   .param("username", name)
                                                   .param("password", password)
                                                   .param("scope", scope)
                                                   .header("Authorization", "Basic " + authHeader))
                                  .andExpect(status().isOk())
                                  .andReturn();

        OAuth2AccessToken response = mapper.readValue(result.getResponse().getContentAsString(), OAuth2AccessToken.class);

        //Assert
        CustomAssertion.assertThat(response)
                       .lazyMatch(OAuth2AccessToken::getValue, Matchers.notNullValue())
                       .lazyMatch(OAuth2AccessToken::getRefreshToken, Matchers.notNullValue())
                       .lazyMatch(OAuth2AccessToken::getExpiresIn, Matchers.notNullValue())
                       .lazyMatch(OAuth2AccessToken::getScope, Matchers.contains(scope))
                       .lazyCheck(OAuth2AccessToken::getTokenType, "bearer")
                       .check();

        return response;
    }
}