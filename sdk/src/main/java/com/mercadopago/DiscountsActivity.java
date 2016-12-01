package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewSummaryView;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

public class DiscountsActivity extends AppCompatActivity implements DiscountsView {

    // Local vars
    protected DecorationPreference mDecorationPreference;

    //View
    protected FrameLayout mReviewSummaryContainer;
    protected OnConfirmPaymentCallback mConfirmCallback;

    protected DiscountsPresenter mDiscountsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();

        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        setContentView();

        try {
            validateActivityParameters();
            initializeControls();
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void createPresenter() {
        mDiscountsPresenter = new DiscountsPresenter(getBaseContext());
        mDiscountsPresenter.attachView(this);
    }

    protected void getActivityParameters() {
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        mDiscountsPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        //TODO no va, porque el AT lo usa el merchant desde su server para ir a la API de descuentos, chequear
        //mDiscountsPresenter.setMerchantAccessToken(this.getIntent().getStringExtra("merchantAccessToken"));
        mDiscountsPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mDiscountsPresenter.setMerchantBaseUrl(this.getIntent().getStringExtra("merchantBaseUrl"));
        mDiscountsPresenter.setMerchantDiscountsUri(this.getIntent().getStringExtra("merchantDiscountsUri"));
        mDiscountsPresenter.setTransactionAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
    }

    protected void setContentView() {
        setContentView(R.layout.activity_discounts);
    }

    protected void onValidStart() {
        mDiscountsPresenter.initializeMercadoPago();

        //TODO analizar si va el Timer
        //showTimer();

        mDiscountsPresenter.initialize();
    }

    protected void validateActivityParameters() {
        mDiscountsPresenter.validateParameters();
    }

    protected void initializeControls() {
        //TODO initialize controls
        mReviewSummaryContainer = (FrameLayout) findViewById(R.id.mpsdkReviewSummaryContainer);

        mConfirmCallback = new OnConfirmPaymentCallback() {
            @Override
            public void confirmPayment() {
                //TODO ver de cambiarle el nombre al método y ver si hay que finalizar
                finishWithResult();
            }
        };
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }


    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    @Override
    public void drawSummary() {
        //TODO volar subtotal
        //TODO que evalúa si mostrar o no descuentos por el couponAmount
        mReviewSummaryContainer.removeAllViews();
        ReviewSummaryView summaryView = new ReviewSummaryView(this, mDiscountsPresenter.getCurrencyId(),
                mDiscountsPresenter.getTransactionAmount(), null, null, mDiscountsPresenter.getPercentOff(),
                mDiscountsPresenter.getCouponAmount(), mConfirmCallback, mDecorationPreference);
        summaryView.inflateInParent(mReviewSummaryContainer, true);
        summaryView.initializeControls();
        summaryView.drawSummary();
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mDiscountsPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
