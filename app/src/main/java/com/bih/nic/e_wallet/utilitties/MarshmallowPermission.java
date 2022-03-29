package com.bih.nic.e_wallet.utilitties;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

/**
 * Created by NIC2 on 1/13/2018.
 */
public class MarshmallowPermission {

	public int result=-1;
	final  int state=0;

	public MarshmallowPermission(Context context, String permission)
	{
		try
		{
			if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
			{
				if (ActivityCompat.checkSelfPermission(context,permission)
						== PackageManager.PERMISSION_GRANTED) {

					this.result=0;
				}
				else
				{

					ActivityCompat.requestPermissions((Activity) context,
							new String[]{permission},
							state);

				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}



	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case state: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					this.result=1;

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.

				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}


}
