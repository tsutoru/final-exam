package exam.file.code.repository;

import exam.file.code.Entity.Cours;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursRepository extends JpaRepository<Cours, UUID> {}
