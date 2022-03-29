package com.bih.nic.e_wallet.entity;



import java.util.Hashtable;

public class Versioninfo  {

	public static Class<Versioninfo> Versioninfo_CLASS = Versioninfo.class;
	private String adminTitle;
	private String adminMsg;
	private String updateTile;
	private String updateMsg;
	private String appUrl;
	private String role;
	private String imei;
	private boolean isVerUpdated;
	private boolean isValidDevice;
	//private int priority;
	private String priority;



	private String appversion;

	public Versioninfo() {

	}



	public String getAdminMsg() {
		return adminMsg;
	}

	public void setAdminMsg(String adminMsg) {
		this.adminMsg = adminMsg;
	}

	public String getAdminTitle() {
		return adminTitle;
	}

	public void setAdminTitle(String adminTitle) {
		this.adminTitle = adminTitle;
	}

	public String getUpdateTile() {
		return updateTile;
	}

	public void setUpdateTile(String updateTile) {
		this.updateTile = updateTile;
	}

	public String getUpdateMsg() {
		return updateMsg;
	}

	public void setUpdateMsg(String updateMsg) {
		this.updateMsg = updateMsg;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public boolean isVerUpdated() {
		return isVerUpdated;
	}

	public void setVerUpdated(boolean isVerUpdated) {
		this.isVerUpdated = isVerUpdated;
	}

	public boolean isValidDevice() {
		return isValidDevice;
	}

	public void setValidDevice(boolean isValidDevice) {
		this.isValidDevice = isValidDevice;
	}

//	public int getPriority() {
//		return priority;
//	}
//
//	public void setPriority(int priority) {
//		this.priority = priority;
//	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}


}
