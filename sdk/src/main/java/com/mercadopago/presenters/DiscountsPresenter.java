package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Token;
import com.mercadopago.views.DiscountsView;

/**
 * Created by mromar on 11/29/16.
 */

public class DiscountsPresenter {

    private DiscountsView mDiscountsView;
    private Context mContext;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Activity parameters
    private String mPublicKey;
    private String mMerchantAccessToken;
    private String mPayerEmail;
    private String mMerchantBaseUrl;
    private String mMerchantDiscountsUri;

    public DiscountsPresenter(Context context) {
        this.mContext = context;
    }

    public void attachView(DiscountsView discountsView) {
        this.mDiscountsView = discountsView;
    }

    public void initialize(String publicKey) {
        //TODO do something
        getDiscount();
    }

    public void validateParameters() throws IllegalStateException {
        //TODO do something
        //is valid call getDiscount()
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
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

    private void getDiscount() {
        //TODO agregar método en la view
        //mDiscountsView.showLoadingView();

        mMercadoPago.getDiscount(new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                //TODO askCode
                mDiscountsView.drawReview();
            }

            @Override
            public void failure(ApiException apiException) {
//                setFailureRecovery(new FailureRecovery() {
//                    @Override
//                    public void recover() {
//                        cloneToken();
//                    }
//                });
                //mView.stopLoadingView();
                //mView.showApiExceptionError(apiException);
            }
        });

    }
}
