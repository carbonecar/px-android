package com.mercadopago.views;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountsView {

    void drawSummary();

    void requestDiscountCode();

    void finishWithResult();

    void finishWithCancelResult();

    void showCodeInputError(String message);

    void clearErrorView();

    void showProgressBar();

    void hideProgressBar();

    void showEmptyDiscountCodeError();
}
