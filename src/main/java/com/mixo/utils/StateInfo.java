package com.mixo.utils;

import java.util.HashMap;
import java.util.Map;

public interface StateInfo {

	Map<String, String> STATE_CODE_MAP = new HashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8648044653285328756L;

		{
			put("JAMMUANDKASHMIR", "01");
			put("HIMACHALPRADESH", "02");
			put("PUNJAB", "03");
			put("CHANDIGARH", "04");
			put("UTTRANCHAL", "05");
			put("HARYANA", "06");
			put("DELHI", "07");
			put("RAJASTHAN", "08");
			put("UTTARPRADESH", "09");
			put("BIHAR", "10");
			put("SIKKIM", "11");
			put("ARUNACHALPRADESH", "12");
			put("NAGALAND", "13");
			put("MANIPUR", "14");
			put("MIZORAM", "15");
			put("TRIPURA", "16");
			put("MEGHALAYA", "17");
			put("ASSAM", "18");
			put("WESTBENGAL", "19");
			put("JHARKHAND", "20");
			put("ORRISA", "21");
			put("CHHATTISGARH", "22");
			put("MADHYAPRADESH", "23");
			put("GUJRAT", "24");
			put("DAMANANDDIU", "25");
			put("DADARAANDNAGARHAVELI", "26");
			put("MAHARASHTRA"	, "27");
			put("ANDHRAPRADESH", "28");
			put("KARNATAKA", "29");
			put("GOA", "30");
			put("LAKSHADWEEP", "31");
			put("KERALA", "32");
			put("TAMILNADU", "33");
			put("PONDICHERRY", "34");
			put("ANDAMANANDNICOBARISLANDS", "35");
			put("TELANGANA", "36");
		}
	};

	// Method to retrieve state code by state name
	static String getStateCode(String stateName) {
		return STATE_CODE_MAP.get(stateName);
	}

}
