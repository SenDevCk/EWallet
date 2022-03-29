package com.bih.nic.e_wallet.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bxl.BXLConst;
import com.bxl.config.editor.BXLConfigLoader;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.config.JposEntry;
import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class BixolonSetupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,OutputCompleteListener, StatusUpdateListener, DirectIOListener, ErrorListener

{
	private static final String[] DATA_STRING = {
			"off-line status(0x10 0x04 0x02)",
			"paper roll sensor status(0x10 0x04 0x04)",
			"Firmware version(0x1d 0x49 0x41)",
			"Manufacturer(0x1d 0x49 0x42)",
			"Printer model(0x1d 0x49 0x43)",
			"Code page(0x1d 0x49 0x45)"
		};
	private static final byte[][] DATA = {
			{0x10, 0x04, 0x02},
			{0x10, 0x04, 0x04},
			{0x1d, 0x49, 0x41},
			{0x1d, 0x49, 0x42},
			{0x1d, 0x49, 0x43},
			{0x1d, 0x49, 0x45},
		};
	private Button btnConnect;

	private Spinner dataSpinner;

	private final ArrayList<CharSequence> bondedDevices = new ArrayList<>();
	private ArrayAdapter<CharSequence> arrayAdapter;

	private static final String DEVICE_ADDRESS_START = " (";
	private static final String DEVICE_ADDRESS_END = ")";

	private BXLConfigLoader bxlConfigLoader;
	private POSPrinter posPrinter;
	private String logicalName;
String address="";
	private long now = System.currentTimeMillis();
	private Date date = new Date(now);
	private SimpleDateFormat curDateFormat;
	private String strCurDate;
    Toolbar toolbar_bexton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_bixolon);
		toolbar_bexton=(Toolbar)findViewById(R.id.toolbar_bexton);
		toolbar_bexton.setTitle("Paired Printers");
		setSupportActionBar(toolbar_bexton);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		btnConnect = (Button) findViewById(R.id.buttonConnect);
		btnConnect.setOnClickListener(this);

		
		dataSpinner = (Spinner)findViewById(R.id.spinnerData);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, DATA_STRING);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataSpinner.setAdapter(adapter);

		setBondedDevices();

		arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, bondedDevices);
		ListView listView = (ListView) findViewById(R.id.listViewPairedDevices);
		listView.setAdapter(arrayAdapter);

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);

		bxlConfigLoader = new BXLConfigLoader(this);
		try
		{
			bxlConfigLoader.openFile();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bxlConfigLoader.newFile();
		}
		posPrinter = new POSPrinter(this);
		posPrinter.addOutputCompleteListener(this);
		posPrinter.addStatusUpdateListener(this);
		posPrinter.addErrorListener(this);
		posPrinter.addDirectIOListener(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if(posPrinter != null)
		{
			try
			{
				posPrinter.close();
			}
			catch(JposException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

			case R.id.buttonConnect:
				openPrinter();
				break;


		}
	}

	private void openPrinter()
	{
		try
		{
			posPrinter.open(address);
			posPrinter.claim(10000);
			posPrinter.setDeviceEnabled(true);
			posPrinter.setAsyncMode(true);
			CommonPref.setPrinterMacAddress(getApplicationContext(), address);
			CommonPref.setPrinterType(getApplicationContext(), "B");

		}
		catch(JposException e)
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

			try
			{
				posPrinter.close();
			}
			catch(JposException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	private void closePrinter()
	{
		try
		{
			posPrinter.close();
		}
		catch(JposException e)
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void setBondedDevices()
	{
		logicalName = null;
		bondedDevices.clear();

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter.getBondedDevices();

		for(BluetoothDevice device:bondedDeviceSet)
		{
			bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
		}

		if(arrayAdapter != null)
		{
			arrayAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		String device = ((TextView) view).getText().toString();

		logicalName = device.substring(0, device.indexOf(DEVICE_ADDRESS_START));

		address = device.substring(device.indexOf(DEVICE_ADDRESS_START) + DEVICE_ADDRESS_START.length(), device.indexOf(DEVICE_ADDRESS_END));

		try
		{
			for(Object entry:bxlConfigLoader.getEntries())
			{
				JposEntry jposEntry = (JposEntry) entry;
				bxlConfigLoader.removeEntry(jposEntry.getLogicalName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			bxlConfigLoader.addEntry(address,
									 BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
									 BXLConst.SPP_R210,
									 BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
									 address);
			bxlConfigLoader.saveFile();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	public void getBatteryStatus()
	{
		try
		{
			// Battery Status
			posPrinter.directIO(2, null, null);
		}
		catch(JposException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printDirectIO()
	{
		try
		{
			int selectedItemPosition = dataSpinner.getSelectedItemPosition();

			// Direct I/O
			posPrinter.directIO(1, null, DATA[selectedItemPosition]);
		}
		catch(JposException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void outputCompleteOccurred(final OutputCompleteEvent e)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(BixolonSetupActivity.this, "complete print", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void errorOccurred(final ErrorEvent arg0)
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{

				Toast.makeText(BixolonSetupActivity.this, "Error status : " + getERMessage(arg0.getErrorCodeExtended()), Toast.LENGTH_SHORT).show();

				if(getERMessage(arg0.getErrorCodeExtended()).equals("Power off"))
				{
					closePrinter();
				}

			}
		});
	}

	@Override
	public void statusUpdateOccurred(final StatusUpdateEvent arg0)
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(BixolonSetupActivity.this, "printer status : " + getSUEMessage(arg0.getStatus()), Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public void directIOOccurred(final DirectIOEvent e)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(BixolonSetupActivity.this, "DirectIO: " + e + "(" + getBatterStatusString(e.getData()) + ")", Toast.LENGTH_SHORT).show();

				if(e.getObject() != null)
				{
					Toast.makeText(BixolonSetupActivity.this, new String((byte[])e.getObject()), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private static String getERMessage(int status)
	{
		switch (status)
		{
			case POSPrinterConst.JPOS_EPTR_COVER_OPEN:
				return "Cover open";

			case POSPrinterConst.JPOS_EPTR_REC_EMPTY:
				return "Paper empty";

			case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
				return "Power off";

			default:
				return "Unknown";
		}
	}

	private static String getSUEMessage(int status)
	{
		switch (status)
		{
			case JposConst.JPOS_SUE_POWER_ONLINE:
				return "Power on";

			case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
				return "Power off";

			case POSPrinterConst.PTR_SUE_COVER_OPEN:
				return "Cover Open";

			case POSPrinterConst.PTR_SUE_COVER_OK:
				return "Cover OK";

			case POSPrinterConst.PTR_SUE_REC_EMPTY:
				return "Receipt Paper Empty";

			case POSPrinterConst.PTR_SUE_REC_NEAREMPTY:
				return "Receipt Paper Near Empty";

			case POSPrinterConst.PTR_SUE_REC_PAPEROK:
				return "Receipt Paper OK";

			case POSPrinterConst.PTR_SUE_IDLE:
				return "Printer Idle";

			default:
				return "Unknown";
		}
	}

	private String getBatterStatusString(int status)
	{
		switch (status)
		{
			case 0x30:
				return "Full";

			case 0x31:
				return "High";

			case 0x32:
				return "Middle";

			case 0x33:
				return "Low";

			default:
				return "Unknwon";
		}
	}
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
}
