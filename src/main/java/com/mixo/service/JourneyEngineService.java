package com.mixo.service;

import com.mixo.dto.ConfigureDto;
import com.mixo.model.JourneyEngine;

public interface JourneyEngineService {

	JourneyEngine findJourneyEngine(ConfigureDto configureDto);

	void saveJourneyEngine(JourneyEngine journeyEngine, String name);

}
