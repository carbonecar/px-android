package com.mercadopago.core;

import android.content.Context;
import android.content.Intent;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.DeprecatedCheckoutActivity;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoCheckout {

    private Context context;
    private String publicKey;
    private CheckoutPreference checkoutPreference;
    private DecorationPreference decorationPreference;
    private ServicePreference servicePreference;
    private FlowPreference flowPreference;

    private MercadoPagoCheckout(Builder builder) {
        this.context = builder.context;
        this.publicKey = builder.publicKey;
        this.checkoutPreference = builder.checkoutPreference;
        this.decorationPreference = builder.decorationPreference;
        this.servicePreference = builder.servicePreference;
        this.flowPreference = builder.flowPreference;
        validate();
    }

    private void validate() throws IllegalStateException {
        // TODO Implement
        //valido todas las preferencias

        //1 checkout preference con un id, sin service preference create pref --> pago con beta
        //2 lo mismo pero con un servidor del integrador para hacer el pago y creando una checkout pref local
        //3 si en vez de poner un paymentcallback, pone un paymentdatacallback,
    }

    private void start(PaymentCallback paymentCallback) {
        attachCheckoutCallback(paymentCallback);
        startCheckoutActivity();
    }

    private void start(PaymentDataCallback paymentDataCallback) {
        attachCheckoutCallback(paymentDataCallback);
        startCheckoutActivity();
    }

    private void attachCheckoutCallback(PaymentCallback paymentCallback) {
        CallbackHolder.getInstance().setPaymentCallback(paymentCallback);
    }

    private void attachCheckoutCallback(PaymentDataCallback paymentDataCallback) {
        CallbackHolder.getInstance().setPaymentDataCallback(paymentDataCallback);
    }

    private void startCheckoutActivity() {
        Intent checkoutIntent = new Intent(context, CheckoutActivity.class);
        checkoutIntent.putExtra("publicKey", publicKey);
        checkoutIntent.putExtra("checkoutPreference", JsonUtil.getInstance().toJson(checkoutPreference));
        checkoutIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        checkoutIntent.putExtra("servicePreference", JsonUtil.getInstance().toJson(servicePreference));
        checkoutIntent.putExtra("flowPreference", JsonUtil.getInstance().toJson(flowPreference));
        context.startActivity(checkoutIntent);
    }

    public static class Builder {
        private Context context;
        private String publicKey;
        private CheckoutPreference checkoutPreference;
        private DecorationPreference decorationPreference;
        private ServicePreference servicePreference;
        private FlowPreference flowPreference;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setCheckoutPreference(CheckoutPreference checkoutPreference) {
            this.checkoutPreference = checkoutPreference;
            return this;
        }

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.decorationPreference = decorationPreference;
            return this;
        }

        public Builder setServicePreference(ServicePreference servicePreference) {
            this.servicePreference = servicePreference;
            return this;
        }

        public Builder setFlowPreference(FlowPreference flowPreference) {
            this.flowPreference = flowPreference;
            return this;
        }

        public void start(PaymentCallback paymentCallback) {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.start(paymentCallback);

        }

        public void start(PaymentDataCallback paymentDataCallback) {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.start(paymentDataCallback);
        }
    }
}
