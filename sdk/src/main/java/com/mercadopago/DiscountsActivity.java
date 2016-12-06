package com.mercadopago;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.card.CardSecurityCodeEditTextCallback;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.card.CardSecurityCodeTextWatcher;
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
    //protected ProgressBar mProgressBar;
    protected FrameLayout mReviewDiscountSummaryContainer;
    protected FrameLayout mDiscountCodeContainer;
    protected FrameLayout mNextButton;
    protected FrameLayout mBackButton;
    protected LinearLayout mDiscountLinearLayout;
    protected MPTextView mReviewSummaryTitle;
    protected MPTextView mReviewSummaryProductAmount;
    protected MPTextView mReviewSummaryDiscountAmount;
    protected MPTextView mReviewSummaryTotalAmount;
    protected TextView mNextButtonText;
    protected TextView mBackButtonText;
    protected MPEditText mDiscountCodeEditText;

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
        mDiscountLinearLayout = (LinearLayout) findViewById(R.id.mpsdkDiscountLinearLayout);

        mReviewDiscountSummaryContainer = (FrameLayout) findViewById(R.id.mpsdkReviewDiscountSummaryContainer);
        mDiscountCodeContainer = (FrameLayout) findViewById(R.id.mpsdkDiscountCodeContainer);

        //Review discount summary
        mReviewSummaryTitle = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTitle);
        mReviewSummaryProductAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryProductsAmount);
        mReviewSummaryDiscountAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryDiscountsAmount);
        mReviewSummaryTotalAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTotalAmount);

        //Discount code input
        mDiscountCodeEditText = (MPEditText) findViewById(R.id.mpsdkDiscountCode);
        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButtonText = (MPTextView) findViewById(R.id.mpsdkNextButtonText);
        mBackButtonText = (MPTextView) findViewById(R.id.mpsdkBackButtonText);

        mNextButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence discountCode = mDiscountCodeEditText.getText();
                mDiscountsPresenter.validateDiscountCodeInput(discountCode.toString());
            }
        });


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
        //mDiscountLinearLayout.setBackgroundColor(Color.parseColor("#009EE3"));
        mDiscountCodeContainer.setVisibility(View.GONE);
        mReviewDiscountSummaryContainer.setVisibility(View.VISIBLE);

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
    public void requestDiscountCode() {
        mReviewDiscountSummaryContainer.setVisibility(View.GONE);
        mDiscountCodeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNextButtonListeners() {
        mNextButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence discountCode = mNextButtonText.getText();
                mDiscountsPresenter.validateDiscountCodeInput(discountCode.toString());
            }
        });
    }

    @Override
    public void setBackButtonListeners() {
        mBackButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO somenthing, finish with cancel result
            }
        });
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mDiscountsPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
