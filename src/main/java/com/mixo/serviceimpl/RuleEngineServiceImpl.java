package com.mixo.serviceimpl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.model.RuleEngine;
import com.mixo.repository.RuleEngineRepository;
import com.mixo.service.RuleEngineService;

@Service
public class RuleEngineServiceImpl implements RuleEngineService {

	@Autowired
	RuleEngineRepository ruleEngineRepository;

	@Override
	public RuleEngine findRuleEngine(String uid, String productId) {
		Optional<RuleEngine> ruleEngine = ruleEngineRepository.findByUidAndProductId(uid, productId);
		if (ruleEngine.isPresent()) {
			return ruleEngine.get();
		}
		RuleEngine newRuleEngine = new RuleEngine();
		newRuleEngine.setUid(uid);
		newRuleEngine.setProductId(productId);
		newRuleEngine.setCibilStatus("INACTIVE");
		newRuleEngine.setCibilValue("0");
		newRuleEngine.setFirstYearDpdStatus("INACTIVE");
		newRuleEngine.setFirstYearDpdValue("0");
		newRuleEngine.setSecondYearDpdStatus("INACTIVE");
		newRuleEngine.setSecondYearDpdValue("0");
		newRuleEngine.setMinAgeStatus("INACTIVE");
		newRuleEngine.setMinAgeValue("0");
		newRuleEngine.setMaxAgeStatus("INACTIVE");
		newRuleEngine.setMaxAgeValue("0");
		return newRuleEngine;

	}

	@Override
	public String saveRuleEngine(RuleEngine ruleEngine, String name) {
		Optional<RuleEngine> ruleEngines = ruleEngineRepository.findByUidAndProductId(ruleEngine.getUid(),
				ruleEngine.getProductId());
		if (ruleEngines.isPresent()) {
			ruleEngine.setId(ruleEngines.get().getId());
			BeanUtils.copyProperties(ruleEngine, ruleEngines.get());
			ruleEngines.get().setUpdatedBy(name);
			ruleEngineRepository.save(ruleEngines.get());
			return "RuleEngine updated successfully";
		} else {
			ruleEngine.setCreatedBy(name);
			ruleEngineRepository.save(ruleEngine);
			return "RuleEngine saved successfully";

		}
	}

}
