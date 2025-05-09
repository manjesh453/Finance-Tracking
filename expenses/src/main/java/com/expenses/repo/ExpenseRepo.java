package com.expenses.repo;

import com.expenses.entity.ExpenseCategory;
import com.expenses.entity.Expenses;
import com.expenses.shared.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expenses,Long> {
    List<Expenses> findByReceiverIdAndStatus(Long receiverId, Status status);
    List<Expenses> findByCreatedDateBetweenAndReceiverId(Date from, Date to, Long receiverId);
    List<Expenses> findByCategoryAndReceiverId(ExpenseCategory categoryId, Long receiverId);
    @Query("SELECT i FROM Expenses i WHERE i.receiverId = :userId ORDER BY i.createdDate DESC")
    List<Expenses> findRecentExpensesByUserId(@Param("userId") Long userId, Pageable pageable);
}
