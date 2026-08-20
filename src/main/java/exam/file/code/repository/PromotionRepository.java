package exam.file.code.repository;

import exam.file.code.Entity.Promotion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {}
