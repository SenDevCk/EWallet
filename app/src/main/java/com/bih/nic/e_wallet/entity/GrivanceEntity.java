package com.bih.nic.e_wallet.entity;

import java.io.Serializable;

public class GrivanceEntity implements Serializable {

    private String TICKET_NO="";
    private String WALLET_ID="";
    private String USER_ID="";
    private String UTR_NO="";
    private String BANK="";
    private String PAY_MODE="";
    private String AMOUNT="";
    private String TOPUP_DATE="";
    private String DATE_TIME="";
    private String STATUS="";
    private String CURRENT_STATUS="";
    private String REMARKS="";
    private String MSG_STR="";

    public String getTICKET_NO() {
        return TICKET_NO;
    }

    public void setTICKET_NO(String TICKET_NO) {
        this.TICKET_NO = TICKET_NO;
    }

    public String getWALLET_ID() {
        return WALLET_ID;
    }

    public void setWALLET_ID(String WALLET_ID) {
        this.WALLET_ID = WALLET_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUTR_NO() {
        return UTR_NO;
    }

    public void setUTR_NO(String UTR_NO) {
        this.UTR_NO = UTR_NO;
    }

    public String getBANK() {
        return BANK;
    }

    public void setBANK(String BANK) {
        this.BANK = BANK;
    }

    public String getPAY_MODE() {
        return PAY_MODE;
    }

    public void setPAY_MODE(String PAY_MODE) {
        this.PAY_MODE = PAY_MODE;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getTOPUP_DATE() {
        return TOPUP_DATE;
    }

    public void setTOPUP_DATE(String TOPUP_DATE) {
        this.TOPUP_DATE = TOPUP_DATE;
    }

    public String getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(String DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getCURRENT_STATUS() {
        return CURRENT_STATUS;
    }

    public void setCURRENT_STATUS(String CURRENT_STATUS) {
        this.CURRENT_STATUS = CURRENT_STATUS;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    public String getMSG_STR() {
        return MSG_STR;
    }

    public void setMSG_STR(String MSG_STR) {
        this.MSG_STR = MSG_STR;
    }
}
