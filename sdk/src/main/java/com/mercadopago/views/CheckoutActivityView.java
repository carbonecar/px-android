package com.mercadopago.views;

/**
 * Created by vaserber on 2/1/17.
 */

public interface CheckoutActivityView {
    void onValidStart();
    void onInvalidStart(String message);
}
