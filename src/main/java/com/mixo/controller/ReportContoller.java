package com.mixo.controller;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mixo.model.Borrower;
import com.mixo.model.Customer;
import com.mixo.service.NbfcService;
import com.mixo.service.ReportService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ReportContoller {

	@Autowired
	ReportService reportService;

	@Autowired
	NbfcService nbfcService;

	@RequestMapping("/borrowerReport")
	@PreAuthorize("hasAuthority('ADD_PRODUCT')")
	public String showTransactions(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "status", required = false, defaultValue = "") String status,
			@RequestParam(value = "uid", required = false, defaultValue = "") String uid, Model model) {
		try {

			if (fromDate == null || fromDate.isEmpty()) {
				fromDate = LocalDate.now().toString(); // default to today
			}
			if (toDate == null || toDate.isEmpty()) {
				toDate = LocalDate.now().toString(); // default to today
			}

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String userName = auth.getName();
			Page<Borrower> transactionPage = reportService.getPaginatedBorrowerAdmin(page, size, fromDate, toDate,
					status, uid);

			model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));

			// Add the bankUserDtos and bankList to the model
			model.addAttribute("transactions", transactionPage.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", transactionPage.getTotalPages());
			model.addAttribute("totalItems", transactionPage.getTotalElements());

			model.addAttribute("fromDate", fromDate);
			model.addAttribute("toDate", toDate);
			model.addAttribute("status", status);
			return "borrowerAdminReport";
		} catch (Exception e) {
			log.error(e.toString());
			return "access-Denied";
		}
	}

	@RequestMapping("/adminBorrowerReport")
	@PreAuthorize("hasAuthority('ADD_PRODUCT')")
	public String adminTxnReport(Model model) {

		return "adminTxnReport";

	}

	@GetMapping("/adminTxnDownload")
	@PreAuthorize("hasAuthority('ADD_PRODUCT')")
	public void adminTxnDownload(@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,

			Model model, HttpServletResponse response) {
		try {

			if (fromDate == null || fromDate.isEmpty()) {
				fromDate = LocalDate.now().toString(); // default to today
			}
			if (toDate == null || toDate.isEmpty()) {
				toDate = LocalDate.now().toString(); // default to today
			}

			String fileName = "Borrower_" + LocalDateTime.now() + ".csv";
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			List<Borrower> transactions = reportService.getTransactionsByDateAndStatus(fromDate, toDate);

			writeTransactionsToCsv(transactions, response.getWriter());

		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	private void writeTransactionsToCsv(List<Borrower> transactions, PrintWriter writer) {
		writer.write(
				"borrowerUid,Date,Status,Customer Id,Amount,Loan Aggrement,Lender BrandName,schemeId,fullName,emiRate,emiTime,loanType,emailId,mobileNo,esignStatus,eNachStatus\n");

		// Write transaction data
		for (Borrower txn : transactions) {
			writer.write(txn.getBorrowerUid() + "," + txn.getCreatedOn() + "," + txn.getLoanStatus() + ","
					+ txn.getCustomerLosId() + "," + txn.getLoanAmount() + "," + txn.getLoanAggrement() + ","
					+ txn.getLenderBrandName() + "," + txn.getSchemeId() + "," + txn.getFullName() + "," + "'"
					+ txn.getEmiRate() + "," + "'" + txn.getEmiTime() + "," + txn.getLoanType() + "," + txn.getEmailId()
					+ "," + txn.getMobileNo() + "," + txn.getEsignStatus() + "," + txn.getENachStatus() + "\n");
		}

	}

	@RequestMapping("/customerReport")
	@PreAuthorize("hasAuthority('ADD_PRODUCT')")
	public String customerReport(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,

			@RequestParam(value = "uid", required = false, defaultValue = "") String uid, Model model) {
		try {

			if (fromDate == null || fromDate.isEmpty()) {
				fromDate = LocalDate.now().toString(); // default to today
			}
			if (toDate == null || toDate.isEmpty()) {
				toDate = LocalDate.now().toString(); // default to today
			}

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Page<Customer> transactionPage = reportService.getPaginatedCustomerAdmin(page, size, fromDate, toDate, uid);

			model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));

			// Add the bankUserDtos and bankList to the model
			model.addAttribute("transactions", transactionPage.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", transactionPage.getTotalPages());
			model.addAttribute("totalItems", transactionPage.getTotalElements());

			model.addAttribute("fromDate", fromDate);
			model.addAttribute("toDate", toDate);
			return "customerReport";
		} catch (Exception e) {
			log.error(e.toString());
			return "access-Denied";
		}
	}

	@GetMapping("/getMoreInfo")
	@ResponseBody
	public Map<String, Object> getMoreInfo(@RequestParam("borrowerUid") String borrowerUid,
			HttpServletResponse response) {
		Map<String, Object> map = reportService.getMoreInfo(borrowerUid);
		return map;

	}

}
