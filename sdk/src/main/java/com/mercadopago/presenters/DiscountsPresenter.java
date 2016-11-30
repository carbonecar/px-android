package com.mercadopago.presenters;

import com.mercadopago.views.DiscountsView;

/**
 * Created by mromar on 11/29/16.
 */

public class DiscountsPresenter {

    protected DiscountsView mDiscountsView;

    //Activity parameters
    private String mPublicKey;
    private String mMerchantAccessToken;
    private String mPayerEmail;
    private String mMerchantBaseUrl;
    private String mMerchantDiscountsUri;

    public void attachView(DiscountsView discountsView) {
        this.mDiscountsView = discountsView;
    }

    public void initialize(String publicKey) {
        //TODO do something
    }

    public void validateParameters() throws IllegalStateException {
        //TODO do something
    }

    public void setMerchantPublicKey(String publicKey){
        this.mPublicKey = publicKey;
    }

    public void setMerchantAccessToken(String merchantAccessToken) {
        this.mMerchantAccessToken = merchantAccessToken;
    }

    public void setMerchantBaseUrl(String merchantBaseUrl) {
        this.mMerchantBaseUrl = merchantBaseUrl;
    }

    public void setMerchantDiscountsUri (String merchantDiscountsUri) {
        this.mMerchantDiscountsUri = merchantDiscountsUri;
    }

    public void setPayerEmail(String payerEmail){
        this.mPayerEmail = payerEmail;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

}
