package com.mercadopago.presenters;

import android.content.Context;
import android.widget.Toast;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

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
    private String mDiscountCode;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;

    private Boolean mDirectDiscountEnabled;
    private Boolean mCodeDiscountEnabled;

    public DiscountsPresenter(Context context) {
        this.mContext = context;
    }

    public void attachView(DiscountsView discountsView) {
        this.mDiscountsView = discountsView;
    }

    public void initialize() {
        if (mDiscount != null) {
            mDiscountsView.drawSummary();
        } else {
            initDiscountFlow();
        }
    }

    private void initDiscountFlow() {
        if (mDirectDiscountEnabled) {
            getDirectDiscount();
        } else if (mCodeDiscountEnabled) {
            mDiscountsView.requestDiscountCode();
        }
        else {
            mDiscountsView.finishWithCancelResult();
        }
    }

    public void validateParameters() throws IllegalStateException {
        //TODO validar todos los parámetros
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }

    private void getDirectDiscount() {
        //mDiscountsView.showLoadingView();

        //TODO no hace falta pasar la PK, la tengo en MercadoPago
        mMercadoPago.getDirectDiscount(mPublicKey, mTransactionAmount.toString(), mPayerEmail,new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                mDiscountsView.drawSummary();
            }

            @Override
            public void failure(ApiException apiException) {
                //TODO sino tiene descuento chequear si tiene código
                //TODO buscar algo más elegante para el equals ese
                if (apiException.getMessage().equals("doesn't find a campaign")) {
                    Toast.makeText(mContext, "No posee descuento", Toast.LENGTH_SHORT).show();
                    //View.askDicountCode
                }
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

    private void getCodeDiscount(String discountCode) {
        mMercadoPago.getCodeDiscount(mPublicKey, mTransactionAmount.toString(), mPayerEmail, discountCode, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                mDiscountsView.drawSummary();
            }

            @Override
            public void failure(ApiException apiException) {
                Toast.makeText(mContext, apiException.getMessage(), Toast.LENGTH_SHORT).show();

                //TODO si el código es incorrecto, mostrar
                if (apiException.getMessage().equals("doesn't find a campaign with the given code")) {
                    Toast.makeText(mContext, "No existe código", Toast.LENGTH_SHORT).show();
                    //TODO código incorrecto
                }

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

    public void validateDiscountCodeInput(String discountCode) {
        if (!isEmpty(discountCode)) {
            getCodeDiscount(discountCode);
        }
        else {
            mDiscountsView.showCodeInputError("No se ingresó codigo");
        }
    }

    public Discount getDiscount() {
        return this.mDiscount;
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

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public void setCodeDiscountEnabled(Boolean codeDiscountEnabled) {
        this.mCodeDiscountEnabled = codeDiscountEnabled;
    }

    public String getCurrencyId() {
        return mDiscount.getCurrencyId();
    }

    public BigDecimal getTransactionAmount() {
        return mTransactionAmount;
    }

    public BigDecimal getPercentOff() {
        return mDiscount.getPercentOff();
    }

    public BigDecimal getAmountOff() {
        return mDiscount.getAmountOff();
    }

    public BigDecimal getCouponAmount() {
        return mDiscount.getCouponAmount();
    }

}
