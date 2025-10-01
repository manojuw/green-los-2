package com.mixo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mixo.model.Product;
import com.mixo.model.RuleEngine;
import com.mixo.service.NbfcService;
import com.mixo.service.RuleEngineService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RuleEngineContoller {

	@Autowired
	NbfcService nbfcService;

	@Autowired
	RuleEngineService ruleEngineService;

	@RequestMapping("/ruleEngine")
	public String addNewProduct(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("ruleEngine", new Product());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "ruleEngine";
	}

	@RequestMapping("/checkRule")
	public String checkRule(Model model, 
	                        @RequestParam String uid, 
	                        @RequestParam(required = false) String productId) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("ruleEngine", new Product());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		model.addAttribute("message", "Data Fatched");
		RuleEngine ruleEngine = ruleEngineService.findRuleEngine(uid, productId);
		model.addAttribute("rule", ruleEngine);

		model.addAttribute("uid", uid);
		model.addAttribute("productId", productId);
		return "ruleEngine";
	}

	@RequestMapping("/addRuleEngine")
	public String addRuleEngine(Model model, @ModelAttribute RuleEngine ruleEngine) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("ruleEngine", new Product());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		model.addAttribute("message", "Data Fatched");
		String message = ruleEngineService.saveRuleEngine(ruleEngine, auth.getName());
		model.addAttribute("rule", ruleEngine);

		model.addAttribute("uid", ruleEngine.getUid());
		model.addAttribute("productId", ruleEngine.getProductId());
		model.addAttribute("rule", ruleEngine);
		model.addAttribute("message", message);
		return "ruleEngine";
	}

}
