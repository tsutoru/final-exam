package exam.file.code.endpoint.rest.controller;

import exam.file.code.dto.NoteCreateDto;
import exam.file.code.dto.NoteDto;
import exam.file.code.dto.NoteUpdateDto;
import exam.file.code.service.NoteService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
  @GetMapping("/me/notes")
  public List<NoteDto> findMyNotes(Authentication authentication) {
    return noteService.findByStudent(authentication.getName());
  }

  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping("/cours/{coursId}/notes")
  public List<NoteDto> findByCoursForTeacher(
      @PathVariable UUID coursId, Authentication authentication) {
    return noteService.findByCoursForTeacher(coursId, authentication.getName());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/cours/{coursId}/notes")
  public List<NoteDto> findByCoursForAdmin(@PathVariable UUID coursId) {
    return noteService.findByCoursForAdmin(coursId);
  }

  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/notes")
  @ResponseStatus(HttpStatus.CREATED)
  public NoteDto create(@RequestBody NoteCreateDto dto, Authentication authentication) {
    return noteService.create(dto, authentication.getName());
  }

  // Raison obligatoire (portée par NoteUpdateDto), historisation faite dans le service.
  @PreAuthorize("hasRole('TEACHER')")
  @PutMapping("/notes/{id}")
  public NoteDto update(
      @PathVariable UUID id, @RequestBody NoteUpdateDto dto, Authentication authentication) {
    return noteService.update(id, dto, authentication.getName());
  }
}
