package com.income.repo;

import com.income.entity.Income;
import com.income.shared.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {
    List<Income> findByUserId(Long userId);
    List<Income>findByCreatedDateBetweenAndUserId(Date from, Date to, Long userId);
    List<Income>findByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId ORDER BY i.createdDate DESC")
    List<Income> findRecentIncomeByUserId(@Param("userId") Long userId, Pageable pageable);
}
