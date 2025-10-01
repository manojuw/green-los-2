package com.mixo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.repository.EmiBreakUpRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class EmiBreakUpContoller {

	@Autowired
	EmiBreakUpRepository emiBreakUpRepository;

	@RequestMapping("/getEmiBreakUp")
	public String getEmiBreakUp(Model model) {
		return "emiBreakup";
	}

	@RequestMapping("/viewEMI")
	public String userList(Model model, @ModelAttribute("borrowerUid") String borrowerUid) {

		model.addAttribute("message", "Data Fatched");
		model.addAttribute("userList", emiBreakUpRepository.findByBorrowerUid(borrowerUid));
		return "emiBreakup";
	}

}
