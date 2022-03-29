package com.bih.nic.e_wallet.entity;

import java.io.Serializable;

/**
 * Created by NIC2 on 2/28/2018.
 */

public class MRUEntity implements Serializable{
    private String id;
    private String CON_ID;
    private String ACT_NO;
    private String OLD_CON_ID;
    private String CNAME;
    private String METER_NO;
    private String BOOK_NO;
    private String MOBILE_NO;
    private String PAYBLE_AMOUNT;
    private String BILL_NO;
    private String TARIFF_ID;
    private String MESSAGE_STRING;
    private String DATE_TIME;
    private String FA_HU_NAME;
    private String BILL_ADDR1;
    private String SUB_DIV_ID;
    private String LAST_PAY_DATE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCON_ID() {
        return CON_ID;
    }

    public void setCON_ID(String CON_ID) {
        this.CON_ID = CON_ID;
    }

    public String getACT_NO() {
        return ACT_NO;
    }

    public void setACT_NO(String ACT_NO) {
        this.ACT_NO = ACT_NO;
    }

    public String getOLD_CON_ID() {
        return OLD_CON_ID;
    }

    public void setOLD_CON_ID(String OLD_CON_ID) {
        this.OLD_CON_ID = OLD_CON_ID;
    }

    public String getCNAME() {
        return CNAME;
    }

    public void setCNAME(String CNAME) {
        this.CNAME = CNAME;
    }

    public String getMETER_NO() {
        return METER_NO;
    }

    public void setMETER_NO(String METER_NO) {
        this.METER_NO = METER_NO;
    }

    public String getBOOK_NO() {
        return BOOK_NO;
    }

    public void setBOOK_NO(String BOOK_NO) {
        this.BOOK_NO = BOOK_NO;
    }

    public String getMOBILE_NO() {
        return MOBILE_NO;
    }

    public void setMOBILE_NO(String MOBILE_NO) {
        this.MOBILE_NO = MOBILE_NO;
    }

    public String getPAYBLE_AMOUNT() {
        return PAYBLE_AMOUNT;
    }

    public void setPAYBLE_AMOUNT(String PAYBLE_AMOUNT) {
        this.PAYBLE_AMOUNT = PAYBLE_AMOUNT;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public void setBILL_NO(String BILL_NO) {
        this.BILL_NO = BILL_NO;
    }

    public String getTARIFF_ID() {
        return TARIFF_ID;
    }

    public void setTARIFF_ID(String TARIFF_ID) {
        this.TARIFF_ID = TARIFF_ID;
    }

    public String getMESSAGE_STRING() {
        return MESSAGE_STRING;
    }

    public void setMESSAGE_STRING(String MESSAGE_STRING) {
        this.MESSAGE_STRING = MESSAGE_STRING;
    }

    public String getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(String DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }

    public String getFA_HU_NAME() {
        return FA_HU_NAME;
    }

    public void setFA_HU_NAME(String FA_HU_NAME) {
        this.FA_HU_NAME = FA_HU_NAME;
    }

    public String getBILL_ADDR1() {
        return BILL_ADDR1;
    }

    public void setBILL_ADDR1(String BILL_ADDR1) {
        this.BILL_ADDR1 = BILL_ADDR1;
    }

    public String getSUB_DIV_ID() {
        return SUB_DIV_ID;
    }

    public void setSUB_DIV_ID(String SUB_DIV_ID) {
        this.SUB_DIV_ID = SUB_DIV_ID;
    }

    public String getLAST_PAY_DATE() {
        return LAST_PAY_DATE;
    }

    public void setLAST_PAY_DATE(String LAST_PAY_DATE) {
        this.LAST_PAY_DATE = LAST_PAY_DATE;
    }
}
