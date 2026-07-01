package com.arca.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.arca.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeIT extends AbstractPostgresIT {

    @Autowired
    MockMvc mvc;

    @Test
    void healthIsPublicAndReportsUp() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedResourceRequiresAuth() throws Exception {
        mvc.perform(get("/api/expenses")).andExpect(status().isUnauthorized());
    }

    @Test
    void demoLoginIssuesToken() throws Exception {
        String body = mvc.perform(post("/api/auth/demo/MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.role").value("MANAGER"))
                .andReturn().getResponse().getContentAsString();
        assertThat(body).contains("token");
    }

    @Test
    void adminOnlyEndpointForbiddenForManager() throws Exception {
        String token = tokenFor("MANAGER");
        mvc.perform(get("/api/ledger/trial-balance").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadTrialBalanceAndItBalances() throws Exception {
        String token = tokenFor("ADMIN");
        mvc.perform(get("/api/ledger/trial-balance").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0));
    }

    private String tokenFor(String role) throws Exception {
        String body = mvc.perform(post("/api/auth/demo/" + role))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        int start = body.indexOf("\"token\":\"") + 9;
        int end = body.indexOf('"', start);
        return body.substring(start, end);
    }
}
