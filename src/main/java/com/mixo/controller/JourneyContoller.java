package com.mixo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.dto.ConfigureDto;
import com.mixo.model.JourneyEngine;
import com.mixo.model.Product;
import com.mixo.service.JourneyEngineService;
import com.mixo.service.NbfcService;

@Controller
public class JourneyContoller {

	@Autowired
	NbfcService nbfcService;
	@Autowired
	JourneyEngineService journeyEngineService;

	@RequestMapping("/journeyEngine")
	public String addNewProduct(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("journeyEngine", new JourneyEngine());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "journeyEngine";
	}

	@RequestMapping("/checkjourneyEngine")
	public String checkRule(Model model, @ModelAttribute ConfigureDto configureDto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		model.addAttribute("ruleEngine", new Product());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		model.addAttribute("message", "Data Fatched");
		model.addAttribute("configureDto", configureDto);
		model.addAttribute("journeyEngine", journeyEngine);

		return "journeyEngine";
	}

	@RequestMapping("/saveJourneyEngine")
	public String saveJourney(Model model, @ModelAttribute JourneyEngine journeyEngine) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		journeyEngineService.saveJourneyEngine(journeyEngine, auth.getName());

		return "redirect:/journeyEngine";
	}

}
