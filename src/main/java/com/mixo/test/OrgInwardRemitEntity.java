package com.mixo.test;

class OrgInwardRemitEntity {
    private double transactionAmount;
    private String settlementTxnId;
    private String settlementStatus;

    public OrgInwardRemitEntity(double amount) {
        this.transactionAmount = amount;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setSettlementTxnId(String txnId) {
        this.settlementTxnId = txnId;
    }

    public void setSettlementStatus(String status) {
        this.settlementStatus = status;
    }

    @Override
    public String toString() {
        return "Remit{amt=" + transactionAmount + ", txnId='" + settlementTxnId + "'}";
    }
}
