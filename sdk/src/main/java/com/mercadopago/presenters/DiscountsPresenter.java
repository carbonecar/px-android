package com.mercadopago.presenters;

import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.providers.DiscountsProvider;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

/**
 * Created by mromar on 11/29/16.
 */

public class DiscountsPresenter extends MvpPresenter<DiscountsView, DiscountsProvider> {

    private DiscountsView mDiscountsView;

    //Activity parameters
    private String mPublicKey;
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;

    private Boolean mDirectDiscountEnabled;

    @Override
    public void attachView(DiscountsView discountsView) {
        this.mDiscountsView = discountsView;
    }

    public void initialize() {
        if (mDiscount == null) {
            initDiscountFlow();
        } else {
            drawSummary();
        }
    }

    private void initDiscountFlow() {
        if (mDirectDiscountEnabled && isAmountValid()) {
            getDirectDiscount();
        } else {
            mDiscountsView.requestDiscountCode();
        }
    }

    private Boolean isAmountValid() {
        return mTransactionAmount != null && mTransactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void drawSummary() {
        //TODO discounts validar parámetros
        mDiscountsView.drawSummary();
    }

    private void getDirectDiscount() {
        getResourcesProvider().getDirectDiscount(mTransactionAmount.toString(), mPayerEmail, new OnResourcesRetrievedCallback<Discount>() {

            @Override
            public void onSuccess(Discount discount) {
                mDiscount = discount;
                mDiscountsView.drawSummary();

            }

            @Override
            public void onFailure(MPException exception) {
                mDiscountsView.requestDiscountCode();
            }
        });
    }

    private void getCodeDiscount(final String discountCode) {
        mDiscountsView.showProgressBar();

        getResourcesProvider().getCodeDiscount(mTransactionAmount.toString(), mPayerEmail, discountCode, new OnResourcesRetrievedCallback<Discount>() {

            @Override
            public void onSuccess(Discount discount) {
                mDiscountsView.setSoftInputModeSummary();
                mDiscountsView.hideKeyboard();
                mDiscountsView.hideProgressBar();

                mDiscount = discount;
                mDiscount.setCouponCode(discountCode);
                mDiscountsView.drawSummary();
            }

            @Override
            public void onFailure(MPException exception) {
                mDiscountsView.hideProgressBar();
                if(exception.isApiException()) {
                    String errorMessage = getResourcesProvider().getApiErrorMessage(exception.getApiException().getError());
                    mDiscountsView.showCodeInputError(errorMessage);
                } else {
                    mDiscountsView.showCodeInputError(getResourcesProvider().getStandardErrorMessage());
                }
            }
        });
    }

    public void validateDiscountCodeInput(String discountCode) {
        if (isAmountValid()) {
            if (isEmpty(discountCode)) {
                mDiscountsView.showEmptyDiscountCodeError();
            } else {
                getCodeDiscount(discountCode);
            }
        } else {
            mDiscountsView.finishWithCancelResult();
        }
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

    public BigDecimal getCouponAmount() {
        return mDiscount.getCouponAmount();
    }

    public String getPublicKey() {
        return this.mPublicKey;
    }

    private boolean isEmpty(String discountCode) {
        return discountCode == null || discountCode.isEmpty();
    }

}
