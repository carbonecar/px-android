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

    public static void createCheckoutPreference(Context context, Callback<CheckoutPreference> callback) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("item_id", "1");
//        map.put("amount", new BigDecimal(300));
//        ServicePreference servicePreference = new ServicePreference.Builder()
//                .setCreateCheckoutPreferenceURL("http://private-4d9654-mercadopagoexamples.apiary-mock.com",
//                        "/merchantUri/create_preference", map)
//                .build();

        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
        String checkoutPreferenceURL = servicePreference.getCreateCheckoutPreferenceURL();
        String checkoutPreferenceURI = servicePreference.getCreateCheckoutPreferenceURI();
        Map<String, Object> additionalInfo = servicePreference.getCreateCheckoutPreferenceAdditionalInfo();

        CustomService service = getService(context, checkoutPreferenceURL);
        service.createPreference(checkoutPreferenceURI, additionalInfo).enqueue(callback);
    }

    public static void getCustomer(Context context, Callback<Customer> callback) {
//        ServicePreference servicePreference = new ServicePreference.Builder()
//                .setCreateCheckoutPreferenceURL("/baseUrl", "/Uri")
//                .build();

        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
        String getCustomerURL = servicePreference.getGetCustomerURL();
        String getCustomerURI = servicePreference.getGetCustomerURI();
        Map<String, String> additionalInfo = servicePreference.getGetCustomerAdditionalInfo();

        CustomService service = getService(context, getCustomerURL);
        service.getCustomer(getCustomerURI, additionalInfo).enqueue(callback);
    }

    public static void createPayment(Context context, Long transactionId, PaymentBody paymentBody, Callback<Payment> callback) {
//        ServicePreference servicePreference = new ServicePreference.Builder()
//                .setCreateCheckoutPreferenceURL("/baseUrl", "/Uri")
//                .build();

        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
        String createPaymentURL = servicePreference.getCreatePaymentURL();
        String createPaymentURI = servicePreference.getCreatePaymentURI();
        Map<String, Object> additionalInfo = servicePreference.getCreatePaymentAdditionalInfo();
        addPaymentBodyToMap(paymentBody, additionalInfo);
        createPayment(context, transactionId, createPaymentURL, createPaymentURI, additionalInfo, callback);
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

    private static void addPaymentBodyToMap(PaymentBody paymentBody, Map<String, Object> additionalInfo) {
        additionalInfo.put("transaction_id", paymentBody.getTransactionId());
        additionalInfo.put("installments", paymentBody.getInstallments());
        additionalInfo.put("issuer_id", paymentBody.getIssuerId());
        additionalInfo.put("payment_method_id", paymentBody.getPaymentMethodId());
        additionalInfo.put("pref_id", paymentBody.getPrefId());
        additionalInfo.put("token", paymentBody.getTokenId());
        additionalInfo.put("binary_mode", paymentBody.getBinaryMode());
        additionalInfo.put("public_key", paymentBody.getPublicKey());
        additionalInfo.put("email", paymentBody.getEmail());
        additionalInfo.put("payer", paymentBody.getPayer());
    }
}