package com.mixo.service;

import com.mixo.model.RuleEngine;

public interface RuleEngineService {

	RuleEngine findRuleEngine(String uid, String productId);

	String saveRuleEngine(RuleEngine ruleEngine, String name);

}
