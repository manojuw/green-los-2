package com.mixo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.dto.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LspContoller {

	@RequestMapping("/lsp")
	public String addNewLsp(Model model) {
		model.addAttribute("lsp", new UserDTO());
		return "lsp";
	}

}
