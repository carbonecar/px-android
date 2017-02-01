package com.mercadopago.presenters;

import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.InstallmentsReviewProvider;
import com.mercadopago.views.InstallmentsReviewActivityView;

/**
 * Created by mromar on 2/1/17.
 */

public class InstallmentsReviewPresenter extends MvpPresenter<InstallmentsReviewActivityView, InstallmentsReviewProvider> {

    private InstallmentsReviewActivityView mView;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    private PayerCost mPayerCost;
    private CardInfo mCardInfo;
    private String mBin;

    public void initialize() {
        //TODO installmentsss
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
}
