package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.CustomServiceHandler;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.ApiUtil;

import java.util.Map;

/**
 * Created by vaserber on 2/2/17.
 */

public class CheckoutProviderImpl implements CheckoutProvider {

    private MercadoPagoServices mercadoPago;
    private Context mContext;
    private String mMerchantPublicKey;

    public CheckoutProviderImpl(Context context, String publicKey) throws IllegalStateException {
        if (publicKey == null) {
            throw new IllegalStateException("public key not set");
        } else if (context == null) {
            throw new IllegalStateException("context not set");
        }
        mContext = context;
        mMerchantPublicKey = publicKey;
        mercadoPago = createMercadoPago(context, publicKey);
    }

    protected MercadoPagoServices createMercadoPago(Context context, String publicKey) {
        return new MercadoPagoServices.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }

    @Override
    public void getPaymentMethodSearch(final OnResourcesRetrievedCallback<PaymentMethodSearch> resourcesRetrievedCallback) {
        CheckoutPreference checkoutPreference = MercadoPagoContext.getInstance().getCheckoutPreference();
        mercadoPago.getPaymentMethodSearch(checkoutPreference.getAmount(), checkoutPreference.getExcludedPaymentTypes(),
                checkoutPreference.getExcludedPaymentMethods(), checkoutPreference.getPayer(), false,
                new Callback<PaymentMethodSearch>() {
                    @Override
                    public void success(PaymentMethodSearch paymentMethodSearch) {
                        resourcesRetrievedCallback.onSuccess(paymentMethodSearch);
                    }

                    @Override
                    public void failure(ApiException apiException) {
//                        MPException exception = new MPException(apiException);
                        MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException);
                        resourcesRetrievedCallback.onFailure(mercadoPagoError);
                    }
                });
    }

    @Override
    public void getCustomer(final OnResourcesRetrievedCallback<Customer> resourcesRetrievedCallback) {
        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
        String getCustomerURL = servicePreference.getGetCustomerURL();
        String getCustomerURI = servicePreference.getGetCustomerURI();
        Map<String, String> additionalInfo = servicePreference.getGetCustomerAdditionalInfo();

        CustomServiceHandler.getCustomer(mContext, getCustomerURL, getCustomerURI, additionalInfo, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                resourcesRetrievedCallback.onSuccess(customer);
            }

            @Override
            public void failure(ApiException apiException) {
                MercadoPagoError mercadoPagoError = new MercadoPagoError(apiException);
                resourcesRetrievedCallback.onFailure(mercadoPagoError);
            }
        });

    }
}
