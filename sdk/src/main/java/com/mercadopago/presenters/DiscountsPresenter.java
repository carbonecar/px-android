package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.R;
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
    private String mMerchantDiscountUri;
    private String mDiscountCode;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;

    //Errors
    private static final String DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH = "amount-doesnt-match";
    private static final String DISCOUNT_ERROR_RUN_OUT_OF_USES = "run out of uses";
    private static final String DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH = "campaign-doesnt-match";
    private static final String DISCOUNT_ERROR_CAMPAIGN_CODE_DOESNT_MATCH = "campaign-code-doesnt-match";

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
        if (mDirectDiscountEnabled && areDiscountParametersValid()) {
            getDirectDiscount();
        } else {
            mDiscountsView.requestDiscountCode();
        }
    }

    private Boolean areDiscountParametersValid() {
        return !isEmpty(mPayerEmail) && mTransactionAmount != null && mTransactionAmount.compareTo(BigDecimal.ZERO)>0;
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }

    private void getDirectDiscount() {
        mMercadoPago.getDirectDiscount(mTransactionAmount.toString(), mPayerEmail,new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                mDiscountsView.drawSummary();
            }

            @Override
            public void failure(ApiException apiException) {
                mDiscountsView.requestDiscountCode();
                }
        });
    }

    private void getCodeDiscount(final String discountCode) {
        mDiscountsView.showProgressBar();

        mMercadoPago.getCodeDiscount(mTransactionAmount.toString(), mPayerEmail, discountCode, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscountsView.setSoftInputModeSummary();
                mDiscountsView.hideKeyboard();
                mDiscountsView.hideProgressBar();
                mDiscount = discount;
                mDiscount.setCouponCode(discountCode);
                applyAmountDiscount();
                mDiscountsView.drawSummary();
            }

            @Override
            public void failure(ApiException apiException) {
                mDiscountsView.hideProgressBar();

                if (apiException.getError().equals(DISCOUNT_ERROR_CAMPAIGN_CODE_DOESNT_MATCH)) {
                    mDiscountsView.showCodeInputError(mContext.getString(R.string.mpsdk_invalid_code));
                } else if (apiException.getError().equals(DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH)) {
                    mDiscountsView.showCodeInputError(mContext.getString(R.string.mpsdk_merchant_without_discount_available));
                } else if (apiException.getError().equals(DISCOUNT_ERROR_RUN_OUT_OF_USES)) {
                    mDiscountsView.showCodeInputError(mContext.getString(R.string.mpsdk_ran_out_of_quantity_uses_discount));
                } else if (apiException.getError().equals(DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH)) {
                    mDiscountsView.showCodeInputError(mContext.getString(R.string.mpsdk_amount_doesnt_match));
                } else {
                    mDiscountsView.finishWithCancelResult();
                }
            }
        });
    }

    public void applyAmountDiscount() {
        mDiscount.setTransactionAmount(mTransactionAmount);
    }

    public void validateDiscountCodeInput(String discountCode) {
        if (areDiscountParametersValid()) {
            if (!isEmpty(discountCode)) {
                getCodeDiscount(discountCode);
            }
            else {
                mDiscountsView.showEmptyDiscountCodeError();
            }
        } else {
            mDiscountsView.finishWithCancelResult();
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
        this.mMerchantDiscountUri = merchantDiscountsUri;
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

    public String getPublicKey() {
        return this.mPublicKey;
    }

}
