package com.mixo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.mixo.dto.NbfcDto;
import com.mixo.dto.NbfcRequestDto;
import com.mixo.model.Nbfc;

public interface NbfcService {
	
	String saveNbfc(NbfcDto nbfcDto);

	List<NbfcDto> getAllNbfc(String userName);
	
	List<NbfcRequestDto> getAllNbfcRequestDto(Authentication auth);
	
	Optional<Nbfc> getNbfcByUid(String uid);

	String updateNbfc(NbfcDto nbfcDto);

}
