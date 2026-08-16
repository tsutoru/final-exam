package exam.file.code.repository;

import exam.file.code.Entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {

  Optional<Users> findByEmail(String email);
}
