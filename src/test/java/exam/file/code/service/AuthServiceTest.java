package exam.file.code.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import exam.file.code.Entity.Student;
import exam.file.code.Entity.Users;
import exam.file.code.dto.CreateUserRequest;
import exam.file.code.dto.LoginRequest;
import exam.file.code.dto.LoginResponse;
import exam.file.code.dto.RegisterRequest;
import exam.file.code.dto.RegisterResponse;
import exam.file.code.dto.UserResponse;
import exam.file.code.repository.UsersRepository;
import exam.file.code.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;
  @Mock private UsersRepository usersRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private Authentication authentication;

  @InjectMocks private AuthService authService;



  @Test
  void login_should_return_a_token_on_success() {
    LoginRequest request = new LoginRequest();
    request.setEmail("student@school.com");
    request.setPassword("password123");

    UserDetails userDetails = User.withUsername("student@school.com").password("x").authorities("ROLE_STUDENT").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtService.generateToken(userDetails)).thenReturn("fake-jwt-token");

    LoginResponse response = authService.login(request);

    assertThat(response.getToken()).isEqualTo("fake-jwt-token");
  }



  @Test
  void register_should_create_a_student_when_email_is_free() {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("john");
    request.setEmail("john@school.com");
    request.setPassword("secret");
    request.setRole("STUDENT");

    when(usersRepository.findByEmail("john@school.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("hashed-password");

    RegisterResponse response = authService.register(request);

    assertThat(response.getMessage()).containsIgnoringCase("créé");
    verify(usersRepository).save(org.mockito.ArgumentMatchers.isA(Student.class));
  }

  @Test
  void register_should_throw_when_email_already_used() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("existing@school.com");
    request.setRole("STUDENT");

    when(usersRepository.findByEmail("existing@school.com"))
        .thenReturn(Optional.of(new Student()));

    assertThatThrownBy(() -> authService.register(request)).isInstanceOf(RuntimeException.class);

    verify(usersRepository, never()).save(any());
  }

  @Test
  void register_should_reject_admin_role() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("wannabe-admin@school.com");
    request.setRole("ADMIN");

    when(usersRepository.findByEmail("wannabe-admin@school.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("ADMIN");

    verify(usersRepository, never()).save(any());
  }

  @Test
  void register_should_reject_unknown_role() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("someone@school.com");
    request.setRole("SUPERVISOR");

    when(usersRepository.findByEmail("someone@school.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.register(request)).isInstanceOf(RuntimeException.class);
  }



  @Test
  void createUser_should_allow_creating_an_admin() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("root");
    request.setEmail("root@school.com");
    request.setPassword("secret");
    request.setRole("ADMIN");

    when(usersRepository.findByEmail("root@school.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("hashed-password");
    when(usersRepository.save(any(Users.class)))
        .thenAnswer(
            invocation -> {
              Users u = invocation.getArgument(0);
              u.setId("generated-id");
              return u;
            });

    UserResponse response = authService.createUser(request);

    assertThat(response.getId()).isEqualTo("generated-id");
    assertThat(response.getRole()).isEqualTo("ADMIN");
  }

  @Test
  void createUser_should_throw_when_email_already_used() {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail("dup@school.com");
    request.setRole("TEACHER");

    when(usersRepository.findByEmail("dup@school.com")).thenReturn(Optional.of(new Student()));

    assertThatThrownBy(() -> authService.createUser(request)).isInstanceOf(RuntimeException.class);
  }
}
