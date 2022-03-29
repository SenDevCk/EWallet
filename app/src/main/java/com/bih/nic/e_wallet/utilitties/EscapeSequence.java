package com.bih.nic.e_wallet.utilitties;

public final class EscapeSequence {

	public static String ESCAPE_CHARACTERS = new String(new byte[] {0x1b, 0x7c});

	public static String Normal 						= ESCAPE_CHARACTERS + "N";
	public static String Font_A 						= ESCAPE_CHARACTERS + "aM"; //Font A (12x24)
	public static String Font_B 						= ESCAPE_CHARACTERS + "bM"; //Font B (9x17)
	public static String Font_C 						= ESCAPE_CHARACTERS + "cM"; //Font C (9x24)
	public static String Left_justify					= ESCAPE_CHARACTERS + "lA";
	public static String Center 						= ESCAPE_CHARACTERS + "cA";
	public static String Right_justify 					= ESCAPE_CHARACTERS + "rA";
	public static String Bold 							= ESCAPE_CHARACTERS + "bC";
	public static String Disabled_bold 					= ESCAPE_CHARACTERS + "!bC";
	public static String Underline 						= ESCAPE_CHARACTERS + "uC";
	public static String Disabled_underline 			= ESCAPE_CHARACTERS + "!uC";
	public static String Reverse_video 					= ESCAPE_CHARACTERS + "rvC";
	public static String Disabled_reverse_videoe 		= ESCAPE_CHARACTERS + "!rvC";
	public static String Single_high_and_wide 			= ESCAPE_CHARACTERS + "1C";
	public static String Double_wide 					= ESCAPE_CHARACTERS + "2C";
	public static String Double_high 					= ESCAPE_CHARACTERS + "3C";
	public static String Double_high_and_wide 			= ESCAPE_CHARACTERS + "4C";
	public static String Scale_1_time_horizontally 		= ESCAPE_CHARACTERS + "1hC";
	public static String Scale_2_time_horizontally 		= ESCAPE_CHARACTERS + "2hC";
	public static String Scale_3_time_horizontally 		= ESCAPE_CHARACTERS + "3hC";
	public static String Scale_4_time_horizontally 		= ESCAPE_CHARACTERS + "4hC";
	public static String Scale_5_time_horizontally 		= ESCAPE_CHARACTERS + "5hC";
	public static String Scale_6_time_horizontally 		= ESCAPE_CHARACTERS + "6hC";
	public static String Scale_7_time_horizontally	 	= ESCAPE_CHARACTERS + "7hC";
	public static String Scale_8_time_horizontally 		= ESCAPE_CHARACTERS + "8hC";
	public static String Scale_1_time_vertically 		= ESCAPE_CHARACTERS + "1vC";
	public static String Scale_2_time_vertically 		= ESCAPE_CHARACTERS + "2vC";
	public static String Scale_3_time_vertically 		= ESCAPE_CHARACTERS + "3vC";
	public static String Scale_4_time_vertically 		= ESCAPE_CHARACTERS + "4vC";
	public static String Scale_5_time_vertically 		= ESCAPE_CHARACTERS + "5vC";
	public static String Scale_6_time_vertically 		= ESCAPE_CHARACTERS + "6vC";
	public static String Scale_7_time_vertically 		= ESCAPE_CHARACTERS + "7vC";
	public static String Scale_8_time_vertically 		= ESCAPE_CHARACTERS + "8vC";

}
