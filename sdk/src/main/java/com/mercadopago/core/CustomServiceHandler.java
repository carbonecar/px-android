package com.mercadopago.core;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.services.CustomService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vaserber on 1/25/17.
 */

public class CustomServiceHandler {

    public static void createCheckoutPreference(Context context, String url, String uri, Callback<CheckoutPreference> callback) {
        CustomService service = getService(context, url);
        service.createPreference(uri, null).enqueue(callback);
    }

    public static void createCheckoutPreference(Context context, String url, String uri, Map<String, Object> bodyInfo, Callback<CheckoutPreference> callback) {
        CustomService service = getService(context, url);
        service.createPreference(uri, bodyInfo).enqueue(callback);
    }

    public static void getCustomer(Context context, String url, String uri, Callback<Customer> callback) {
        CustomService service = getService(context, url);
        service.getCustomer(uri, null).enqueue(callback);
    }

    public static void getCustomer(Context context, String url, String uri, Map<String, String> additionalInfo, Callback<Customer> callback) {
        CustomService service = getService(context, url);
        service.getCustomer(uri, additionalInfo).enqueue(callback);
    }

    public static void createPayment(Context context, Long transactionId, String baseUrl, String uri,
                                     Map<String, Object> paymentData, Callback<Payment> callback) {
        CustomService service = getService(context, baseUrl);
        service.createPayment(String.valueOf(transactionId), ripFirstSlash(uri), paymentData).enqueue(callback);
    }

    private static CustomService getService(Context context, String baseUrl) {

        Retrofit retrofit = getRetrofit(context, baseUrl);
        return retrofit.create(CustomService.class);
    }

    private static Retrofit getRetrofit(Context context, String baseUrl) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpClientUtil.getClient(context, 20, 20, 20))
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    private static String ripFirstSlash(String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

}
