package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.EscapeSequence;
import com.bih.nic.e_wallet.utilitties.ShowMsg;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bxl.config.editor.BXLConfigLoader;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class PrintReceptActivity extends AppCompatActivity implements ReceiveListener,OutputCompleteListener, StatusUpdateListener, DirectIOListener, ErrorListener,View.OnClickListener {
    Toolbar toolbar_print_recept;
    Statement bill;

    private Printer mPrinter = null;
    private Context mContext = null;
    protected String btAddressDir = Environment.getExternalStorageDirectory()+ "";
    BluetoothAdapter bluetoothAdapter;
    String address = null;
    String macaddress = "";
    int c=0;
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 99;
    private static final int REQUEST_ENABLE_BT1 = 100;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private POSPrinter posPrinter;
    private BXLConfigLoader bxlConfigLoader;
    Button button_print;
    TextView text_consumer_name,text_consumer_ac,text_con_id_pr,text_bill_no_pr
            ,text_contact,text_walet_bal,text_pay_amount,text_recept_no2,text_amount,text_name_pr;
    LinearLayout ll1,ll2;
    private int BLUETOOTH_PERMISSION_CODE = 23;

    int printid1=0;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recept);
        toolbar_print_recept=(Toolbar)findViewById(R.id.toolbar_print_recept);
        toolbar_print_recept.setTitle("Print Recept");
        setSupportActionBar(toolbar_print_recept);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ll1=(LinearLayout)findViewById(R.id.ll1);
        ll2=(LinearLayout)findViewById(R.id.ll2);
        if (getIntent().hasExtra("object")) {
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.GONE);
            bill = (Statement) getIntent().getSerializableExtra("object");
            initUI1();
        }else{
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.VISIBLE);
            String query="select * from Statement where CON_ID=?";
            try {
                DataBaseHelper dataBaseHelper=new DataBaseHelper(PrintReceptActivity.this);
                SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery(query, new String[]{getIntent().getStringExtra("conid").trim()});
                while (cursor.moveToNext()) {
                    bill = new Statement();
                    bill.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    bill.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                    bill.setRCPT_NO(cursor.getString(cursor.getColumnIndex("RCPT_NO")));
                    bill.setPAY_AMT(cursor.getString(cursor.getColumnIndex("PAY_AMT")));
                    bill.setWALLET_BALANCE(cursor.getString(cursor.getColumnIndex("WALLET_BALANCE")));
                    bill.setWALLET_ID(cursor.getString(cursor.getColumnIndex("WALLET_ID")));
                    bill.setRRFContactNo(cursor.getString(cursor.getColumnIndex("RRFContactNo")));
                    bill.setConsumerContactNo(cursor.getString(cursor.getColumnIndex("ConsumerContactNo")));
                    bill.setMESSAGE_STRING(cursor.getString(cursor.getColumnIndex("MESSAGE_STRING")));
                    bill.setPayDate(cursor.getLong(cursor.getColumnIndex("payDate")));
                    bill.setBILL_NO(cursor.getString(cursor.getColumnIndex("BILL_NO")));
                    bill.setPayMode(cursor.getString(cursor.getColumnIndex("payMode")));
                    bill.setCNAME(cursor.getString(cursor.getColumnIndex("CNAME")));
                    bill.set_IsAlredyPrint(cursor.getString(cursor.getColumnIndex("IS_PRINTED")));
                    bill.setTRANS_ID(cursor.getString(cursor.getColumnIndex("TRANS_ID")));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            text_name_pr=(TextView)findViewById(R.id.text_name_pr);
            text_recept_no2=(TextView)findViewById(R.id.text_recept_no2);
            text_amount=(TextView)findViewById(R.id.text_amount);
            ImageView image_pr=(ImageView)findViewById(R.id.image_pr);
            if ((bill.getRCPT_NO().toString().trim().charAt(4))=='2') {
                image_pr.setImageDrawable(getResources().getDrawable(R.drawable.sblogo1));
            } else if ((bill.getRCPT_NO().toString().trim().charAt(4))=='1') {
                image_pr.setImageDrawable(getResources().getDrawable(R.drawable.nblogo));
            } else {
                image_pr.setImageDrawable(getResources().getDrawable(R.drawable.bsphcl_logo));
            }
            text_name_pr.setText("Hello "+bill.getCNAME()+",");
            text_recept_no2.setText("Recept number : "+bill.getRCPT_NO());
            text_amount.setText("Amount : Rs. "+bill.getPAY_AMT());
            button_print=(Button)findViewById(R.id.button_print);

            if (Utiilties.getCountOfDays(Utiilties.getCurrentDateWithTime(),Utiilties.convertTimestampToStringSlash( bill.getPayDate()))>=7){
                button_print.setClickable(false);
            }else{
                button_print.setOnClickListener(this);
            }
        }
        //printer initialization
        //  CommonPref.setPrinterstatus(PrintHomeActivity.this,false);
        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
            printerIntialise();
        } else {
            if (checkAndRequestPermissions()){
                printerIntialise();
            }
        }

    }
    private void printerIntialise(){
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
        c=0;
        mContext = PrintReceptActivity.this;
    }


    private void initUI1() {
        ImageView logo_pr=(ImageView)findViewById(R.id.logo_pr);
        if ((bill.getRCPT_NO().toString().trim().charAt(4))=='2') {
            logo_pr.setImageDrawable(getResources().getDrawable(R.drawable.sblogo1));
        } else if ((bill.getRCPT_NO().toString().trim().charAt(4))=='1') {
            logo_pr.setImageDrawable(getResources().getDrawable(R.drawable.nblogo));
        } else {
            logo_pr.setImageDrawable(getResources().getDrawable(R.drawable.bsphcl_logo));
        }
        text_consumer_name=(TextView)findViewById(R.id.text_consumer_name);
        text_consumer_ac=(TextView)findViewById(R.id.text_consumer_ac);
        text_con_id_pr=(TextView)findViewById(R.id.text_con_id_pr);
        text_bill_no_pr=(TextView)findViewById(R.id.text_bill_no_pr);
        text_contact=(TextView)findViewById(R.id.text_contact);
        text_walet_bal=(TextView)findViewById(R.id.text_walet_bal);
        text_pay_amount=(TextView)findViewById(R.id.text_pay_amount);
        text_consumer_name.setText(""+bill.getCNAME());
        text_consumer_ac.setText(""+bill.getRCPT_NO());
        text_con_id_pr.setText(""+bill.getCON_ID());
        text_contact.setText(""+bill.getConsumerContactNo());
        text_walet_bal.setText(""+Utiilties.convertTimestampToStringSlash(bill.getPayDate()));
        text_pay_amount.setText(""+bill.getPAY_AMT());
        text_bill_no_pr.setText(""+bill.getBILL_NO());
        button_print=(Button)findViewById(R.id.button_print);
        button_print.setOnClickListener(this);
        if (Utiilties.getCountOfDays(Utiilties.getCurrentDateWithTime(),Utiilties.convertTimestampToStringSlash( bill.getPayDate()))>=7){
            button_print.setClickable(false);
        }else{
            button_print.setOnClickListener(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        closePrinter();
        onBackPressed();
        return true;
    }
    private boolean runPrintReceiptSequence(Boolean isOnline) {
        if (!initializeObject()) {
            return false;
        }


        if (isOnline) {
            if (!createReceiptDataOffline()) {
                finalizeObject();
                return false;
            }
        } else {
            if (!createReceiptDataOffline()) {
                finalizeObject();
                return false;
            }
        }


        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_P20, Printer.MODEL_SOUTHASIA, mContext);
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }


    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                if (makeErrorMessage(status).isEmpty() && code == 0) {
                    long c1 = new DataBaseHelper(PrintReceptActivity.this).updateStatementDetailsIsPrinted(bill.getTRANS_ID());
                    if (c1 > 0) {
                        Toast.makeText(PrintReceptActivity.this, "Update in Local DataBase", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PrintReceptActivity.this, "Error in Local Database", Toast.LENGTH_LONG).show();
                    }
                }

                dispPrinterWarnings(status);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
        // EditText edtWarnings = (EditText) findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }


    }


    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }


    private boolean createReceiptData() {
        String method = "";

        StringBuilder textData = new StringBuilder();

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            String header;
            if (bill.get_IsAlredyPrint() != null) {
                if (bill.get_IsAlredyPrint().toString().trim().equals("Y")) {
                    header = "Duplicate Bill";
                } else {
                    header = "ENERGY BILL";
                }
            } else {
                header = "ENERGY BILL";
            }
            mPrinter.addText(header + "\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            textData.append("***********************************\n");
            textData.append("******************************\n");
            textData.append("CONSUMER DETAILS\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("BILL NO             :       " + bill.getBILL_NO().trim() + "\n");
            textData.append("CON ID              :       " + bill.getCON_ID().trim() + "\n");
            textData.append("C_NAME              :       " + bill.getCNAME() + "\n");
            textData.append("A/C NUMBER          :       " + bill.getRCPT_NO().trim() + "\n");
            textData.append("***********************************\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("READING DETAILS\n");
            mPrinter.addText("---------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }


    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }
        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(CommonPref.getPrinterMacAddress(mContext), Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }


    private void bluetooth() {
        // TODO Auto-generated method stub


        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth

                Toast.makeText(mContext, "Device does not support Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(mContext, "Bluetooth about to start.",
                        Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {

                if (bill != null) {
                    Toast.makeText(PrintReceptActivity.this, "Please Wait \nPrinting....", Toast.LENGTH_LONG).show();

                    if (CommonPref.getPrinterType(getApplicationContext()).equals("E")) {
                        runPrintReceiptSequence(false);
                    } else if (CommonPref.getPrinterType(getApplicationContext()).equals("A")) {
                        CheckBlueToothState(false);
                        File f = new File(btAddressDir + "/BTaddress.txt");
                        if (f.exists()) {

                            try {
                                FileInputStream fstream = new FileInputStream(btAddressDir + "/BTaddress.txt");
                                DataInputStream in = new DataInputStream(fstream);
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                String strLine;
                                while ((strLine = br.readLine()) != null) {
                                    address = strLine;
                                }
                                in.close();
                                open(true);
                            } catch (Exception e) {// Catch exception if any
                                System.err.println("Error: " + e.getMessage());

                            }

                        } else {
                            Intent intent = new Intent(PrintReceptActivity.this, AnalogicsPrinterSetupActivity.class);
                            startActivity(intent);
                        }
                    } else if (CommonPref.getPrinterType(getApplicationContext()).equals("B")) {
                        openPrinter();
                        try
                        {
                            posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
                            printReceipt();
                            posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
                        }
                        catch(JposException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(PrintReceptActivity.this,
                            "Error in Local Database", Toast.LENGTH_LONG)
                            .show();
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void CheckBlueToothState(Boolean flag) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            Toast.makeText(getBaseContext(), "Bluetooth is NOT Enabled",
                    Toast.LENGTH_LONG).show();

        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(
                            getBaseContext(),
                            "Bluetooth is currently in device discovery process.",
                            Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getBaseContext(), "Bluetooth is Enabled.",
                            Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getBaseContext(), "Bluetooth is NOT Enabled.",
                        Toast.LENGTH_LONG).show();
                if (flag == true) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT1);
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState(true);
        } else if (requestCode == REQUEST_ENABLE_BT1) {
            CheckBlueToothState(false);
        }
    }

    public void open(boolean flag) {
        try {

            conn.openBT(address);

            createReceiptDataOfflineForanalogics();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] convertBitmapToByteArray(Context context, Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private boolean createReceiptDataOfflineForanalogics() {

        UserInfo2 userinfo= CommonPref.getUserDetails(PrintReceptActivity.this);

        String method = "";
        StringBuilder textData = new StringBuilder();

        Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
        Bitmap logoData = null;
        if (printer == null) {
            return false;
        }
        try {
            String website = "";
            String header = "";
            if (userinfo.getUserID().startsWith("1")) {
                header = "           NBPDCL";
                website = "https://www.nbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(PrintReceptActivity.this.getResources(), R.drawable.nblogo);
            } else if (userinfo.getUserID().startsWith("2")) {
                header = "           SBPDCL";
                website =  "https://www.sbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(PrintReceptActivity.this.getResources(), R.drawable.sblogo1);
            } else {
                header = "" + bill.getRCPT_NO().trim();
            }
            if (logoData != null) {
                logoData = Bitmap.createScaledBitmap(logoData, 100, 100, true);
                byte bytes[] = null;
                bytes = printer.prepareLogoImageDataToPrint(address, logoData);
                conn.printData(bytes);
                conn.printData(printer.Reset_VIP());
            }
            textData.append("\n");
            textData.append("        Payment Receipt" + "\n");
            textData.append(header + "\n");
            textData.append("PAY DATE    :" + Utiilties.convertTimestampToStringSlash(bill.getPayDate()).trim().substring(0, 19) + "\n");
            textData.append("RECEIPT NO  :" + bill.getRCPT_NO().trim() + "\n");
            //textData.append("A/C NUMBER  :" + bill.getWALLET_ID().trim() + "\n");
            textData.append("CON ID      :" + bill.getCON_ID().trim() + "\n");
            textData.append("C_NAME      :" + bill.getCNAME().trim() + "\n");
            textData.append("BILL_NO     :" + bill.getBILL_NO().trim() + "\n");
            if (bill.getPayMode().equalsIgnoreCase("M")) {
                textData.append("PAY MODE    : " + "CASH" + "\n");
                textData.append("PAID AMOUNT : " + bill.getPAY_AMT().trim() + "\n");
            }
            textData.append("CON MOBNO.  :" + bill.getConsumerContactNo().trim() + "\n");
            textData.append("RRF ID      :" + CommonPref.getUserDetails(getApplicationContext()).getUserID() + "\n\n");
            textData.append("Pay your bill at " + website);
            textData.append("OR Bihar Bijli Bill Pay(BBBP)   mobile app.");
            textData.append("TOLLFREE HELPLINE NO. " + "1912" + "\n\n\n\n\n");
            textData.append("         DEVELOPED BY NIC BIHAR"  + "\n");
            conn.printData(textData.toString());
            textData.delete(0, textData.length());
            long c1 = new DataBaseHelper(PrintReceptActivity.this).updateStatementDetailsIsPrinted(bill.getTRANS_ID());
            if (c1 == 1) {
                Toast.makeText(PrintReceptActivity.this, "Database Updated !", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintReceptActivity.this, "Error in database !", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }
        textData = null;

        return true;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private void openPrinter()
    {
        try
        {
            //if(c==1 ){
                posPrinter.open(CommonPref.getPrinterMacAddress(PrintReceptActivity.this));
                posPrinter.claim(3000);
                posPrinter.setDeviceEnabled(true);
                posPrinter.setAsyncMode(true);
            //}
        }
        catch(JposException e)
        {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            try
            {
                posPrinter.release();
                posPrinter.close();
            }
            catch(JposException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    private boolean createReceiptDataOffline() {
        UserInfo2 userinfo= CommonPref.getUserDetails(PrintReceptActivity.this);
        String method = "";

        StringBuilder textData = new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        try {
            String header = "";
            String website = "";
            Bitmap logoData = null;
            if (userinfo.getUserID().startsWith("2")) {
                header = "SBPDCL";
                website = "https://www.sbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(getResources(), R.drawable.sblogo1);
            } else if (userinfo.getUserID().startsWith("1")) {
                header = "NBPDCL";
                website = "https://www.nbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(getResources(), R.drawable.nblogo);
            } else
                header = "Transaction failure";

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addImage";
            mPrinter.addTextFont(Printer.FONT_D);
            mPrinter.addImage(logoData, 0, 0, logoData.getWidth(), logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("Payment Receipt" + "\n");
            mPrinter.addText(header + "\n");
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            textData.append("\n");
            textData.append("PAY DATE     : " +Utiilties.convertTimestampToStringSlash(bill.getPayDate()).trim().substring(0, 19) + "\n");
            textData.append("RECEIPT NO   : " + bill.getRCPT_NO().trim() + "\n");
            //textData.append("A/C NUMBER   : " + bill.getWALLET_ID().trim() + "\n");
            textData.append("CON ID       : " + bill.getCON_ID().trim() + "\n");
            textData.append("C_NAME       : " + bill.getCNAME().trim() + "\n");
            textData.append("BILL NO.     : " + bill.getBILL_NO().trim() + "\n");
           if (bill.getPayMode().equalsIgnoreCase("M")) {
                textData.append("PAY MODE     : " + "CASH" + "\n");
                textData.append("PAID AMOUNT  : " + bill.getPAY_AMT().trim() + "\n");
            }
            textData.append("CON MOBNO.   : " + bill.getConsumerContactNo().trim() + "\n");
            textData.append("RRF_ID       : " + CommonPref.getUserDetails(getApplicationContext()).getUserID().trim() + "\n\n");
            textData.append("Pay your bills at  " + website );
            textData.append(" OR Bihar Bijli Bill Pay(BBBP)   mobile app.");
            textData.append("TOLLFREE HELPLINE NO. " + "1912" + "\n");
            textData.append("        DEVELOPED BY NIC BIHAR"  + "\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
            long c1 = new DataBaseHelper(PrintReceptActivity.this).updateStatementDetailsIsPrinted(bill.getTRANS_ID());
            if (c1 == 1) {
                Toast.makeText(PrintReceptActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintReceptActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            ShowMsg.showException(e, method, mContext);

            return false;
        }

        textData = null;

        return true;
    }

    private void closePrinter()
    {
        try
        {
            if (posPrinter!=null) {
                posPrinter.release();
                posPrinter.close();
            }
        }
        catch(JposException e)
        {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public void outputCompleteOccurred(final OutputCompleteEvent e)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                //   Toast.makeText(PrintReceptActivity.this, "complete print", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(PrintReceptActivity.this, "Error status : " + getERMessage(arg0.getErrorCodeExtended()), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(PrintReceptActivity.this, "printer status : " + getSUEMessage(arg0.getStatus()), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(PrintReceptActivity.this, "DirectIO: " + e + "(" + getBatterStatusString(e.getData()) + ")", Toast.LENGTH_SHORT).show();

                if(e.getObject() != null)
                {
                    Toast.makeText(PrintReceptActivity.this, new String((byte[])e.getObject()), Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        c=0;
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
        super.onResume();
    }

    private void printReceipt()
    {
        UserInfo2 userinfo= CommonPref.getUserDetails(PrintReceptActivity.this);
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
            buffer.put((byte) 80);
            buffer.put((byte) 0x00);
            buffer.put((byte) 0x00);
            String header = "";
            String website = "";
            Bitmap logoData = null;
            if (userinfo.getUserID().startsWith("2")) {
                header = "SBPDCL";
                website = "https://www.sbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(getResources(), R.drawable.sblogo1);
            } else if (userinfo.getUserID().startsWith("1")) {
                header = "NBPDCL";
                website = "https://www.nbpdcl.co.in";
                logoData = BitmapFactory.decodeResource(getResources(), R.drawable.nblogo);
            } else
                header = "Transaction failure";


            posPrinter.printBitmap(buffer.getInt(0), logoData, 100, POSPrinterConst.PTR_BM_CENTER);

            String DATA1 = "ETM TICKET";
            String ptrData = EscapeSequence.Font_C + EscapeSequence.Disabled_bold + EscapeSequence.Scale_2_time_horizontally + EscapeSequence.Scale_1_time_vertically + header + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            DATA1 = "भुगतान रसीद";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_2_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            DATA1 = "भुगतान की तिथि :" + Utiilties.convertTimestampToStringSlash(bill.getPayDate()).trim().substring(0, 19) + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            DATA1 = "रसीद संख्या   :" + bill.getRCPT_NO().trim() + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            /*DATA1 = "खाता नंबर    :" + bill.getWALLET_ID().trim() + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);*/

            DATA1 = "उपभोक्ता आईडी :" + bill.getCON_ID().trim() + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            DATA1 = "उपभोक्ता का नाम:" + bill.getCNAME().trim() + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

            DATA1 = "बील संख्या   :" + bill.getBILL_NO().trim() + "\n";
            ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically+EscapeSequence.Left_justify + DATA1 + "\n";
            posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

         if (bill.getPayMode().equalsIgnoreCase("M")) {
                DATA1 = "भुगतान मोड   :" + "नकद" + "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 ="भुगतान राशि   :" + bill.getPAY_AMT().trim() + "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 = "उपभोक्ता मोबाइल नंबर:" + bill.getConsumerContactNo().trim() + "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 = "RRF_ID     :" + CommonPref.getUserDetails(getApplicationContext()).getUserID().trim() + "\n\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 = "अपने बिलों का भुगतान " + website +" OR \n Bihar Bijli Bill Pay(BBBP)   mobile app"+ " से करें |"+ "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 = "TOLLFREE HELPLINE NO. " + "1912" + "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
                DATA1 = "        DEVELOPED BY NIC BIHAR"  + "\n";
                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically +DATA1 + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + "" + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);

                ptrData = EscapeSequence.Font_A + EscapeSequence.Disabled_bold + EscapeSequence.Scale_1_time_horizontally + EscapeSequence.Scale_1_time_vertically + "" + "\n";
                posPrinter.printNormal(POSPrinterConst.PTR_PM_NORMAL, ptrData);
            }

            long c1 = new DataBaseHelper(PrintReceptActivity.this).updateStatementDetailsIsPrinted(bill.getTRANS_ID());
            if (c1 == 1) {
                Toast.makeText(PrintReceptActivity.this, "Update database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PrintReceptActivity.this, "Error in database", Toast.LENGTH_LONG).show();
            }
        }
        catch(JposException e)
        {

            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        closePrinter();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            bluetooth();
            return;
        }else {

            //If the app has not the permission then asking for the permission
            requestBluetoothPermission();
        }
    }
    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestBluetoothPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.BLUETOOTH)){
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},BLUETOOTH_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == BLUETOOTH_PERMISSION_CODE){
            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                bluetooth();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }*/

    public void AlertDialogForPrinter() {
        final Dialog dialog = new Dialog(PrintReceptActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/

        dialog.setContentView(R.layout.printerdialog);
        dialog.setTitle("Printers");

        // set the custom dialog components - text, image and button

        final RadioButton epson = (RadioButton) dialog.findViewById(R.id.Epson);
        final RadioButton Analogics = (RadioButton) dialog.findViewById(R.id.Analogics);
        final RadioButton Bixolon = (RadioButton) dialog.findViewById(R.id.bixolon);

        Button Cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        // if button is clicked, close the custom dialog
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button Ok = (Button) dialog.findViewById(R.id.btn_OK);
        // if button is clicked, close the custom dialog
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epson.isChecked() && Analogics.isChecked()) {

                    Toast.makeText(getBaseContext(), "Please Select One Printer", Toast.LENGTH_SHORT).show();
                } else if (epson.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Epson", Toast.LENGTH_SHORT).show();
                    CommonPref.setPrinterType(PrintReceptActivity.this, "E");
                    dialog.dismiss();
                    bluetooth(1);
                } else if (Analogics.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Analogics", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PrintReceptActivity.this,
                            AnalogicsPrinterSetupActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    //bluetooth(2);
                }
                else if (Bixolon.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Analogics", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    bluetooth(2);
                }

            }
        });

        dialog.show();
    }
    private void bluetooth(int printid) {
        // TODO Auto-generated method stub
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(PrintReceptActivity.this, "Device does not support Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(PrintReceptActivity.this, "Bluetooth about to start.",Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                if (printid == 1) {
                    printid1=1;
                    if (Build.VERSION.SDK_INT < 23) {
                        //Do not need to check the permission
                        Intent intent = new Intent(PrintReceptActivity.this, ConfigurePrinterActivity.class);
                        startActivity(intent);
                    } else {
                        if (checkAndRequestPermissions()){
                            Intent intent = new Intent(PrintReceptActivity.this, ConfigurePrinterActivity.class);
                            startActivity(intent);
                        }
                    }

                }else if(printid == 2){
                    printid1=2;
                    if (Build.VERSION.SDK_INT < 23) {
                        //Do not need to check the permission
                        Intent intent = new Intent(PrintReceptActivity.this, BixolonSetupActivity.class);
                        startActivity(intent);
                    } else {
                        if (checkAndRequestPermissions()){
                            Intent intent = new Intent(PrintReceptActivity.this, BixolonSetupActivity.class);
                            startActivity(intent);
                        }
                    }

                }

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            return;
        }
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
                    printerIntialise();
                    if (printid1==1){
                        Intent intent = new Intent(PrintReceptActivity.this,
                                ConfigurePrinterActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(PrintReceptActivity.this, BixolonSetupActivity.class);
                        startActivity(intent);
                    }
                }
                else if(requestCode == BLUETOOTH_PERMISSION_CODE){
                    //If permission is granted
                    if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        printerIntialise();
                        bluetooth();
                    }else{
                        //Displaying another toast if permission is not granted
                        Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Please enable all permissions !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.print_recept_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print_menu: {
                AlertDialogForPrinter();
                break;
            }
           case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}
