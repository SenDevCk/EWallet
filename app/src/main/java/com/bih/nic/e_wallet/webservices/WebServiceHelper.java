package com.bih.nic.e_wallet.webservices;


import com.bih.nic.e_wallet.entity.BookNoEntity;
import com.bih.nic.e_wallet.entity.GrivanceEntity;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.entity.ReportEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.entity.Versioninfo;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class WebServiceHelper {

    public static Versioninfo CheckVersion(String res) {
        Versioninfo versioninfo = null;
        try {
            JSONObject jsonObject = new JSONObject(res);
            versioninfo = new Versioninfo();
            versioninfo.setAdminMsg(jsonObject.getString("ADMINMSG"));
            versioninfo.setAdminTitle(jsonObject.getString("ADMINTITLE"));
            versioninfo.setUpdateMsg(jsonObject.getString("UPDATEMSG"));
            versioninfo.setUpdateTile(jsonObject.getString("UPDATETITLE"));
            versioninfo.setAppUrl(jsonObject.getString("APPURL"));
            versioninfo.setRole(jsonObject.getString("ROLE"));
            versioninfo.setAppversion(jsonObject.getString("VER"));
            versioninfo.setPriority(jsonObject.getString("PRIORITY"));
            versioninfo.setVerUpdated(jsonObject.getBoolean("ISUPDATED"));
            versioninfo.setValidDevice(jsonObject.getBoolean("ISVALIDDEVICE"));
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return versioninfo;
    }

    public static UserInfo2 loginParser(String res, String serial_id) {
        UserInfo2 userInfo2 = null;
        try {
            JSONObject jsonObject = new JSONObject(res);
            userInfo2 = new UserInfo2();
            userInfo2.setMessageString(jsonObject.getString("MessageString"));
            userInfo2.setUserID(jsonObject.getString("UserID"));
            userInfo2.setUserName(jsonObject.getString("UserName"));
            userInfo2.setSubDiv(jsonObject.getString("SubDiv"));
            userInfo2.setSubdivName(jsonObject.getString("SubdivName"));
            userInfo2.setWalletId(jsonObject.getString("WalletId"));
            userInfo2.setWalletAmount(jsonObject.getString("WalletAmount"));
            userInfo2.setAuthenticated(jsonObject.getBoolean("Authenticated"));
            userInfo2.setImeiNo(jsonObject.getString("ImeiNo"));
            userInfo2.setContactNo(jsonObject.getString("ContactNo"));
            userInfo2.setIFSCCode(jsonObject.getString("IFSCCode"));
            userInfo2.setACCT_NO(jsonObject.getString("ACCT_NO"));
            userInfo2.setSerialNo(serial_id);
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return userInfo2;
    }


    public static ArrayList<BookNoEntity> mruBookNo(String res) {
        ArrayList<BookNoEntity> mruEntities = new ArrayList<>();

        try {
            mruEntities.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BookNoEntity bookNoEntity = new BookNoEntity();
                bookNoEntity.setBookNo(jsonObject.getString("BookNo"));
                bookNoEntity.setMessageString(jsonObject.getString("MessageString"));
                mruEntities.add(bookNoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return mruEntities;
    }

    public static ArrayList<MRUEntity> mruParser(String res) {
        ArrayList<MRUEntity> mruEntities = new ArrayList<>();

        try {
            mruEntities.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MRUEntity bookNoEntity = new MRUEntity();
                bookNoEntity.setCON_ID(jsonObject.getString("CON_ID"));
                bookNoEntity.setACT_NO(jsonObject.getString("ACT_NO"));
                bookNoEntity.setOLD_CON_ID(jsonObject.getString("OLD_CON_ID"));
                bookNoEntity.setCNAME(jsonObject.getString("CNAME"));
                bookNoEntity.setMETER_NO(jsonObject.getString("METER_NO"));
                bookNoEntity.setBOOK_NO(jsonObject.getString("BOOK_NO"));
                bookNoEntity.setMOBILE_NO(jsonObject.getString("MOBILE_NO"));
                bookNoEntity.setPAYBLE_AMOUNT(jsonObject.getString("PAYBLE_AMOUNT"));
                bookNoEntity.setBILL_NO(jsonObject.getString("BILL_NO"));
                bookNoEntity.setTARIFF_ID(jsonObject.getString("TARIFF_ID"));
                bookNoEntity.setMESSAGE_STRING(jsonObject.getString("MESSAGE_STRING"));
                bookNoEntity.setDATE_TIME(jsonObject.getString("DATE_TIME"));
                bookNoEntity.setFA_HU_NAME(jsonObject.getString("FA_HU_NAME"));
                bookNoEntity.setBILL_ADDR1(jsonObject.getString("BILL_ADDR1"));
                if (jsonObject.getString("MESSAGE_STRING").equalsIgnoreCase("success"))
                    mruEntities.add(bookNoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return mruEntities;
    }


    public static ArrayList<Statement> SynkParser(String res) {
        ArrayList<Statement> statementMS = new ArrayList<>();

        try {
            statementMS.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Statement statementM = new Statement();
                if (jsonObject.has("ACT_NO")) statementM.setACT_NO(jsonObject.getString("ACT_NO"));
                if (jsonObject.has("CON_ID")) statementM.setCON_ID(jsonObject.getString("CON_ID"));
                if (jsonObject.has("RCPT_NO"))
                    statementM.setRCPT_NO(jsonObject.getString("RCPT_NO"));
                else statementM.setRCPT_NO("");
                if (jsonObject.has("PAY_AMT"))
                    statementM.setPAY_AMT(jsonObject.getString("PAY_AMT"));
                if (jsonObject.has("ConsumerContactNo"))
                    statementM.setConsumerContactNo(jsonObject.getString("ConsumerContactNo"));
                if (jsonObject.has("transStatus"))
                    statementM.setTransStatus(jsonObject.getString("transStatus"));
                if (jsonObject.has("BILL_NO"))
                    statementM.setBILL_NO(jsonObject.getString("BILL_NO"));
                if (jsonObject.has("cname")) statementM.setCNAME(jsonObject.getString("cname"));
                if (jsonObject.has("transTime"))
                    statementM.setPayDate(Utiilties.convertStringToTimestampSlash(jsonObject.getString("transTime")));
                if (jsonObject.has("MESSAGE_STRING"))
                    statementM.setMESSAGE_STRING(jsonObject.getString("MESSAGE_STRING"));
                if (jsonObject.has("Authenticated"))
                    statementM.setAuthenticated(jsonObject.getBoolean("Authenticated"));
                if (jsonObject.has("WALLET_BALANCE"))
                    statementM.setWALLET_BALANCE(jsonObject.getString("WALLET_BALANCE"));
                if (jsonObject.has("WALLET_ID"))
                    statementM.setWALLET_ID(jsonObject.getString("WALLET_ID"));
                if (jsonObject.has("RRFContactNo"))
                    statementM.setRRFContactNo(jsonObject.getString("RRFContactNo"));
                if (jsonObject.has("payMode"))
                    statementM.setPayMode(jsonObject.getString("payMode"));
                if (jsonObject.has("TRANS_ID"))
                    statementM.setTRANS_ID(jsonObject.getString("TRANS_ID"));
                else statementM.setTRANS_ID("");
                statementMS.add(statementM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return statementMS;
    }

    public static ArrayList<GrivanceEntity> GrievanceParser(String res) {
        ArrayList<GrivanceEntity> statementMS = new ArrayList<>();

        try {
            statementMS.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GrivanceEntity grivanceEntity = new GrivanceEntity();
                if (jsonObject.has("TICKET_NO"))
                    grivanceEntity.setTICKET_NO(jsonObject.getString("TICKET_NO"));
                if (jsonObject.has("WALLET_ID"))
                    grivanceEntity.setWALLET_ID(jsonObject.getString("WALLET_ID"));
                if (jsonObject.has("USER_ID"))
                    grivanceEntity.setUSER_ID(jsonObject.getString("USER_ID"));
                else grivanceEntity.setUSER_ID("");
                if (jsonObject.has("UTR_NO"))
                    grivanceEntity.setUTR_NO(jsonObject.getString("UTR_NO"));
                if (jsonObject.has("BANK")) grivanceEntity.setBANK(jsonObject.getString("BANK"));
                if (jsonObject.has("PAY_MODE"))
                    grivanceEntity.setPAY_MODE(jsonObject.getString("PAY_MODE"));
                if (jsonObject.has("AMOUNT"))
                    grivanceEntity.setAMOUNT(jsonObject.getString("AMOUNT"));
                if (jsonObject.has("TOPUP_DATE"))
                    grivanceEntity.setTOPUP_DATE(jsonObject.getString("TOPUP_DATE"));
                if (jsonObject.has("DATE_TIME"))
                    grivanceEntity.setDATE_TIME(jsonObject.getString("DATE_TIME"));
                if (jsonObject.has("STATUS"))
                    grivanceEntity.setSTATUS(jsonObject.getString("STATUS"));
                else grivanceEntity.setSTATUS("N");
                if (jsonObject.has("CURRENT_STATUS"))
                    grivanceEntity.setCURRENT_STATUS(jsonObject.getString("CURRENT_STATUS"));
                if (jsonObject.has("REMARKS"))
                    grivanceEntity.setREMARKS(jsonObject.getString("REMARKS"));
                if (jsonObject.has("MSG_STR"))
                    grivanceEntity.setMSG_STR(jsonObject.getString("USER_REMARKS"));
                statementMS.add(grivanceEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return statementMS;
    }


    public static ArrayList<NeftEntity> getNeft(String res) {
        ArrayList<NeftEntity> neftEntities = new ArrayList<NeftEntity>();

        try {
            neftEntities.clear();
            JSONArray jsonArray = new JSONArray(res);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NeftEntity neftEntity = new NeftEntity();
                    if (jsonObject.has("WALLET_ID"))
                        neftEntity.setWALLET_ID(jsonObject.getString("WALLET_ID"));
                    if (jsonObject.has("AMOUNT"))
                        neftEntity.setAMOUNT(jsonObject.getString("AMOUNT"));
                    if (jsonObject.has("UTR_NO"))
                        neftEntity.setUTR_NO(jsonObject.getString("UTR_NO"));
                    if (jsonObject.has("TOPUP_TIME"))
                        neftEntity.setTOPUP_TIME(Utiilties.convertStringToTimestamp(jsonObject.getString("TOPUP_TIME")));
                    neftEntities.add(neftEntity);
                }
            } else {
                return neftEntities;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return neftEntities;
    }

    public static ArrayList<ReportEntity> getReport(String res) {
        ArrayList<ReportEntity> reportEntities = new ArrayList<ReportEntity>();

        try {
            reportEntities.clear();
            JSONArray jsonArray = new JSONArray(res);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ReportEntity reportEntity = new ReportEntity();
                    if (jsonObject.has("payDate"))
                        reportEntity.setDate(jsonObject.getString("payDate"));
                    if (jsonObject.has("totalReceipts"))
                        reportEntity.setNo_of_recept(Integer.parseInt(jsonObject.getString("totalReceipts")));
                    if (jsonObject.has("collectedAmount"))
                        reportEntity.setTotal_amount(Double.parseDouble(jsonObject.getString("collectedAmount")));
                    if (jsonObject.has("verifyStatus"))
                        reportEntity.setVerifyStatus(jsonObject.getString("verifyStatus"));
                    reportEntities.add(reportEntity);
                }
            } else {
                return reportEntities;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return reportEntities;
    }

    public static ArrayList<MRUEntity> unbilledConsumerParser(String res) {
        ArrayList<MRUEntity> mruEntities = new ArrayList<>();
        try {
            mruEntities.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MRUEntity bookNoEntity = new MRUEntity();
                bookNoEntity.setCON_ID(jsonObject.getString("CON_ID"));
                bookNoEntity.setACT_NO(jsonObject.getString("ACT_NO"));
                bookNoEntity.setSUB_DIV_ID(jsonObject.getString("SUB_DIV_ID"));
                bookNoEntity.setCNAME(jsonObject.getString("NAME"));
                //bookNoEntity.setMETER_NO(jsonObject.getString("METER_NO"));
                bookNoEntity.setBOOK_NO(jsonObject.getString("BOOK_NO"));
                bookNoEntity.setBILL_ADDR1(jsonObject.getString("ADDRESS"));
                bookNoEntity.setLAST_PAY_DATE(jsonObject.getString("LAST_PAY_DATE"));
                    mruEntities.add(bookNoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return mruEntities;
    }

}
