package exam.file.code.security;

import exam.file.code.Entity.Users;
import exam.file.code.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Users user =
        usersRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "Utilisateur introuvable avec l'email : " + email));

    String role = user.getClass().getSimpleName().toUpperCase();

    return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(new SimpleGrantedAuthority("ROLE_" + role))
        .build();
  }
}
