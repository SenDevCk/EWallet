package com.bih.nic.e_wallet.entity;

/**
 * Created by NIC2 on 08-03-2018.
 */

public class UserInfo2 {
    private String MessageString;
    private String UserID;
    private String UserName;
    private String SubDiv;
    private String SubdivName;
    private String WalletId;
    private String WalletAmount;
    private boolean Authenticated;
    private String ImeiNo;
    private String ContactNo;
    private String IFSCCode;
    private String password;
    private String serialNo;
    private String ACCT_NO;


    public String getMessageString() {
        return MessageString;
    }

    public void setMessageString(String messageString) {
        MessageString = messageString;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSubDiv() {
        return SubDiv;
    }

    public void setSubDiv(String subDiv) {
        SubDiv = subDiv;
    }

    public String getSubdivName() {
        return SubdivName;
    }

    public void setSubdivName(String subdivName) {
        SubdivName = subdivName;
    }

    public String getWalletId() {
        return WalletId;
    }

    public void setWalletId(String walletId) {
        WalletId = walletId;
    }

    public String getWalletAmount() {
        return WalletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        WalletAmount = walletAmount;
    }

    public boolean getAuthenticated() {
        return Authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        Authenticated = authenticated;
    }

    public String getImeiNo() {
        return ImeiNo;
    }

    public void setImeiNo(String imeiNo) {
        ImeiNo = imeiNo;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getACCT_NO() {
        return ACCT_NO;
    }

    public void setACCT_NO(String ACCT_NO) {
        this.ACCT_NO = ACCT_NO;
    }
}
