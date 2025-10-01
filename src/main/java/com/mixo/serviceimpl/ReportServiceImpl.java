package com.mixo.serviceimpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mixo.dto.LoanStatus;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerBank;
import com.mixo.model.BorrowerCibil;
import com.mixo.model.BorrowerDoc;
import com.mixo.model.BorrowerPan;
import com.mixo.model.Customer;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerBankRepository;
import com.mixo.repository.BorrowerCibilRepository;
import com.mixo.repository.BorrowerDocRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.repository.CustomerRepository;
import com.mixo.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	BorrowerRepository borrowerRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	BorrowerBankRepository borrowerBankRepository;

	@Autowired
	BorrowerCibilRepository borrowerCibilRepository;

	@Autowired
	BorrowerDocRepository borrowerDocRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public Page<Borrower> getPaginatedBorrowerAdmin(int page, int size, String fromDate, String toDate, String status,
			String uid) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());

		if (fromDate != null && toDate != null && status != null && !status.isEmpty() && uid != null) {
			String fromDateString = fromDate + " 00:00:00";
			String toDateString = toDate + " 23:59:59";
			LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime toDates = LocalDateTime.parse(toDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LoanStatus statusEnum = LoanStatus.valueOf(status);
			return borrowerRepository.findByLenderUidAndLoanStatusAndCreatedOnBetween(uid, statusEnum, fromDates,
					toDates, pageable);

		} else if (fromDate != null && toDate != null && status != null && !status.isEmpty() && uid == null) {
			String fromDateString = fromDate + " 00:00:00";
			String toDateString = toDate + " 23:59:59";
			LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime toDates = LocalDateTime.parse(toDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LoanStatus statusEnum = LoanStatus.valueOf(status);
			return borrowerRepository.findByLoanStatusAndCreatedOnBetween(statusEnum, fromDates, toDates, pageable);
		}

		else {
			String fromDateString = fromDate + " 00:00:00";
			String toDateString = toDate + " 23:59:59";
			LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime toDates = LocalDateTime.parse(toDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			return borrowerRepository.findByCreatedOnBetween(fromDates, toDates, pageable);
		}
	}

	@Override
	public Map<String, Object> getMoreInfo(String borrowerUid) {
		Map<String, Object> map = new HashMap<>();
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
		if (borrower.isPresent()) {
			map.put("borrower", borrower);
		}

		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository
				.findByBorrowerUid(borrower.get().getCustomerLosId());
		if (borrowerAadhaar != null) {
			map.put("borrowerAadhaar", borrowerAadhaar);
		}
		BorrowerCibil borrowerCibil = borrowerCibilRepository
				.findTopByBorrowerUidOrderByCreatedOnDesc(borrower.get().getCustomerLosId());
		if (borrowerCibil != null) {
			map.put("borrowerCibil", borrowerCibil);
		}
		BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(borrower.get().getCustomerLosId());
		if (borrowerDoc != null) {
			map.put("borrowerDoc", borrowerDoc);
		}
		BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrower.get().getCustomerLosId());
		if (borrowerPan != null) {
			map.put("borrowerPan", borrowerPan);
		}
		BorrowerBank borrowerBank = borrowerBankRepository.findByBorrowerUid(borrower.get().getCustomerLosId());
		if (borrowerBank != null) {
			map.put("borrowerBank", borrowerBank);
		}

		return map;
	}

	@Override
	public Page<Customer> getPaginatedCustomerAdmin(int page, int size, String fromDate, String toDate, String uid) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		if (fromDate != null && toDate != null && !uid.isBlank()) {
			return customerRepository.findByUid(uid, pageable);

		} else if (fromDate != null && toDate != null) {
			String fromDateString = fromDate + " 00:00:00";
			String toDateString = toDate + " 23:59:59";
			LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime toDates = LocalDateTime.parse(toDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			return customerRepository.findByCreatedAtBetween(fromDates, toDates, pageable);
		}

		else {
			String fromDateString = fromDate + " 00:00:00";
			String toDateString = toDate + " 23:59:59";
			LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime toDates = LocalDateTime.parse(toDateString,
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			return customerRepository.findByCreatedAtBetween(fromDates, toDates, pageable);
		}
	}

	@Override
	public List<Borrower> getTransactionsByDateAndStatus(String fromDate, String toDate) {
		String fromDateString = fromDate + " 00:00:00";
		String toDateString = toDate + " 23:59:59";
		LocalDateTime fromDates = LocalDateTime.parse(fromDateString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime toDates = LocalDateTime.parse(toDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		return borrowerRepository.findByCreatedOnBetween(fromDates, toDates);
	}

}
