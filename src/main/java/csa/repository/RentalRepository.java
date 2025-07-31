package csa.repository;

import csa.model.Rental;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @EntityGraph(attributePaths = {"car", "user"})
    Optional<Rental> findWithCarAndUserById(Long rentalId);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findWithCarById(Long rentalId);

    @Query("select r from Rental r where r.user.id = :userId and r.isActive = :isActive")
    Page<Rental> findAllByUserIdAndIsActive(@Param("userId") Long userId,
                                            @Param("isActive") Boolean isActive,
                                            Pageable pageable);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);
}
