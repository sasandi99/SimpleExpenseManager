package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public PersistentTransactionDAO(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String accTable = "create table Account (accountNo text primary key, bankName text, accountHolderName text, balance real)";
        String transTable = "create table Transactions (transId integer primary key autoincrement, date text, accountNo text, expenseType text, amount real, foreign key(accountNo) references Account(accountNo))";
        sqLiteDatabase.execSQL(accTable);
        sqLiteDatabase.execSQL(transTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String strDate =  new SimpleDateFormat("dd-MM-yyyy").format(date);
        String query = "insert into Transactions (date, accountNo, expenseType, amount) values (?, ?, ?, ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(query);
        statement.bindString(1, strDate);
        statement.bindString(2, accountNo);
        statement.bindString(3, expenseType.toString());
        statement.bindDouble(4, amount);
        statement.executeInsert();
        statement.close();
        sqLiteDatabase.close();
    }

    private List<Transaction> getTransList(Cursor cursor) {
        List<Transaction> trans = new ArrayList<Transaction>();
        if (cursor.moveToFirst()){
            do {
                Transaction transaction = new Transaction(null, null, null, 0);
                String date = cursor.getString(1);
                try {
                    Date dateObj =  new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
                    transaction.setDate(dateObj);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(2));
                String enumVal = cursor.getString(3);
                ExpenseType expenseType = ExpenseType.valueOf(enumVal);
                transaction.setExpenseType(expenseType);
                transaction.setAmount(Double.parseDouble(cursor.getString(4)));
                trans.add(transaction);
            } while (cursor.moveToNext());
        }
        return trans;
    }
    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Transactions";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        List<Transaction> trans =  this.getTransList(cursor);
        cursor.close();
        sqLiteDatabase.close();
        return trans;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Transactions limit " + String.valueOf(limit);
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        List<Transaction> trans =  this.getTransList(cursor);
        cursor.close();
        sqLiteDatabase.close();
        return trans;
    }
}
