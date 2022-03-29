package com.bih.nic.e_wallet.utilitties;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;

import java.util.ArrayList;


/**
 * Created by NIC2 on 1/12/2018.
 */
public class CommonPref {

	static Context context;
    public static ArrayList<NeftEntity> neftEntities;
	CommonPref() {

	}

	CommonPref(Context context) {
		CommonPref.context = context;
	}


	public static void setUserDetails(Context context, UserInfo2 UserInfo2) {

		String key = "_USER_DETAILS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("MessageString", UserInfo2.getUserName());
		editor.putString("UserID", UserInfo2.getUserID());
		editor.putString("UserName", UserInfo2.getUserName());
		editor.putString("SubDiv", UserInfo2.getSubDiv());
		editor.putString("SubdivName", UserInfo2.getSubdivName());
		editor.putString("WalletId", UserInfo2.getWalletId());
		editor.putString("WalletAmount", UserInfo2.getWalletAmount());
		editor.putBoolean("Authenticated", UserInfo2.getAuthenticated());
		editor.putString("ImeiNo", UserInfo2.getImeiNo());
		editor.putString("ContactNo", UserInfo2.getContactNo());
		editor.putString("IFSCCode", UserInfo2.getIFSCCode());
		editor.putString("SerialNo", UserInfo2.getSerialNo());
		//editor.putString("Password", Utiilties.encryption(UserInfo2.getPassword()));
		editor.putString("Password", UserInfo2.getPassword());
		editor.putString("ACCT_NO", UserInfo2.getACCT_NO());
		editor.commit();
	}

	public static UserInfo2 getUserDetails(Context context) {
		String key = "_USER_DETAILS";
		UserInfo2 UserInfo2 = new UserInfo2();
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		UserInfo2.setMessageString(prefs.getString("MessageString", ""));
		UserInfo2.setUserID(prefs.getString("UserID", ""));
		UserInfo2.setUserName(prefs.getString("UserName", ""));
		UserInfo2.setSubDiv(prefs.getString("SubDiv", ""));
		UserInfo2.setSubdivName(prefs.getString("SubdivName", ""));
		UserInfo2.setWalletId(prefs.getString("WalletId", ""));
		UserInfo2.setWalletAmount(prefs.getString("WalletAmount", ""));
		UserInfo2.setAuthenticated(prefs.getBoolean("Authenticated", false));
		UserInfo2.setImeiNo(prefs.getString("ImeiNo", ""));
		UserInfo2.setContactNo(prefs.getString("ContactNo", ""));
		UserInfo2.setIFSCCode(prefs.getString("IFSCCode", ""));
		UserInfo2.setSerialNo(prefs.getString("SerialNo", ""));
		//UserInfo2.setPassword(Utiilties.decryption(prefs.getString("Password", "")));
		UserInfo2.setPassword(prefs.getString("Password", ""));
		UserInfo2.setACCT_NO(prefs.getString("ACCT_NO", ""));
		return UserInfo2;
	}
	public static void logout(Context context) {

		String key = "_USER_DETAILS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("MessageString", "");
		editor.putString("UserID", "");
		editor.putString("UserName", "");
		editor.putString("SubDiv", "");
		editor.putString("SubdivName", "");
		editor.putString("WalletId", "");
		editor.putString("WalletAmount", "");
		editor.putBoolean("Authenticated", false);
		editor.putString("ImeiNo", "");
		editor.putString("ContactNo", "");
		editor.putString("IFSCCode", "");
		editor.putString("SerialNo", "");
		editor.putString("Password","");
		editor.putString("ACCT_NO","");
		editor.commit();
	}

	public static void setCheckUpdate(Context context, long dateTime) {

		String key = "_CheckUpdate";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		dateTime = dateTime + 1 * 3600000;
		editor.putLong("LastVisitedDate", dateTime);

		editor.commit();

	}
	public static void setPrinterMacAddress(Context context, String address) {

		String key = "_MAC_ADDRESS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("MacAddress", address);
		editor.commit();

	}

	public static String getPrinterMacAddress(Context context) {

		String key = "_MAC_ADDRESS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		String macAddress = prefs.getString("MacAddress", "");
		return macAddress;
	}

	public static void setPrinterType(Context context, String address) {

		String key = "P_Type";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("PType", address);
		editor.commit();

	}

	public static String getPrinterType(Context context) {

		String key = "P_Type";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		String Ptype = prefs.getString("PType", "");
		return Ptype;
	}

	public static void setCurrentDateForSync(Activity activity,String dateToStr) {
		String key = "CurrentDateS";

		SharedPreferences prefs = activity.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("date", dateToStr);
		editor.commit();
	}

	public static String getCurrentDateForSync(Activity activity) {

		String key = "CurrentDateS";

		SharedPreferences prefs = activity.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		return prefs.getString("date", "");
	}

    public static void setCurrentDate(Activity activity,String dateToStr) {
		String key = "CurrentDate";

		SharedPreferences prefs = activity.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("date", dateToStr);
		editor.commit();
    }

	public static String getCurrentDate(Activity activity) {

		String key = "CurrentDate";

		SharedPreferences prefs = activity.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		return prefs.getString("date", "");
	}
}
