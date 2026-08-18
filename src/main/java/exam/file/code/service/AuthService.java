package exam.file.code.service;

import exam.file.code.Entity.Admin;
import exam.file.code.Entity.Student;
import exam.file.code.Entity.Teacher;
import exam.file.code.Entity.Users;
import exam.file.code.dto.*;
import exam.file.code.repository.UsersRepository;
import exam.file.code.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  public LoginResponse login(LoginRequest request) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String token = jwtService.generateToken(userDetails);

    return new LoginResponse(token);
  }

  public RegisterResponse register(RegisterRequest request) {

    if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("Cet email existe déjà");
    }

    Users user;

    switch (request.getRole().toUpperCase()) {
      case "STUDENT":
        user = new Student();
        break;

      case "TEACHER":
        user = new Teacher();
        break;

      case "ADMIN":
        throw new RuntimeException("La création d'un ADMIN n'est pas autorisée par ce endpoint");

      default:
        throw new RuntimeException("Rôle invalide. Utilisez STUDENT ou TEACHER");
    }

    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    usersRepository.save(user);

    return new RegisterResponse("Utilisateur créé avec succès");
  }

  public UserResponse createUser(CreateUserRequest request) {

    if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("Cet email existe déjà");
    }

    Users user;

    switch (request.getRole().toUpperCase()) {
      case "STUDENT":
        user = new Student();
        break;

      case "TEACHER":
        user = new Teacher();
        break;

      case "ADMIN":
        user = new Admin();
        break;

      default:
        throw new RuntimeException("Role invalide : STUDENT, TEACHER ou ADMIN");
    }

    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    Users savedUser = usersRepository.save(user);

    return new UserResponse(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        request.getRole().toUpperCase());
  }
}
