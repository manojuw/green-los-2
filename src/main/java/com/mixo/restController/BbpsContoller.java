package com.mixo.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mixo.dto.EBillDto;
import com.mixo.service.BbpsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bbps")
@Slf4j
public class BbpsContoller {

	@Autowired
	BbpsService bbpsService;

	@PostMapping("/Electricity")
	public ResponseEntity<?> category() {
		return bbpsService.category();
	}

	@PostMapping("/E/billerId/{billID}")
	public ResponseEntity<?> billerId(@PathVariable("billID") String billID) {
		return bbpsService.billerId(billID);
	}

	@PostMapping("/E/fatchBill")
	public String sendBillRequest(@RequestBody EBillDto dto) {
		return bbpsService.fatchBill(dto);
	}
	
	@PostMapping("/E/fatchBill2")
	public String sendBillRequest2(@RequestBody EBillDto dto) {
		return bbpsService.fatchBill2(dto);
	}

}
