package com.bih.nic.e_wallet.entity;



import androidx.annotation.NonNull;

import com.bih.nic.e_wallet.utilitties.Utiilties;

import java.io.Serializable;

public class ReportEntity implements Serializable,Comparable<ReportEntity> {
    private String date;
    private int no_of_recept=0;
    private double total_amount=0.00;
    private String verifyStatus;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNo_of_recept() {
        return no_of_recept;
    }

    public void setNo_of_recept(int no_of_recept) {
        this.no_of_recept = no_of_recept;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public int compareTo(@NonNull ReportEntity o) {
        int get=0;
        try {
            get= getDate().compareTo(o.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return get;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }
}
