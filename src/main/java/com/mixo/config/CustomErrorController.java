package com.mixo.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError() {
		// Handle the 404 error
		return "redirect:/access-denied";
	}

	public String getErrorPath() {
		return "redirect:/access-denied";
	}
}