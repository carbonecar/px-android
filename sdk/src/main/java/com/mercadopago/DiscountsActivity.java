package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewSummaryView;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

public class DiscountsActivity extends AppCompatActivity implements DiscountsView {

    // Local vars
    protected DecorationPreference mDecorationPreference;

    //View
    protected ProgressBar mProgressBar;

    protected FrameLayout mReviewSummary;
    protected MPTextView mReviewSummaryTitle;
    protected MPTextView mReviewSummaryProductAmount;
    protected MPTextView mReviewSummaryDiscountAmount;
    protected MPTextView mReviewSummaryTotalAmount;

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

        mDiscountsPresenter.initialize();
    }

    protected void validateActivityParameters() {
        mDiscountsPresenter.validateParameters();
    }

    protected void initializeControls() {
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mReviewSummary = (FrameLayout) findViewById(R.id.mpsdkReviewSummary);

        mReviewSummaryTitle = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTitle);
        mReviewSummaryProductAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryProductsAmount);
        mReviewSummaryDiscountAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryDiscountsAmount);
        mReviewSummaryTotalAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTotalAmount);
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
        mReviewSummary.setVisibility(View.VISIBLE);

        if (mDiscountsPresenter.getDiscount().getAmountOff().equals(new BigDecimal(0))) {
            String title = mDiscountsPresenter.getPercentOff() + " de descuento";
            mReviewSummaryTitle.setText(title);
        } else {
            String title = mDiscountsPresenter.getAmountOff() + " de descuento";
            mReviewSummaryTitle.setText(title);
        }

        //TODO agregar el formateo a los números
        mReviewSummaryProductAmount.setText(mDiscountsPresenter.getTransactionAmount().toString());
        mReviewSummaryDiscountAmount.setText(mDiscountsPresenter.getCouponAmount().toString());

        BigDecimal total = mDiscountsPresenter.getTransactionAmount().subtract(mDiscountsPresenter.getCouponAmount());
        String totalAmount = total.toString();

        mReviewSummaryTotalAmount.setText(totalAmount);
    }

    @Override
    public void showLoadingView() {
        //LayoutUtil.showProgressLayout(this);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mDiscountsPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
