package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.CheckoutActivityView;

import java.math.BigDecimal;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutPresenter {

    private Context mContext;
    private String mPublicKey;
    private CheckoutActivityView mView;
    private MercadoPagoServices mMercadoPagoServices;

    public CheckoutPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(CheckoutActivityView view) {
        this.mView = view;
    }

    public void validateActivityParameters() {
        //TODO validar
        if (true) {
            mView.onValidStart();
        } else {
            //TODO change
            mView.onInvalidStart("error");
        }
    }

    private void initializeMercadoPagoServices() {
        if (mPublicKey == null || mMercadoPagoServices != null) return;
        mMercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(mContext)
                .setPublicKey(mPublicKey)
                .build();
    }

//    public void getPaymentMethodSearch() {
//        initializeMercadoPagoServices();
//
//        //BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, boolean accountMoneyEnabled, final Callback<PaymentMethodSearch> callback
//        CheckoutPreference checkoutPreference = MercadoPagoContext.getInstance().getCheckoutPreference();
//
//        mMercadoPagoServices.getPaymentMethodSearch(checkoutPreference.getAmount(), checkoutPreference.getExcludedPaymentTypes(), checkoutPreference.getExcludedPaymentMethods(), checkoutPreference.getPayer(), false, new Callback<PaymentMethodSearch>() {
//            @Override
//            public void success(PaymentMethodSearch paymentMethodSearch) {
//                mPaymentMethodSearch = paymentMethodSearch;
//                if (!mPaymentMethodSearch.hasSavedCards() && isMerchantServerInfoAvailable()) {
//                    getCustomerAsync();
//                } else if (isActivityActive()) {
//                    startPaymentVaultActivity();
//                }
//            }
//
//            @Override
//            public void failure(ApiException apiException) {
//                if (isActivityActive()) {
//                    setFailureRecovery(new FailureRecovery() {
//                        @Override
//                        public void recover() {
//                            getPaymentMethodSearch();
//                        }
//                    });
//                    ApiUtil.showApiExceptionError(getActivity(), apiException);
//                }
//            }
//        });
//    }

}
