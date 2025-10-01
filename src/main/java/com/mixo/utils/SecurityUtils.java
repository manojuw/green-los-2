package com.mixo.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

	public boolean hasAuthority(String authority) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
				if (authority.equals(grantedAuthority.getAuthority())) {
					return true;
				}
			}
		}

		return false;
	}

}
