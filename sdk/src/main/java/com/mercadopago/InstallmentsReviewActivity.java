package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.InstallmentsReviewPresenter;
import com.mercadopago.providers.DiscountProviderImpl;
import com.mercadopago.providers.InstallmentsReviewProviderImpl;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.DiscountsView;
import com.mercadopago.views.InstallmentsActivityView;
import com.mercadopago.views.InstallmentsReviewView;

import java.math.BigDecimal;

public class InstallmentsReviewActivity extends AppCompatActivity implements InstallmentsReviewView, TimerObserver {

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
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        mPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mPresenter.setTransactionAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
        mPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
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
        setContentView(R.layout.activity_installments_confirm);
    }

    private  void initializeControls() {
        //TODO installments
    }

    protected void onValidStart() {
        //TODO installments
//        showTimer();

        mPresenter.initialize();
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
        //TODO installments review add
    }

    @Override
    public void onFinish() {
        //TODO installments review add
    }
}
