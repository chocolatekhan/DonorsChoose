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

public class SSLCommerz implements SSLCTransactionResponseListener {

    Context mainContext;

    public void makePayment (Context context, int amount) {

        mainContext = context;

        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization(
                "donor5fe9eb165fe19", "donor5fe9eb165fe19@ssl",
                amount, SSLCCurrencyType.BDT,
                "123456789098765", "yourProductType", SSLCSdkType.TESTBOX);


        IntegrateSSLCommerz.getInstance(mainContext).addSSLCommerzInitialization(sslCommerzInitialization).buildApiCall(this);

    }

    @Override
    public void transactionSuccess(SSLCTransactionInfoModel sslcTransactionInfoModel) {
        Toast.makeText(mainContext, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void transactionFail(String s) {
        Toast.makeText(mainContext, "Failure", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void merchantValidationError(String s) {
        Toast.makeText(mainContext, "Error", Toast.LENGTH_SHORT).show();

    }
}