package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {

    public PersistentAccountDAO(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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
    public List<String> getAccountNumbersList() {
        List<String> accNums = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select accountNo from Account";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                accNums.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return accNums;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accs = new ArrayList<Account>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Account";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                Account account = new Account(null, null, null, 0);
                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(Double.parseDouble(cursor.getString(3)));
                accs.add(account);
            } while (cursor.moveToNext());
        }
        return accs;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Account where accountNo = " + accountNo;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            Account account = new Account(null, null, null, 0);
            account.setAccountNo(cursor.getString(0));
            account.setBankName(cursor.getString(1));
            account.setAccountHolderName(cursor.getString(2));
            account.setBalance(Double.parseDouble(cursor.getString(3)));
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String accountNo = account.getAccountNo();
        String bankName = account.getBankName();
        String accountHolderName = account.getAccountHolderName();
        Double balance = account.getBalance();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", bankName);
        contentValues.put("accountHolderName", accountHolderName);
        contentValues.put("balance", balance);
        sqLiteDatabase.insert("Account", null, contentValues);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Account where accountNo = " + accountNo;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            sqLiteDatabase.delete("Account", "accountNo=?", new String[]{accountNo});
            return;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select balance from Account where accountNo = " + "'" + accountNo + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            Double newBalance = null;
            Double balance = Double.parseDouble(cursor.getString(0));
            switch (expenseType) {
                case EXPENSE:
                    newBalance = balance - amount;
                    break;
                case INCOME:
                    newBalance = balance + amount;
                    break;
            }
            String stmt = "update Account set balance = " + newBalance + " where accountNo = " + "'" + accountNo + "'";
            sqLiteDatabase.execSQL(stmt);
            return;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }
}