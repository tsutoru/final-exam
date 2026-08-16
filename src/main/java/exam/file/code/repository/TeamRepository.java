package exam.file.code.repository;

import exam.file.code.Entity.Team;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, UUID> {}
