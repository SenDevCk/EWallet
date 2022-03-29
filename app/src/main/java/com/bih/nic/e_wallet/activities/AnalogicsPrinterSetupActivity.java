

package com.bih.nic.e_wallet.activities;


import android.Manifest;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.utilitties.CommonPref;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AnalogicsPrinterSetupActivity extends AppCompatActivity {
    Toolbar toolbar_analog_pri_set;
    private static final int REQUEST_ENABLE_BT = 1;
    private ActionBar actionBar;
    ListView listDevicesFound;
    Button btnScanDevice, backBtnSET;
    TextView stateBluetooth, btaddressTv;
    BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter mBluetoothAdapter = null;
    static final UUID MY_UUID = UUID.randomUUID();

    private int BLUETOOTH_PERMISSION_CODE = 23;

    int printid1=0;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    String address = "";

    EditText bluetoothTXT;
    public final String DATA_PATH1 = Environment.getExternalStorageDirectory()
            + "/";

    ArrayAdapter<String> btArrayAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
      /*  actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>SetUp</font>"));*/

        toolbar_analog_pri_set=(Toolbar)findViewById(R.id.toolbar_analog_pri_set);
        toolbar_analog_pri_set.setTitle(Html.fromHtml("<font color='#FFFFFF'>SetUp</font>"));
        setSupportActionBar(toolbar_analog_pri_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btaddressTv = (TextView) findViewById(R.id.btaddTV);
        // ***********************************************************
        try {

            FileInputStream fstream = new FileInputStream(DATA_PATH1
                    + "BTaddress.txt");

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                btaddressTv.setText(strLine);
            }

            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());

        }

        // ***********************************************************

        btnScanDevice = (Button) findViewById(R.id.scandevice);
        stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listDevicesFound = (ListView) findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<String>(AnalogicsPrinterSetupActivity.this,
                android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        bluetoothTXT = (EditText) findViewById(R.id.bluetoothAds);
        CheckBlueToothState();
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));

        listDevicesFound.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

				/*
                 * Toast.makeText(getApplicationContext(),
				 * ""+listDevicesFound.getCount(), Toast.LENGTH_SHORT).show();
				 */
                String selection = (String) (listDevicesFound
                        .getItemAtPosition(position));
                Toast.makeText(getApplicationContext(),
                        "BLUETOOTH ADDRESS IS SAVED SUCCESSFULLY",
                        Toast.LENGTH_SHORT).show();

                address = selection.substring(0, 17);
                CommonPref.setPrinterMacAddress(getApplicationContext(), address);
                CommonPref.setPrinterType(getApplicationContext(), "A");
                bluetoothTXT.setText(address);

                try {

                    File myFile = new File(DATA_PATH1 + "BTaddress.txt");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(address);
                    myOutWriter.close();
                    fOut.close();
                } catch (Exception e) {
                    return;
                }

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Alertmessage();

            }
        });


        backBtnSET = (Button) findViewById(R.id.backBtnSET);
        backBtnSET.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                finish();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
        } else {
            if (checkAndRequestPermissions()){
            }
        }
    }

    private void CheckBlueToothState() {
        if (bluetoothAdapter == null) {
            stateBluetooth.setText("Bluetooth NOT support");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                } else {
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            } else {
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private OnClickListener btnScanDeviceOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                btArrayAdapter.clear();
                bluetoothAdapter.startDiscovery();
            } else {
                if (checkAndRequestPermissions()){
                    btArrayAdapter.clear();
                    bluetoothAdapter.startDiscovery();
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getAddress() + "\n"
                        + device.getName());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void Alertmessage() {

        if (mBluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth is not available.",
                    Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this,
                    "Please enable your BT and re-run this program.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean checkAndRequestPermissions() {

        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "All Permissions Allowed !", Toast.LENGTH_SHORT).show();
                    btArrayAdapter.clear();
                    bluetoothAdapter.startDiscovery();
                }
                else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Please enable all permissions !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}