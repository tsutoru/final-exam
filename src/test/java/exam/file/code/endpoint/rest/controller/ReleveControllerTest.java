package exam.file.code.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import exam.file.code.endpoint.event.EventProducer;
import exam.file.code.endpoint.event.model.ReleveNotesRequested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// On teste uniquement la couche web + les droits (@PreAuthorize), sans passer
// par la vraie chaîne JWT : @WithMockUser peuple directement le contexte de sécurité.
@WebMvcTest(ReleveController.class)
@Import(ReleveControllerTest.MethodSecurityTestConfig.class)
class ReleveControllerTest {

  @TestConfiguration
  @EnableMethodSecurity
  static class MethodSecurityTestConfig {}

  @Autowired private MockMvc mockMvc;

  @MockBean private EventProducer<ReleveNotesRequested> eventProducer;

  @Test
  @WithMockUser(username = "student-1", roles = "STUDENT")
  void demanderMonReleve_should_return_202_for_the_connected_student() throws Exception {
    mockMvc
        .perform(post("/me/releve").param("email", "jean@school.com").with(csrf()))
        .andExpect(status().isAccepted());

    verify(eventProducer).accept(any());
  }

  @Test
  void demanderMonReleve_should_be_rejected_when_not_authenticated() throws Exception {
    mockMvc
        .perform(post("/me/releve").param("email", "jean@school.com").with(csrf()))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void envoyerReleve_should_return_202_for_an_admin() throws Exception {
    mockMvc
        .perform(
            post("/admin/students/student-2/releve").param("email", "x@school.com").with(csrf()))
        .andExpect(status().isAccepted());

    verify(eventProducer).accept(any());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void envoyerReleve_should_be_forbidden_for_a_student() throws Exception {
    mockMvc
        .perform(
            post("/admin/students/student-2/releve").param("email", "x@school.com").with(csrf()))
        .andExpect(status().isForbidden());
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor csrf() {
    return org.springframework.security.test.web.servlet.request
        .SecurityMockMvcRequestPostProcessors.csrf();
  }
}
