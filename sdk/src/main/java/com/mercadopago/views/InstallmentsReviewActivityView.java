package com.mercadopago.views;

import com.mercadopago.mvp.MvpView;

import java.math.BigDecimal;

/**
 * Created by mromar on 2/1/17.
 */

public interface InstallmentsReviewActivityView extends MvpView {

    void finishWithCancelResult();

    void showInstallmentAmount();

    void showTotalAmount();

    void showTeaPercent();

    void showCftPercent();

    void finishWithResult();

    void startDiscountActivity(BigDecimal mTransactionAmount);

    void showDiscountRow(BigDecimal mTransactionAmount);
}
