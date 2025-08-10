package in.yogesh.removebg.repository;

import in.yogesh.removebg.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByClerkid(String clerkid);
    boolean existsById(Long id); // ✅ Correct
}

