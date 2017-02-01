package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.InstallmentsReviewPresenter;
import com.mercadopago.providers.InstallmentsReviewProviderImpl;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.InstallmentsReviewActivityView;

public class InstallmentsReviewActivity extends AppCompatActivity implements InstallmentsReviewActivityView, TimerObserver {

    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;

    protected MPTextView mTimerTextView;

    // Local vars
    protected DecorationPreference mDecorationPreference;

    protected InstallmentsReviewPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPresenter();
        getActivityParameters();
        initializePresenter();

        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        setContentView();
        initializeControls();
        onValidStart();
    }

    private void createPresenter() {
        mPresenter = new InstallmentsReviewPresenter();
    }

    private void getActivityParameters() {
        //TODO installments agregar los parámetros en mercado pago, faltan algunos
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        mPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentMethod"), PaymentMethod.class));
        mPresenter.setPayerCost(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("payerCost"), PayerCost.class));
        mPresenter.setCardInfo(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("cardInfo"), CardInfo.class));
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
        setContentView(R.layout.activity_installments_review);
    }

    private  void initializeControls() {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkInstallmentesAppBar);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
        mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mNormalToolbar.setVisibility(View.VISIBLE);
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

    private void loadViews() {
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

                    //TODO installments, analizar si va
//                    returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
        }
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
