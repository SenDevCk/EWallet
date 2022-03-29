package com.bih.nic.e_wallet.utilitties;


import com.bih.nic.e_wallet.entity.ReportEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;

import java.util.ArrayList;

/**
 * Created by Chandan on 1/04/2019.
 */
public class GlobalVariables {
	public static UserInfo2 LoggedUser;
	public static ArrayList<ReportEntity> reportEntities;
	public static boolean active = false;
	public static String bank_name="";
}
