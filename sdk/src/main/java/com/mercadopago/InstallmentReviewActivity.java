package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.InstallmentReviewPresenter;
import com.mercadopago.providers.InstallmentsReviewProviderImpl;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.InstallmentReviewActivityView;

import java.math.BigDecimal;

public class InstallmentReviewActivity extends AppCompatActivity implements InstallmentReviewActivityView, TimerObserver {

    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected FrameLayout mDiscountFrameLayout;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;

    protected MPTextView mInstallmentsAmount;
    protected MPTextView mTotalAmount;
    protected MPTextView mTeaPercent;
    protected MPTextView mCftpercent;

    protected MPTextView mTimerTextView;

    //ViewMode
    protected boolean mLowResActive;
    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;

    // Local vars
    protected DecorationPreference mDecorationPreference;

    protected InstallmentReviewPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPresenter();
        getActivityParameters();
        initializePresenter();

        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        analyzeLowRes();
        setContentView();
        initializeControls();
        onValidStart();
    }

    private void createPresenter() {
        mPresenter = new InstallmentReviewPresenter();
    }

    private void getActivityParameters() {
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        mPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentMethod"), PaymentMethod.class));
        mPresenter.setPayerCost(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("payerCost"), PayerCost.class));
        mPresenter.setCardInfo(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("cardInfo"), CardInfo.class));
        mPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
        mPresenter.setTransactionAmount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("amount"), BigDecimal.class));
        mPresenter.setSite(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class));
        mPresenter.setPayerEmail(getIntent().getStringExtra("payerEmail"));
        mPresenter.setDiscountEnabled(getIntent().getBooleanExtra("discountEnabled", true));

    }

    private void initializePresenter() {
        try {
            InstallmentsReviewProviderImpl installmentsReviewProvider = new InstallmentsReviewProviderImpl(this, mPresenter.getPublicKey());
            mPresenter.attachResourcesProvider(installmentsReviewProvider);
            mPresenter.attachView(this);
        } catch (IllegalStateException exception) {
            finishWithCancelResult();
        }
    }

    private void setContentView() {
        MPTracker.getInstance().trackScreen("INSTALLMENT_REVIEW", "2", mPresenter.getPublicKey(), mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);

        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_installment_review_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_installment_review_normal);
    }

    private  void initializeControls() {
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);

            if (CheckoutTimer.getInstance().isTimerEnabled()) {
                Toolbar.LayoutParams marginParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                marginParams.setMargins(0, 0, 0, 6);
                mLowResTitleToolbar.setLayoutParams(marginParams);
                mLowResTitleToolbar.setTextSize(19);
                mTimerTextView.setTextSize(17);
            }

            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
            mAppBar = (AppBarLayout) findViewById(R.id.mpsdkInstallmentesAppBar);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }

        mInstallmentsAmount = (MPTextView) findViewById(R.id.mpsdkInstallmentsAmount);
        mTotalAmount = (MPTextView) findViewById(R.id.mpsdkTotalAmount);
        mTeaPercent = (MPTextView) findViewById(R.id.mpsdkTeaPercent);
        mCftpercent = (MPTextView) findViewById(R.id.mpsdkCftpercent);

        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
        mDiscountFrameLayout = (FrameLayout) findViewById(R.id.mpsdkDiscount);
    }

    protected void onValidStart() {
        showTimer();
        loadViews();

        mPresenter.initialize();
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    public void analyzeLowRes() {
        if (mPresenter.isCardInfoAvailable()) {
            this.mLowResActive = ScaleUtil.isLowRes(this);
        } else {
            this.mLowResActive = true;
        }
    }

    private void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_card_installments_title));
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(getString(R.string.mpsdk_card_installments_title));

        mFrontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInfo() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInfo().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
        mFrontCardView.enableEditingCardNumber();
    }

    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
        }
    }

    @Override
    public void setInstallmentAmountText() {
        StringBuilder sb = new StringBuilder();

        sb.append(mPresenter.getPayerCost().getInstallments());
        sb.append(" ");
        sb.append(getString(R.string.mpsdk_installments_by));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(mPresenter.getPayerCost().getInstallmentAmount(), mPresenter.getSite().getCurrencyId()));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPresenter.getPayerCost().getInstallmentAmount(),
                mPresenter.getSite().getCurrencyId(), sb.toString(), false, true);

        mInstallmentsAmount.setText(spannedInstallmentsText);
    }

    @Override
    public void setTotalAmountWithRateText() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mPresenter.getPayerCost().getTotalAmount(), mPresenter.getSite().getCurrencyId()));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPresenter.getPayerCost().getTotalAmount(),
                mPresenter.getSite().getCurrencyId(), sb.toString(), false, true);

        mTotalAmount.setText(spannedFullAmountText);
    }

    @Override
    public void showTeaPercent() {
        String teaPercenct = getString(R.string.mpsdk_installments_tea) + " " + mPresenter.getPayerCost().getTEAPercent();
        mTeaPercent.setText(teaPercenct);
    }

    @Override
    public void showCftPercent() {
        String cftPercent = getString(R.string.mpsdk_installments_cft) + " " + mPresenter.getPayerCost().getCFTPercent();
        mCftpercent.setText(cftPercent);
    }

    public void onClickInstallmentOptionButton(View view) {
        finishWithResult();
    }

    @Override
    public void startDiscountActivity(BigDecimal transactionAmount) {
        MercadoPago.StartActivityBuilder mercadoPagoBuilder = new MercadoPago.StartActivityBuilder();

        mercadoPagoBuilder.setActivity(this)
                .setPublicKey(mPresenter.getPublicKey())
                .setPayerEmail(mPresenter.getPayerEmail())
                .setAmount(transactionAmount)
                .setDiscount(mPresenter.getDiscount())
                .setDecorationPreference(mDecorationPreference);

        if (mPresenter.getDiscount() == null) {
            mercadoPagoBuilder.setDirectDiscountEnabled(false);
        } else {
            mercadoPagoBuilder.setDiscount(mPresenter.getDiscount());
        }

        mercadoPagoBuilder.startDiscountsActivity();
    }

    @Override
    public void showDiscountRow(BigDecimal transactionAmount) {
        MercadoPagoUI.Views.DiscountRowViewBuilder discountRowBuilder = new MercadoPagoUI.Views.DiscountRowViewBuilder();

        discountRowBuilder.setContext(this)
                        .setTransactionAmount(transactionAmount)
                        .setDiscount(mPresenter.getDiscount())
                        .setCurrencyId(mPresenter.getSite().getCurrencyId());

        if (mPresenter.getDiscount() == null) {
            discountRowBuilder.setDiscountEnabled(false);
        }

        DiscountRowView discountRowView = discountRowBuilder.build();

        discountRowView.inflateInParent(mDiscountFrameLayout, true);
        discountRowView.initializeControls();
        discountRowView.draw();
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mPresenter.getPayerCost()));
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("INSTALLMENT_REVIEW", "BACK_PRESSED", "2", mPresenter.getPublicKey(), mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    @Override
    public void finishWithCancelResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        this.finish();
    }
}
