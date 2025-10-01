package com.mixo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.dto.NbfcDto;
import com.mixo.model.Nbfc;
import com.mixo.service.NbfcService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NbfcContoller {

	@Autowired
	NbfcService nbfcService;

	@RequestMapping("/organisation")
	public String addNewLsp(Model model) {
		model.addAttribute("nbfc", new NbfcDto());
		return "nbfc";
	}

	@PostMapping("/submit-organisation")
	public String handleFormSubmission(@ModelAttribute NbfcDto nbfcDto, Model model) {
		// Process the DTO and files
		String message = nbfcService.saveNbfc(nbfcDto);
		model.addAttribute("message", message);
		log.info(nbfcDto.toString());
		return "nbfc";
	}

	@RequestMapping("/organisationList")
	public String nbfcList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();

		model.addAttribute("nbfcList", nbfcService.getAllNbfc(userName));
		return "nbfcList";
	}

	@RequestMapping("/editorganisation")
	public String editCard(@ModelAttribute("id") String uid, Model model) {
		Nbfc nbfc = nbfcService.getNbfcByUid(uid).get();
		model.addAttribute("nbfc", nbfc);
		return "editNbfc";
	}
	
	@PostMapping("/update-organisation")
	public String updateNbfc(@ModelAttribute NbfcDto nbfcDto, Model model) {
		// Process the DTO and files
		String message = nbfcService.updateNbfc(nbfcDto);
		model.addAttribute("message", message);
		log.info(nbfcDto.toString());
		return "nbfc";
	}

}
