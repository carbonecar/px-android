package com.mercadopago.presenters;

import com.mercadopago.model.Discount;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.InstallmentsReviewProvider;
import com.mercadopago.views.InstallmentsReviewView;

import java.math.BigDecimal;

/**
 * Created by mromar on 2/1/17.
 */

public class InstallmentsReviewPresenter extends MvpPresenter<InstallmentsReviewView, InstallmentsReviewProvider> {

    private InstallmentsReviewView mInstallmentsReviewView;

    //Activity parameters
    private String mPublicKey;
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;
    private Boolean mDirectDiscountEnabled;

    public void initialize() {
        //TODO installmentsss
    }

    public Discount getDiscount() {
        return this.mDiscount;
    }

    public void setMerchantPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setPayerEmail(String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public String getCurrencyId() {
        return mDiscount.getCurrencyId();
    }

    public BigDecimal getTransactionAmount() {
        return mTransactionAmount;
    }

    public String getPublicKey() {
        return this.mPublicKey;
    }
}
