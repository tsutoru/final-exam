package exam.file.code.repository;

import exam.file.code.Entity.CourseTeamAssignment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTeamAssignmentRepository extends JpaRepository<CourseTeamAssignment, UUID> {

  List<CourseTeamAssignment> findByCoursId(UUID coursId);

  List<CourseTeamAssignment> findByTeacherId(String teacherId);

  List<CourseTeamAssignment> findByTeamId(UUID teamId);
}
