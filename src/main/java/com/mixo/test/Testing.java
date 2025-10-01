package com.mixo.test;

public class Testing {

	public static void main(String[] args) {
	    double amountSum = 4867267.0; // Sample amount
	    // Define the max limit per transaction
	    double maxTransactionAmount = 200000; // 2 lakh

	    // Define a counter for txn-id generation
	    int txnIdCounter = 1;

	    // If the amountSum is greater than the max limit, split it
	    if (amountSum > maxTransactionAmount) {
	        // Calculate how many parts we need to split the amount into
	        double remainingAmount = amountSum;
	        while (remainingAmount > 0) {
	            double transactionAmount = Math.min(remainingAmount, maxTransactionAmount);

	            // Create txn-id for each chunk
	            String txnId = "txn-id" + txnIdCounter;

	            remainingAmount -= transactionAmount;

	            // Print the transaction details
	            System.out.println("Processed org settlement chunk: " + txnId + ", amount: " + transactionAmount);

	            // Increment the counter for the next transaction ID
	            txnIdCounter++;
	        }
	    } else {
	        // If the amount is within the limit
	        System.out.println("Amount is within the limit. Process txn-id1");
	    }
	}
	
	 

}
