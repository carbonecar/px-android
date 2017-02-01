package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.CheckoutActivityView;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutPresenter {

    private Context mContext;
    private CheckoutActivityView mView;

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

}
