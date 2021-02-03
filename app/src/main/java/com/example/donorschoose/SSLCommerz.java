package com.example.donorschoose;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;

import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener;

/**
 * Controller class for payment gateway
 */
public class SSLCommerz implements SSLCTransactionResponseListener {

    Context mainContext;

    /**
     * loads the payment gateway to pay given amount
     * @param context is the activity from which the method was called
     * @param amount is the amount to be paid
     */
    public void makePayment (Context context, int amount) {

        mainContext = context;

        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization(
                "donor5fe9eb165fe19", "donor5fe9eb165fe19@ssl",                 // store id and password for charity being paid to; holds test data for now
                amount, SSLCCurrencyType.BDT,                                                       // amount to be paid and currency
                "123456789098765", "yourProductType", SSLCSdkType.TESTBOX); // details of transaction


        IntegrateSSLCommerz.getInstance(mainContext).addSSLCommerzInitialization(sslCommerzInitialization).buildApiCall(this);
        // initiate payment
    }

    @Override
    public void transactionSuccess(SSLCTransactionInfoModel sslcTransactionInfoModel) {
       Donate.successfulTransaction(sslcTransactionInfoModel.getAmount());
    }

    @Override
    public void transactionFail(String s) {
        Toast.makeText(mainContext, "Transaction Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void merchantValidationError(String s) {
        Toast.makeText(mainContext, "Error", Toast.LENGTH_SHORT).show();
    }
}