package com.mixo.test;

public class TransactionManager {

	 private static int counter = 1000;
	    public static String getNewTransactionId(String type) {
	        return type + "-" + (++counter);
	    }
}
