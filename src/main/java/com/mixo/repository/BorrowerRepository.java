package com.mixo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.dto.LoanStatus;
import com.mixo.model.Borrower;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

	Optional<Borrower> findByBorrowerUid(String borrowerUid);

	Page<Borrower> findByLenderUidAndLoanStatusAndCreatedOnBetween(String uid, LoanStatus statusEnum,
			LocalDateTime fromDates, LocalDateTime toDates, PageRequest pageable);

	Page<Borrower> findByLoanStatusAndCreatedOnBetween(LoanStatus statusEnum, LocalDateTime fromDates,
			LocalDateTime toDates, PageRequest pageable);

	Page<Borrower> findByCreatedOnBetween(LocalDateTime fromDates, LocalDateTime toDates, PageRequest pageable);

	List<Borrower> findByUserUniqueIdAndLoanStatus(String userUniqueId, LoanStatus statusEnum);

	List<Borrower> findByMobileNo(String mobileNo);

	List<Borrower> findByMobileNoAndLoanStatus(String mobileNo, LoanStatus loanSuccessful);

	List<Borrower> findByLoanStatusAndCreatedOnBetween(LoanStatus loanInitiate, LocalDateTime timeBefore30Min,
			LocalDateTime now);

	Borrower findByCustomerLosId(String customerLosId);

	Borrower findByCustomerLosIdAndBorrowerUid(String customerLosId, String borrowerUid);

	List<Borrower> findByCreatedOnBetween(LocalDateTime fromDates, LocalDateTime toDates);

}
