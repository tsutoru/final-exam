package exam.file.code.endpoint.rest.controller;

import exam.file.code.endpoint.event.EventProducer;
import exam.file.code.endpoint.event.model.ReleveNotesRequested;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReleveController {

  private final EventProducer<ReleveNotesRequested> eventProducer;

  public ReleveController(EventProducer<ReleveNotesRequested> eventProducer) {
    this.eventProducer = eventProducer;
  }

  @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
  @PostMapping("/me/releve")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void demanderMonReleve(@RequestParam String email, Authentication authentication) {
    var event =
        ReleveNotesRequested.builder()
            .studentId(authentication.getName())
            .recipientEmail(email)
            .build();
    eventProducer.accept(List.of(event));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/admin/students/{studentId}/releve")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void envoyerReleve(@PathVariable String studentId, @RequestParam String email) {
    var event = ReleveNotesRequested.builder().studentId(studentId).recipientEmail(email).build();
    eventProducer.accept(List.of(event));
  }
}
