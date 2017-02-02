package com.mercadopago.presenters;

import android.util.Log;

import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.providers.CheckoutProvider;
import com.mercadopago.views.CheckoutActivityView;

import java.util.List;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutPresenter extends MvpPresenter<CheckoutActivityView, CheckoutProvider> {

    private PaymentMethodSearch mPaymentMethodSearch;
    private String mCustomerId;
    private List<Card> mSavedCards;

    public void start() {
        getPaymentMethodSearchAsync();
    }

    private void getPaymentMethodSearchAsync() {
//        getView().showProgress();

        getResourcesProvider().getPaymentMethodSearch(new OnResourcesRetrievedCallback<PaymentMethodSearch>() {
            @Override
            public void onSuccess(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;
                if (needsToLoadSavedCards()) {
                    getCustomerAsync();
                } else {
//                } else if (isActivityActive()) {
                    getView().startPaymentVaultActivity();
                }
            }

            @Override
            public void onFailure(MercadoPagoError exception) {
                //TODO
                Log.d("log", "failure get payment method search");
//                if (isActivityActive()) {
//                    setFailureRecovery(new FailureRecovery() {
//                        @Override
//                        public void recover() {
//                            getPaymentMethodSearchAsync();
//                        }
//                    });
//                    ApiUtil.showApiExceptionError(getActivity(), apiException);
//                }
            }
        });
    }

    public PaymentMethodSearch getPaymentMethodSearch() {
        return mPaymentMethodSearch;
    }

    private boolean needsToLoadSavedCards() {
        ServicePreference servicePreference = MercadoPagoContext.getInstance().getServicePreference();
        return !mPaymentMethodSearch.hasSavedCards() && servicePreference.hasGetCustomerURL();
    }

    private void getCustomerAsync() {
        //        getView().showProgress();

        getResourcesProvider().getCustomer(new OnResourcesRetrievedCallback<Customer>() {
            @Override
            public void onSuccess(Customer customer) {
                if (customer != null) {
                    mCustomerId = customer.getId();
                    CheckoutPreference checkoutPreference = MercadoPagoContext.getInstance().getCheckoutPreference();
                    mSavedCards = checkoutPreference.getPaymentPreference() == null ? customer.getCards() : checkoutPreference.getPaymentPreference().getValidCards(customer.getCards());
                }
                getView().startPaymentVaultActivity();
            }

            @Override
            public void onFailure(MercadoPagoError exception) {
                getView().startPaymentVaultActivity();
            }
        });
    }

    public List<Card> getSavedCards() {
        return mSavedCards;
    }
}
