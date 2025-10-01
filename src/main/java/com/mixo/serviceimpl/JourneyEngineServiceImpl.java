package com.mixo.serviceimpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.dto.ConfigureDto;
import com.mixo.model.JourneyEngine;
import com.mixo.repository.JourneyEngineRepository;
import com.mixo.service.JourneyEngineService;

@Service
public class JourneyEngineServiceImpl implements JourneyEngineService {

	@Autowired
	JourneyEngineRepository journeyEngineRepository;

	@Override
	public JourneyEngine findJourneyEngine(ConfigureDto configureDto) {

		JourneyEngine journeyEngine = journeyEngineRepository.findByUidAndProductId(configureDto.getUid(),
				configureDto.getProductId());

		if (journeyEngine != null) {
			return journeyEngine;

		}

		return null;
	}

	@Override
	public void saveJourneyEngine(JourneyEngine journeyEngine, String name) {
		JourneyEngine journeyEngine1 = journeyEngineRepository.findByUidAndProductId(journeyEngine.getUid(),
				journeyEngine.getProductId());

		if (journeyEngine1 == null) {
			journeyEngine.setUpdatedBy(name);
			journeyEngine.setCreatedBy(name);
			journeyEngineRepository.save(journeyEngine);
		}

		else {
			JourneyEngine journeyEngine2 = new JourneyEngine();
			BeanUtils.copyProperties(journeyEngine, journeyEngine2);
			journeyEngine2.setUid(journeyEngine.getUid());
			journeyEngine2.setProductId(journeyEngine.getProductId());
			journeyEngine2.setId(journeyEngine1.getId());
			journeyEngine2.setUpdatedBy(name);
			journeyEngineRepository.save(journeyEngine2);
		}

	}

}
