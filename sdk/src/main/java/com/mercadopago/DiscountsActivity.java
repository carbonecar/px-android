package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

public class DiscountsActivity extends AppCompatActivity implements DiscountsView {

    // Local vars
    protected DecorationPreference mDecorationPreference;

    //View
    protected FrameLayout mReviewDiscountSummaryContainer;
    protected FrameLayout mDiscountCodeContainer;
    protected FrameLayout mNextButton;
    protected FrameLayout mBackButton;
    protected FrameLayout mErrorContainer;
    protected LinearLayout mDiscountLinearLayout;
    protected MPTextView mReviewSummaryTitle;
    protected MPTextView mReviewSummaryProductAmount;
    protected MPTextView mReviewSummaryDiscountAmount;
    protected MPTextView mReviewSummaryTotalAmount;
    protected MPTextView mErrorTextView;
    protected TextView mNextButtonText;
    protected TextView mBackButtonText;
    protected MPEditText mDiscountCodeEditText;

    protected Toolbar mToolbar;

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
        mDiscountsPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mDiscountsPresenter.setMerchantBaseUrl(this.getIntent().getStringExtra("merchantBaseUrl"));
        mDiscountsPresenter.setMerchantDiscountsUri(this.getIntent().getStringExtra("merchantDiscountsUri"));
        mDiscountsPresenter.setTransactionAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
        mDiscountsPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
        mDiscountsPresenter.setDirectDiscountEnabled(this.getIntent().getBooleanExtra("directDiscountEnabled", true));
        mDiscountsPresenter.setCodeDiscountEnabled(this.getIntent().getBooleanExtra("codeDiscountEnabled", true));
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
        mBackButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDiscountCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearErrorView();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mErrorContainer = (FrameLayout) findViewById(R.id.mpsdkErrorContainer);
        mErrorTextView = (MPTextView) findViewById(R.id.mpsdkErrorTextView);

        initializeToolbar();
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDiscountsPresenter.getDiscount() == null) {
                    onBackPressed();
                } else {
                    finishWithResult();
                }
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
        mToolbar.setNavigationIcon(R.drawable.mpsdk_close);
        mDiscountCodeContainer.setVisibility(View.GONE);
        mReviewDiscountSummaryContainer.setVisibility(View.VISIBLE);

        showSummaryTitle();
        showTransactionRow();
        showDiscountRow();
        showTotalRow();
    }

    private void showTotalRow() {
        BigDecimal total = mDiscountsPresenter.getDiscount().getTransactionAmount().subtract(mDiscountsPresenter.getCouponAmount());
        Spanned formattedTotalAmount = CurrenciesUtil.formatNumber( total, mDiscountsPresenter.getDiscount().getCurrencyId(), false, true);
        mReviewSummaryTotalAmount.setText(formattedTotalAmount);
    }

    private void showDiscountRow() {
        StringBuilder formattedDiscountAmountBuilder = new StringBuilder();
        Spanned spannedDiscountAmount;

        formattedDiscountAmountBuilder.append("-");
        formattedDiscountAmountBuilder.append(CurrenciesUtil.formatNumber(mDiscountsPresenter.getCouponAmount(), mDiscountsPresenter.getCurrencyId()));
        spannedDiscountAmount = CurrenciesUtil.formatCurrencyInText(mDiscountsPresenter.getCouponAmount(), mDiscountsPresenter.getCurrencyId(), formattedDiscountAmountBuilder.toString(), false, true);
        mReviewSummaryDiscountAmount.setText(spannedDiscountAmount);
    }

    private void showTransactionRow() {
        Spanned formattedTransactionAmount = CurrenciesUtil.formatNumber(mDiscountsPresenter.getDiscount().getTransactionAmount(),mDiscountsPresenter.getDiscount().getCurrencyId(), false, true);
        mReviewSummaryProductAmount.setText(formattedTransactionAmount);
    }

    private void showSummaryTitle() {
        if (mDiscountsPresenter.getDiscount().getAmountOff().equals(new BigDecimal(0))) {
            String title = mDiscountsPresenter.getDiscount().getPercentOff() + getString(R.string.mpsdk_percent_of_discount);
            mReviewSummaryTitle.setText(title);
        } else {
            StringBuilder formattedTitle = new StringBuilder();
            formattedTitle.append(CurrenciesUtil.formatNumber(mDiscountsPresenter.getDiscount().getAmountOff(), mDiscountsPresenter.getCurrencyId()));
            formattedTitle.append(" ");
            formattedTitle.append(getString(R.string.mpsdk_of_discount));
            Spanned spannedFullText = CurrenciesUtil.formatCurrencyInText(mDiscountsPresenter.getDiscount().getAmountOff(), mDiscountsPresenter.getDiscount().getCurrencyId(), formattedTitle.toString(), false, true);
            mReviewSummaryTitle.setText(spannedFullText);
        }
    }

    @Override
    public void requestDiscountCode() {
        mReviewDiscountSummaryContainer.setVisibility(View.GONE);
        mDiscountCodeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCodeInputError(String message) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTextView.setText(message);
    }

    @Override
    public void clearErrorView() {
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText("");
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mDiscountsPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void finishWithCancelResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
