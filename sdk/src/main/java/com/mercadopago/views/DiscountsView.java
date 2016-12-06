package com.mercadopago.views;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountsView {

    void drawSummary();

    void requestDiscountCode();

    void setNextButtonListeners();

    void setBackButtonListeners();

    void finishWithResult();
}
