package com.mixo.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.mixo.model.Borrower;
import com.mixo.model.Customer;

public interface ReportService {

	Page<Borrower> getPaginatedBorrowerAdmin(int page, int size, String fromDate, String toDate, String status,
			String uid);

	Map<String, Object> getMoreInfo(String borrowerUid);

	Page<Customer> getPaginatedCustomerAdmin(int page, int size, String fromDate, String toDate, String uid);

	List<Borrower> getTransactionsByDateAndStatus(String fromDate, String toDate);

}
