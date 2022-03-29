package com.bih.nic.e_wallet.entity;


import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NIC2 on 3/16/2018.
 */

public class Statement implements Serializable{
    // Comparable<Statement>
    private int id;
    private String CNAME;
    private String CON_ID;
    private String RCPT_NO;
    private String PAY_AMT;
    private String WALLET_BALANCE;
    private String WALLET_ID;
    private String RRFContactNo;
    private String ConsumerContactNo;
    private String transStatus;
    private String MESSAGE_STRING;
    private long payDate;
    private String BILL_NO;
    private String payMode;
    private String _IsAlredyPrint;
    private String ACT_NO;
    private boolean Authenticated;
    private String TRANS_ID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCNAME() {
        return CNAME;
    }

    public void setCNAME(String CNAME) {
        this.CNAME = CNAME;
    }

    public String getCON_ID() {
        return CON_ID;
    }

    public void setCON_ID(String CON_ID) {
        this.CON_ID = CON_ID;
    }

    public String getRCPT_NO() {
        return RCPT_NO;
    }

    public void setRCPT_NO(String RCPT_NO) {
        this.RCPT_NO = RCPT_NO;
    }

    public String getPAY_AMT() {
        return PAY_AMT;
    }

    public void setPAY_AMT(String PAY_AMT) {
        this.PAY_AMT = PAY_AMT;
    }

    public String getWALLET_BALANCE() {
        return WALLET_BALANCE;
    }

    public void setWALLET_BALANCE(String WALLET_BALANCE) {
        this.WALLET_BALANCE = WALLET_BALANCE;
    }

    public String getWALLET_ID() {
        return WALLET_ID;
    }

    public void setWALLET_ID(String WALLET_ID) {
        this.WALLET_ID = WALLET_ID;
    }

    public String getRRFContactNo() {
        return RRFContactNo;
    }

    public void setRRFContactNo(String RRFContactNo) {
        this.RRFContactNo = RRFContactNo;
    }

    public String getConsumerContactNo() {
        return ConsumerContactNo;
    }

    public void setConsumerContactNo(String consumerContactNo) {
        ConsumerContactNo = consumerContactNo;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public String getMESSAGE_STRING() {
        return MESSAGE_STRING;
    }

    public void setMESSAGE_STRING(String MESSAGE_STRING) {
        this.MESSAGE_STRING = MESSAGE_STRING;
    }

    public long getPayDate() {
        return payDate;
    }

    public void setPayDate(long payDate) {
        this.payDate = payDate;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public void setBILL_NO(String BILL_NO) {
        this.BILL_NO = BILL_NO;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String get_IsAlredyPrint() {
        return _IsAlredyPrint;
    }

    public void set_IsAlredyPrint(String _IsAlredyPrint) {
        this._IsAlredyPrint = _IsAlredyPrint;
    }


    public String getACT_NO() {
        return ACT_NO;
    }

    public void setACT_NO(String ACT_NO) {
        this.ACT_NO = ACT_NO;
    }

    public boolean isAuthenticated() {
        return Authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        Authenticated = authenticated;
    }

    public String getTRANS_ID() {
        return TRANS_ID;
    }

    public void setTRANS_ID(String TRANS_ID) {
        this.TRANS_ID = TRANS_ID;
    }

   /* @Override
    public int compareTo(@NonNull Statement o) {
        int get=0;
        try {
            get= Utiilties.convertTimestampToString(getPayDate()).compareTo(Utiilties.convertTimestampToString(o.getPayDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return get;
    }*/
}
