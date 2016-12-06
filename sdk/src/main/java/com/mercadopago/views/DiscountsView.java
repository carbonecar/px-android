package com.mercadopago.views;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountsView {

    void drawSummary();

    void askCode();

    void setDiscountCodeListener();

    void setNextButtonListeners();

    void setBackButtonListeners();

    void showLoadingView();

    void finishWithResult();
}
