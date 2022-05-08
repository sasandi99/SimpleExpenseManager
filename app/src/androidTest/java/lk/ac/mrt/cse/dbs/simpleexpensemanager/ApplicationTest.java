/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentDemoExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest{
    private static ExpenseManager expenseManager;

    @BeforeClass
    public static void createApplication(){
        Context context = ApplicationProvider.getApplicationContext();
        assertEquals("lk.ac.mrt.cse.dbs.simpleexpensemanager", context.getPackageName());
        expenseManager = new PersistentDemoExpenseManager(context);

    }

    @Test
    public void newAccountTest (){
        expenseManager.addAccount("testAcc1","testBank1","testOwner1",5000.0);
        assertTrue(expenseManager.getAccountNumbersList().contains("testAcc1"));
    }

    @Test
    public void newTransactionTest(){
        int currCount = expenseManager.getTransactionLogs().size();
        try {
            expenseManager.updateAccountBalance("78945Z", 7, 7, 2010, ExpenseType.INCOME, "400.0");
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        assertEquals(currCount +1,expenseManager.getTransactionLogs().size());
    }


}