package com.bih.nic.e_wallet.dataBaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.bih.nic.e_wallet.entity.BookNoEntity;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.NeftEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.utilitties.Utiilties;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by NIC2 on 1/6/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    private static String DB_PATH = "";
    private static String DB_NAME = "e_wallet.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        if (Build.VERSION.SDK_INT >= 29) {
            DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        } else if (Build.VERSION.SDK_INT >= 21) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
            Log.d("DataBase", "exist");
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            Log.d("DataBase", "exist");
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        String myPath = null;
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                myPath = DB_PATH;
            } else {
                myPath = DB_PATH + DB_NAME;
            }
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS
                            | SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {

            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public boolean databaseExist() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = null;
        if (Build.VERSION.SDK_INT >= 29) {
            outFileName = DB_PATH;
        } else {
            outFileName = DB_PATH + DB_NAME;
        }

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = null;
        if (Build.VERSION.SDK_INT >= 29) {
            myPath = DB_PATH;
        } else {
            myPath = DB_PATH + DB_NAME;
        }
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      /*  String CREATE_NEFT_TABLE = "CREATE TABLE NEFT (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,WALLET_ID TEXT,AMOUNT TEXT,UTR_NO TEXT,TOPUP_TIME TEXT,u_id TEXT);";
        String CREATE_STATEMENT_TABLE = "CREATE TABLE Statement (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,CON_ID TEXT,RCPT_NO TEXT,PAY_AMT TEXT,WALLET_BALANCE TEXT,WALLET_ID TEXT,RRFContactNo TEXT,ConsumerContactNo TEXT,transStatus TEXT,MESSAGE_STRING TEXT,Authenticated TEXT,payDate\tTEXT,BILL_NO TEXT,payMode TEXT,CNAME TEXT,IS_PRINTED TEXT,TRANS_ID TEXT,USER_ID TEXT);";
        String CREATE_BookNo_TABLE = "CREATE TABLE BookNo (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,BookNo TEXT,MessageString TEXT,USER_ID TEXT);";
        String CREATE_MRU_TABLE = "CREATE TABLE MRU (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,CON_ID TEXT,ACT_NO TEXT,OLD_CON_ID TEXT,CNAME TEXT,METER_NO TEXT,BOOK_NO TEXT,MOBILE_NO TEXT,PAYBLE_AMOUNT TEXT,BILL_NO TEXT,TARIFF_ID TEXT,MESSAGE_STRING TEXT,DATE_TIME TEXT,FA_HU_NAME TEXT,BILL_ADDR1 TEXT,USER_ID TEXT);";
        db.execSQL(CREATE_NEFT_TABLE);
        db.execSQL(CREATE_STATEMENT_TABLE);
        db.execSQL(CREATE_BookNo_TABLE);
        db.execSQL(CREATE_MRU_TABLE);*/
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) return;
        ClearAllTable(db);
        onCreate(db);
        if (oldVersion == 1) {
            Log.d("New Version", "Data can be upgraded");
            //String CREATE_UNBILLEDCONSUMER_TABLE = "CREATE TABLE UNBILLEDCONSUMER (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,SUB_DIV_ID TEXT, CON_ID TEXT, ACT_NO TEXT, BOOK_NO TEXT, NAME TEXT, ADDRESS TEXT, LAST_PAY_DATE TEXT, USER_ID TEXT);";
            //db.execSQL(CREATE_UNBILLEDCONSUMER_TABLE);
        }

        Log.d("Sample Data", "onUpgrade	: " + newVersion);
    }

    public void ClearAllTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS UserDetail");
        db.execSQL("DROP TABLE IF EXISTS MRU");
        db.execSQL("DROP TABLE IF EXISTS BookNo");
        db.execSQL("DROP TABLE IF EXISTS Statement");
        db.execSQL("DROP TABLE IF EXISTS UNBILLEDCONSUMER");
    }

    public long saveMru(ArrayList<MRUEntity> mruEntities, String userid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (MRUEntity mruEntity : mruEntities) {
                ContentValues values = new ContentValues();
                values.put("CON_ID", mruEntity.getCON_ID().trim());
                values.put("ACT_NO", mruEntity.getACT_NO());
                values.put("OLD_CON_ID", mruEntity.getOLD_CON_ID());
                values.put("CNAME", mruEntity.getCNAME());
                values.put("METER_NO", mruEntity.getMETER_NO());
                values.put("BOOK_NO", mruEntity.getBOOK_NO());
                values.put("MOBILE_NO", mruEntity.getMOBILE_NO());
                values.put("PAYBLE_AMOUNT", mruEntity.getPAYBLE_AMOUNT());
                values.put("BILL_NO", mruEntity.getBILL_NO());
                values.put("TARIFF_ID", mruEntity.getTARIFF_ID());
                values.put("MESSAGE_STRING", mruEntity.getMESSAGE_STRING());
                values.put("DATE_TIME", mruEntity.getDATE_TIME());
                values.put("FA_HU_NAME", mruEntity.getFA_HU_NAME());
                values.put("BILL_ADDR1", mruEntity.getBILL_ADDR1());
                values.put("USER_ID", userid);
                String[] whereArgs = new String[]{mruEntity.getCON_ID().trim()};
                c = db.update("MRU", values, "CON_ID=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("MRU", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for VILLAGE");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<MRUEntity> getMRU(String bookno, String userid) {
        ArrayList<MRUEntity> mruEntities = new ArrayList<>();
        mruEntities.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;
            String query = "";
            if (bookno.equals("")) {
                query = "select * from MRU where USER_ID=?";
                cursor = db.rawQuery(query, new String[]{userid});
            } else {
                query = "select * from MRU where BOOK_NO=? and USER_ID=?";
                cursor = db.rawQuery(query, new String[]{bookno, userid});
            }
            while (cursor.moveToNext()) {
                MRUEntity mruEntity = new MRUEntity();
                mruEntity.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                mruEntity.setACT_NO(cursor.getString(cursor.getColumnIndex("ACT_NO")));
                mruEntity.setOLD_CON_ID(cursor.getString(cursor.getColumnIndex("OLD_CON_ID")));
                mruEntity.setCNAME(cursor.getString(cursor.getColumnIndex("CNAME")));
                mruEntity.setMETER_NO(cursor.getString(cursor.getColumnIndex("METER_NO")));
                mruEntity.setBOOK_NO(cursor.getString(cursor.getColumnIndex("BOOK_NO")));
                mruEntity.setMOBILE_NO(cursor.getString(cursor.getColumnIndex("MOBILE_NO")));
                mruEntity.setPAYBLE_AMOUNT(cursor.getString(cursor.getColumnIndex("PAYBLE_AMOUNT")));
                mruEntity.setBILL_NO(cursor.getString(cursor.getColumnIndex("BILL_NO")));
                mruEntity.setTARIFF_ID(cursor.getString(cursor.getColumnIndex("TARIFF_ID")));
                mruEntity.setMESSAGE_STRING(cursor.getString(cursor.getColumnIndex("MESSAGE_STRING")));
                mruEntity.setDATE_TIME(cursor.getString(cursor.getColumnIndex("DATE_TIME")));
                mruEntity.setFA_HU_NAME(cursor.getString(cursor.getColumnIndex("FA_HU_NAME")));
                mruEntity.setBILL_ADDR1(cursor.getString(cursor.getColumnIndex("BILL_ADDR1")));
                mruEntities.add(mruEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mruEntities;
    }

    public ArrayList<MRUEntity> getMRU(String userid, String... strings) {
        ArrayList<MRUEntity> mruEntities = new ArrayList<>();
        mruEntities.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] tokens = strings[0].split("\\|");
            Cursor cursor = null;
            String query = "";
            if (tokens[tokens.length - 1].equals("NA") && tokens[tokens.length - 3].equals("NA")) {
                query = "select * from MRU where CON_ID=? and USER_ID=?";
                cursor = db.rawQuery(query, new String[]{tokens[tokens.length - 2], userid});
            } else if (tokens[tokens.length - 1].equals("NA") && tokens[tokens.length - 2].equals("NA")) {
                query = "select * from MRU where BOOK_NO=? and USER_ID=?";
                cursor = db.rawQuery(query, new String[]{tokens[tokens.length - 3], userid});
            } else {
                query = "select * from MRU where ACT_NO=? and USER_ID=?";
                cursor = db.rawQuery(query, new String[]{tokens[tokens.length - 1], userid});
            }
            while (cursor.moveToNext()) {
                MRUEntity mruEntity = new MRUEntity();
                mruEntity.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                mruEntity.setACT_NO(cursor.getString(cursor.getColumnIndex("ACT_NO")));
                mruEntity.setOLD_CON_ID(cursor.getString(cursor.getColumnIndex("OLD_CON_ID")));
                mruEntity.setCNAME(cursor.getString(cursor.getColumnIndex("CNAME")));
                mruEntity.setMETER_NO(cursor.getString(cursor.getColumnIndex("METER_NO")));
                mruEntity.setBOOK_NO(cursor.getString(cursor.getColumnIndex("BOOK_NO")));
                mruEntity.setMOBILE_NO(cursor.getString(cursor.getColumnIndex("MOBILE_NO")));
                mruEntity.setPAYBLE_AMOUNT(cursor.getString(cursor.getColumnIndex("PAYBLE_AMOUNT")));
                mruEntity.setBILL_NO(cursor.getString(cursor.getColumnIndex("BILL_NO")));
                mruEntity.setTARIFF_ID(cursor.getString(cursor.getColumnIndex("TARIFF_ID")));
                mruEntity.setMESSAGE_STRING(cursor.getString(cursor.getColumnIndex("MESSAGE_STRING")));
                mruEntity.setDATE_TIME(cursor.getString(cursor.getColumnIndex("DATE_TIME")));
                mruEntity.setFA_HU_NAME(cursor.getString(cursor.getColumnIndex("FA_HU_NAME")));
                mruEntity.setBILL_ADDR1(cursor.getString(cursor.getColumnIndex("BILL_ADDR1")));
                mruEntities.add(mruEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mruEntities;
    }

    public long saveBookNo(ArrayList<BookNoEntity> mruEntities, String userid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        // db.execSQL("delete from BookNo");
        try {
            for (BookNoEntity bookNoEntity : mruEntities) {
                ContentValues values = new ContentValues();
                values.put("BookNo", bookNoEntity.getBookNo().trim());
                values.put("MessageString", bookNoEntity.getMessageString().trim());
                values.put("USER_ID", userid.trim());
                String[] whereArgs = new String[]{bookNoEntity.getBookNo().trim()};
                c = db.update("BookNo", values, "BookNo=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("BookNo", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for BookNo");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<BookNoEntity> getBookNo(String userid) {
        ArrayList<BookNoEntity> mruEntities = new ArrayList<>();
        mruEntities.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from BookNo where USER_ID=?";
            Cursor cursor = db.rawQuery(quary, new String[]{userid});
            while (cursor.moveToNext()) {
                BookNoEntity bookNoEntity = new BookNoEntity();
                bookNoEntity.setBookNo(cursor.getString(cursor.getColumnIndex("BookNo")));
                bookNoEntity.setMessageString(cursor.getString(cursor.getColumnIndex("MessageString")));
                if (cursor.getString(cursor.getColumnIndex("MessageString")).equalsIgnoreCase("success")) {
                    mruEntities.add(bookNoEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mruEntities;
    }

    public long saveStatement(String result, String userid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            JSONObject jsonObject = new JSONObject(result);
            ContentValues values = new ContentValues();
            values.put("CON_ID", jsonObject.getString("CON_ID"));
            values.put("RCPT_NO", jsonObject.getString("RCPT_NO"));
            values.put("PAY_AMT", jsonObject.getString("PAY_AMT"));
            values.put("WALLET_BALANCE", jsonObject.getString("WALLET_BALANCE"));
            values.put("WALLET_ID", jsonObject.getString("WALLET_ID"));
            values.put("RRFContactNo", jsonObject.getString("RRFContactNo"));
            values.put("ConsumerContactNo", jsonObject.getString("ConsumerContactNo"));
            values.put("transStatus", jsonObject.getString("transStatus"));
            values.put("MESSAGE_STRING", jsonObject.getString("MESSAGE_STRING"));
            values.put("CNAME", jsonObject.getString("CNAME"));
            values.put("payDate", Utiilties.convertStringToTimestampSlash(jsonObject.getString("transTime").trim()));
            values.put("BILL_NO", jsonObject.getString("BILL_NO"));
            values.put("payMode", jsonObject.getString("payMode"));
            values.put("TRANS_ID", jsonObject.getString("TRANS_ID").trim());
            values.put("USER_ID", userid);
            values.put("IS_PRINTED", "N");
            String[] whereArgs = new String[]{jsonObject.getString("TRANS_ID").trim()};
            c = db.update("Statement", values, "TRANS_ID=?", whereArgs);
            if (!(c > 0)) {
                c = db.insert("Statement", null, values);
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for Statement");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<Statement> getSuccessStatements(String user_id, String charSequence, boolean tog_val) {
        String query = null;
        ArrayList<Statement> statementMS = new ArrayList<>();
        statementMS.clear();
        if (charSequence.equals("")) {
            query = "select * from Statement where MESSAGE_STRING='SUCCESS' and USER_ID='" + user_id + "' ORDER BY payDate DESC";
        } else if (!charSequence.equals("") && tog_val) {
            query = "select * from Statement where MESSAGE_STRING='SUCCESS' and USER_ID='" + user_id + "' and (RCPT_NO LIKE'" + charSequence + "%' )ORDER BY payDate DESC";
        } else if (!charSequence.equals("") && !tog_val) {
            query = "select * from Statement where MESSAGE_STRING='SUCCESS' and USER_ID='" + user_id + "' and (CON_ID LIKE'" + charSequence + "%' )ORDER BY payDate DESC";
        }
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Statement statementM = new Statement();
                statementM.setId(cursor.getInt(cursor.getColumnIndex("id")));
                statementM.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                statementM.setRCPT_NO(cursor.getString(cursor.getColumnIndex("RCPT_NO")));
                statementM.setPAY_AMT(cursor.getString(cursor.getColumnIndex("PAY_AMT")));
                statementM.setWALLET_BALANCE(cursor.getString(cursor.getColumnIndex("WALLET_BALANCE")));
                statementM.setWALLET_ID(cursor.getString(cursor.getColumnIndex("WALLET_ID")));
                statementM.setRRFContactNo(cursor.getString(cursor.getColumnIndex("RRFContactNo")));
                statementM.setConsumerContactNo(cursor.getString(cursor.getColumnIndex("ConsumerContactNo")));
                statementM.setMESSAGE_STRING(cursor.getString(cursor.getColumnIndex("MESSAGE_STRING")));
                statementM.setPayDate(cursor.getLong(cursor.getColumnIndex("payDate")));
                statementM.setBILL_NO(cursor.getString(cursor.getColumnIndex("BILL_NO")));
                statementM.setPayMode(cursor.getString(cursor.getColumnIndex("payMode")));
                statementM.setCNAME(cursor.getString(cursor.getColumnIndex("CNAME")));
                statementM.set_IsAlredyPrint(cursor.getString(cursor.getColumnIndex("IS_PRINTED")));
                statementM.setTRANS_ID(cursor.getString(cursor.getColumnIndex("TRANS_ID")).trim());
                statementMS.add(statementM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statementMS;
    }

    public long saveTotalStatement(ArrayList<Statement> statementMS, String user_id) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        if (getTotalStatementCount(user_id) > 0) {
            db.execSQL("delete from Statement where TRANS_ID!='NA'");
        }
        try {
            for (Statement statementM : statementMS) {
                ContentValues values = new ContentValues();
                values.put("CON_ID", statementM.getCON_ID());
                values.put("CNAME", statementM.getCNAME());
                values.put("RCPT_NO", statementM.getRCPT_NO());
                values.put("PAY_AMT", statementM.getPAY_AMT());
                values.put("WALLET_BALANCE", statementM.getWALLET_BALANCE());
                values.put("WALLET_ID", statementM.getWALLET_ID());
                values.put("RRFContactNo", statementM.getRRFContactNo());
                values.put("ConsumerContactNo", statementM.getConsumerContactNo());
                values.put("transStatus", statementM.getTransStatus());
                values.put("MESSAGE_STRING", statementM.getMESSAGE_STRING());
                values.put("payDate", statementM.getPayDate());
                values.put("BILL_NO", statementM.getBILL_NO());
                values.put("payMode", statementM.getPayMode());
                values.put("TRANS_ID", statementM.getTRANS_ID().trim());
                values.put("USER_ID", user_id);
                values.put("IS_PRINTED", "N");
                String[] whereArgs = new String[]{statementM.getTRANS_ID().trim()};
                c = db.update("Statement", values, "TRANS_ID=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Statement", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for Statement");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;

    }

    private long getTotalStatementCount(String user_id) {
        long count = 0;
        String query = "select * from Statement where USER_ID=?";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{user_id});
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count;
    }

    public ArrayList<Statement> getTotalSynkStatements(String user_id) {
        ArrayList<Statement> statementMS = new ArrayList<>();
        statementMS.clear();
        String query = "select * from Statement where USER_ID=? and transStatus!=? and RCPT_NO!=? ORDER BY payDate DESC";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{user_id, "TC", "NA"});
            while (cursor.moveToNext()) {
                Statement statementM = new Statement();
                statementM.setId(cursor.getInt(cursor.getColumnIndex("id")));
                statementM.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                statementM.setRCPT_NO(cursor.getString(cursor.getColumnIndex("RCPT_NO")));
                statementM.setPAY_AMT(cursor.getString(cursor.getColumnIndex("PAY_AMT")));
                statementM.setWALLET_BALANCE(cursor.getString(cursor.getColumnIndex("WALLET_BALANCE")));
                statementM.setWALLET_ID(cursor.getString(cursor.getColumnIndex("WALLET_ID")));
                statementM.setRRFContactNo(cursor.getString(cursor.getColumnIndex("RRFContactNo")));
                statementM.setConsumerContactNo(cursor.getString(cursor.getColumnIndex("ConsumerContactNo")));
                statementM.setMESSAGE_STRING(cursor.getString(cursor.getColumnIndex("MESSAGE_STRING")));
                statementM.setPayDate(cursor.getLong(cursor.getColumnIndex("payDate")));
                statementM.setBILL_NO(cursor.getString(cursor.getColumnIndex("BILL_NO")));
                statementM.setPayMode(cursor.getString(cursor.getColumnIndex("payMode")));
                statementM.setCNAME(cursor.getString(cursor.getColumnIndex("CNAME")));
                statementM.setTransStatus(cursor.getString(cursor.getColumnIndex("transStatus")));
                statementM.set_IsAlredyPrint(cursor.getString(cursor.getColumnIndex("IS_PRINTED")));
                statementM.setTRANS_ID(cursor.getString(cursor.getColumnIndex("TRANS_ID")).trim());
                statementMS.add(statementM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statementMS;
    }


    public long updateStatementDetailsIsPrinted(String tra_id) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("IS_PRINTED", "Y");
            String[] whereArgs = new String[]{tra_id.trim()};
            c = db.update("Statement", values, "TRANS_ID=?", whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public long updateStatment(JSONObject jsonObject) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            if (jsonObject.has("CON_ID")) values.put("CON_ID", jsonObject.getString("CON_ID"));
            if (jsonObject.has("CNAME")) values.put("CNAME", jsonObject.getString("CNAME"));
            if (jsonObject.has("PAY_AMT")) values.put("PAY_AMT", jsonObject.getString("PAY_AMT"));
            if (jsonObject.has("WALLET_BALANCE"))
                values.put("WALLET_BALANCE", jsonObject.getString("WALLET_BALANCE"));
            if (jsonObject.has("WALLET_ID"))
                values.put("WALLET_ID", jsonObject.getString("WALLET_ID"));
            if (jsonObject.has("RRFContactNo"))
                values.put("RRFContactNo", jsonObject.getString("RRFContactNo"));
            if (jsonObject.has("ConsumerContactNo"))
                values.put("ConsumerContactNo", jsonObject.getString("ConsumerContactNo"));
            if (jsonObject.has("transStatus"))
                values.put("transStatus", jsonObject.getString("transStatus"));
            if (jsonObject.has("MESSAGE_STRING"))
                values.put("MESSAGE_STRING", jsonObject.getString("MESSAGE_STRING"));
            if (jsonObject.has("transTime"))
                values.put("payDate", jsonObject.getString("transTime").trim());
            if (jsonObject.has("BILL_NO")) values.put("BILL_NO", jsonObject.getString("BILL_NO"));
            if (jsonObject.has("payMode")) values.put("payMode", jsonObject.getString("payMode"));
            if (jsonObject.has("TRANS_ID"))
                values.put("TRANS_ID", jsonObject.getString("TRANS_ID").trim());
            if (jsonObject.has("RCPT_NO")) values.put("RCPT_NO", jsonObject.getString("RCPT_NO"));
            String[] whereArgs = new String[]{jsonObject.getString("TRANS_ID").trim()};
            c = db.update("Statement", values, "TRANS_ID=?", whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public long insertNeftDetails(ArrayList<NeftEntity> neftEntities, String uid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (NeftEntity neftEntity : neftEntities) {
                ContentValues values = new ContentValues();
                values.put("WALLET_ID", neftEntity.getWALLET_ID());
                values.put("AMOUNT", neftEntity.getAMOUNT());
                values.put("UTR_NO", neftEntity.getUTR_NO());
                values.put("TOPUP_TIME", neftEntity.getTOPUP_TIME());
                values.put("u_id", uid);
                String[] whereArgs = new String[]{neftEntity.getUTR_NO().trim()};
                c = db.update("NEFT", values, "UTR_NO=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("NEFT", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for NEFT");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<NeftEntity> getTotalNeft(String from, String to, String user_id) {
        ArrayList<NeftEntity> neftEntities = new ArrayList<>();
        neftEntities.clear();
        String query = "select * from NEFT where u_id=? and TOPUP_TIME BETWEEN ? AND ? ORDER BY TOPUP_TIME DESC";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{user_id, String.valueOf(Utiilties.convertStringToTimestamp((from + " 00:00:00").replace("/", "-"))), String.valueOf(Utiilties.convertStringToTimestamp((to + " 23:59:59").replace("/", "-")))});
            while (cursor.moveToNext()) {
                NeftEntity neftEntity = new NeftEntity();
                neftEntity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                neftEntity.setWALLET_ID(cursor.getString(cursor.getColumnIndex("WALLET_ID")));
                neftEntity.setAMOUNT(cursor.getString(cursor.getColumnIndex("AMOUNT")));
                neftEntity.setUTR_NO(cursor.getString(cursor.getColumnIndex("UTR_NO")));
                neftEntity.setTOPUP_TIME(cursor.getLong(cursor.getColumnIndex("TOPUP_TIME")));
                neftEntities.add(neftEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return neftEntities;
    }

    public ArrayList<Statement> getReport(String from, String to, String user_id) {
        ArrayList<Statement> statementMS = new ArrayList<>();
        statementMS.clear();
        String query = "select payDate,PAY_AMT,RCPT_NO from Statement where USER_ID=? and MESSAGE_STRING=? and payDate BETWEEN ? AND ? ORDER BY payDate DESC";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{user_id, "SUCCESS", String.valueOf(Utiilties.convertStringToTimestampSlash(from + " 00:00:00")), String.valueOf(Utiilties.convertStringToTimestampSlash(to + " 23:59:59"))});
            while (cursor.moveToNext()) {
                Statement statementM = new Statement();
                statementM.setRCPT_NO(cursor.getString(cursor.getColumnIndex("RCPT_NO")));
                statementM.setPAY_AMT(cursor.getString(cursor.getColumnIndex("PAY_AMT")));
                statementM.setPayDate(cursor.getLong(cursor.getColumnIndex("payDate")));
                statementMS.add(statementM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statementMS;
    }

    public Statement getTransStatus(UserInfo2 userInfo2, String con_id, String date) {
        Statement statement = null;
        String query = "select transStatus,payDate from Statement where USER_ID=? and CON_ID=? and payDate BETWEEN ? AND ?";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{userInfo2.getUserID(), con_id, String.valueOf(Utiilties.convertStringToTimestampSlash(date + " 00:00:00")), String.valueOf(Utiilties.convertStringToTimestampSlash(date + " 23:59:59"))});
            while (cursor.moveToNext()) {
                statement = new Statement();
                statement.setTransStatus(cursor.getString(cursor.getColumnIndex("transStatus")));
                statement.setPayDate(cursor.getLong(cursor.getColumnIndex("payDate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statement;
    }

    public void deleteRequestedPayment(String con_id, String amount) {
        String quary = "delete from Statement where CON_ID='" + con_id + "' and PAY_AMT='" + amount + "' and RCPT_NO='NA'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(quary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteReceptNo_NA_(String con_id) {
        String quary = "delete from Statement where CON_ID='" + con_id + "' and RCPT_NO='NA'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(quary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getPendingCount(String uid) {
        long c = -1;
        String quary = "select con_id from Statement where u_id= '" + uid + "' and (transStatus='TP' or transStatus='TI')";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(quary, null);
            c = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public long insertBanks(ArrayList<String> banks) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (String bank : banks) {
                ContentValues values = new ContentValues();
                values.put("BANK_NAME", bank.trim());
                c = db.update("Banks", values, "BANK_NAME=?", new String[]{bank.trim()});
                if (!(c > 0)) {
                    c = db.insert("Banks", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for Banks");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<String> getBanks() {
        ArrayList<String> banks = new ArrayList<>();
        banks.clear();
        banks.add("--Select Bank--");
        String query = "select BANK_NAME from Banks";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                banks.add(cursor.getString(cursor.getColumnIndex("BANK_NAME")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return banks;
    }

    public long saveUnbuiledConsumer(ArrayList<MRUEntity> res,String userid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (MRUEntity consumerEntity : res) {
                ContentValues values = new ContentValues();
                values.put("SUB_DIV_ID", consumerEntity.getSUB_DIV_ID());
                values.put("CON_ID", consumerEntity.getCON_ID());
                values.put("ACT_NO", consumerEntity.getACT_NO());
                values.put("BOOK_NO", consumerEntity.getBOOK_NO());
                values.put("NAME", consumerEntity.getCNAME());
                values.put("ADDRESS", consumerEntity.getBILL_ADDR1());
                values.put("LAST_PAY_DATE", consumerEntity.getLAST_PAY_DATE());
                values.put("USER_ID", userid.trim());
                String[] whereArgs = new String[]{consumerEntity.getCON_ID().trim()};
                c = db.update("UNBILLEDCONSUMER", values, "CON_ID=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("UNBILLEDCONSUMER", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for UNBILLEDCONSUMER");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<MRUEntity> getUnbuiledConsumer(String userid, String... tokens) {
        ArrayList<MRUEntity> mruEntities = new ArrayList<>();
        mruEntities.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;
            String query = "";
            if (tokens.length>0) {
                if (tokens[0].equals("conid") ) {
                    query = "select * from UNBILLEDCONSUMER where CON_ID=? and USER_ID=?";
                } else if (tokens[0].equals("book")) {
                    query = "select * from UNBILLEDCONSUMER where BOOK_NO=? and USER_ID=?";
                } else {
                    query = "select * from UNBILLEDCONSUMER where ACT_NO=? and USER_ID=?";
                }
                cursor = db.rawQuery(query, new String[]{tokens[1], userid});
            }
            else{
                query = "select * from UNBILLEDCONSUMER where USER_ID=?";
                cursor = db.rawQuery(query, new String[]{userid.trim()});
            }
            while (cursor.moveToNext()) {
                MRUEntity mruEntity = new MRUEntity();
                mruEntity.setSUB_DIV_ID(cursor.getString(cursor.getColumnIndex("SUB_DIV_ID")));
                mruEntity.setCON_ID(cursor.getString(cursor.getColumnIndex("CON_ID")));
                mruEntity.setACT_NO(cursor.getString(cursor.getColumnIndex("ACT_NO")));
                mruEntity.setBOOK_NO(cursor.getString(cursor.getColumnIndex("BOOK_NO")));
                mruEntity.setCNAME(cursor.getString(cursor.getColumnIndex("NAME")));
                mruEntity.setBILL_ADDR1(cursor.getString(cursor.getColumnIndex("ADDRESS")));
                mruEntity.setLAST_PAY_DATE(cursor.getString(cursor.getColumnIndex("LAST_PAY_DATE")));
                mruEntity.setMOBILE_NO("");
                mruEntities.add(mruEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mruEntities;
    }

    public long getUnbuiledConsumerCount(String userid){
        Cursor cursor = null;
        String query = "";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            query = "select * from UNBILLEDCONSUMER where USER_ID=?";
            cursor = db.rawQuery(query, new String[]{userid.trim()});

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return cursor.getCount();
    }


}