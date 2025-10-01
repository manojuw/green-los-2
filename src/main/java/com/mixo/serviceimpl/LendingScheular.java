package com.mixo.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mixo.dto.LoanStatus;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.service.AllCloudApiService;
import com.mixo.service.ApiService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LendingScheular {

	@Autowired
	BorrowerRepository borrowerRepository;

	@Autowired
	AllCloudApiService allCloudApiService;

	@Autowired
	ApiService apiService;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Scheduled(cron = "0 */1 * * * *") // make it for 5 min
	public void initiateLendingScheular() {
		executeScheduledLending();
	}

	@Scheduled(cron = "0 */1 * * * *") // make it for 5 min
	public void checkAadharStatus() {
		checkAadharStatusFor30Min();
	}

	private void checkAadharStatusFor30Min() {
		List<BorrowerAadhaar> borrowerAadhaars = borrowerAadhaarRepository.findByIsVerifiedAndUpdatedOnBetween(false,
				LocalDateTime.now().minusMinutes(30), LocalDateTime.now());
		for (BorrowerAadhaar borrowerAadhaar : borrowerAadhaars) {

			try {
				apiService.checkAadharStatus(borrowerAadhaar);
			} catch (Exception e) {
				log.error(e.getMessage());
				log.error("Error while checking Aadhar Status for borrower " + borrowerAadhaar.getBorrowerUid());
				continue;
			}
		}

	}

	private void executeScheduledLending() {
		log.info("Scheduled Lending Started at " + LocalDateTime.now() + "");

		LocalDateTime timeBefore30Min = LocalDateTime.now().minusMinutes(30);
		LocalDateTime now = LocalDateTime.now();

		List<Borrower> borrowers = borrowerRepository.findByLoanStatusAndCreatedOnBetween(LoanStatus.LOAN_INITIATE,
				timeBefore30Min, now);

		log.info("Total Borrowers " + borrowers.size());
		for (Borrower borrower : borrowers) {

			log.info("Borrower Uid " + borrower.getBorrowerUid());
			allCloudApiService.callAllCloudApi(borrower);

		}

	}

}
