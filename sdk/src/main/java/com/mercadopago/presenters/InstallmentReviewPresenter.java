package com.mercadopago.presenters;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.InstallmentReviewProvider;
import com.mercadopago.views.InstallmentReviewActivityView;

import java.math.BigDecimal;

/**
 * Created by mromar on 2/1/17.
 */

public class InstallmentReviewPresenter extends MvpPresenter<InstallmentReviewActivityView, InstallmentReviewProvider> {

    private InstallmentReviewActivityView mView;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    private PayerCost mPayerCost;
    private CardInfo mCardInfo;
    private String mBin;
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;
    private Boolean mDiscountEnabled;
    private Site mSite;

    @Override
    public void attachView(InstallmentReviewActivityView view) {
        this.mView = view;
    }

    public void initialize() {
        mView.setInstallmentAmountText();
        mView.setTotalAmountWithRateText();
        mView.showTeaPercent();
        mView.showCftPercent();

        mView.showDiscountRow(mTransactionAmount);
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public void setMerchantPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public String getPublicKey() {
        return this.mPublicKey;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public void setPayerCost(PayerCost payerCost) {
        this.mPayerCost = payerCost;
    }

    public PayerCost getPayerCost() {
        return this.mPayerCost;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;

        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public CardInfo getCardInfo() {
        return this.mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void setPayerEmail(String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public String getPayerEmail() {
        return this.mPayerEmail;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public Discount getDiscount() {
        return this.mDiscount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionAmount() {
        return this.mTransactionAmount;
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public Site getSite() {
        return this.mSite;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }
}
