package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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
        cursor.close();
        sqLiteDatabase.close();
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
        cursor.close();
        sqLiteDatabase.close();
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
            cursor.close();
            sqLiteDatabase.close();
            return account;
        }
        cursor.close();
        sqLiteDatabase.close();
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
        String query = "insert into Account (accountNo, bankName, accountHolderName, balance) values (?, ?, ?, ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(query);
        statement.bindString(1, accountNo);
        statement.bindString(2, bankName);
        statement.bindString(3, accountHolderName);
        statement.bindDouble(4, balance);
        try{
            statement.executeInsert();
        } catch (Exception e){
            e.printStackTrace();
        }
        statement.close();
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from Account where accountNo = " + accountNo;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            sqLiteDatabase.delete("Account", "accountNo=?", new String[]{accountNo});
            cursor.close();
            sqLiteDatabase.close();
            return;
        }
        cursor.close();
        sqLiteDatabase.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String table = "Account";
        String[] columnToReturn = {"balance"};
        String condition = "accountNo = ?";
        String[] selectionArgs = {accountNo};
        Cursor cursor = sqLiteDatabase.query(table, columnToReturn, condition, selectionArgs, null, null, null);
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
            String sql = "update Account set balance = ? where accountNo = ?";
            SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
            statement.bindDouble(1, newBalance);
            statement.bindString(2, accountNo);
            statement.executeUpdateDelete();
            statement.close();
            cursor.close();
            sqLiteDatabase.close();
            return;
        }
        cursor.close();
        sqLiteDatabase.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }
}