package com.bih.nic.e_wallet.entity;

public class NeftEntity {

    private int id;
    private String WALLET_ID;
    private String AMOUNT;
    private String UTR_NO;
    private long TOPUP_TIME;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getWALLET_ID() {
        return WALLET_ID;
    }

    public void setWALLET_ID(String WALLET_ID) {
        this.WALLET_ID = WALLET_ID;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getUTR_NO() {
        return UTR_NO;
    }

    public void setUTR_NO(String UTR_NO) {
        this.UTR_NO = UTR_NO;
    }

    public long getTOPUP_TIME() {
        return TOPUP_TIME;
    }

    public void setTOPUP_TIME(long TOPUP_TIME) {
        this.TOPUP_TIME = TOPUP_TIME;
    }


}
