package com.mercadopago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.DiscountsView;

public class DiscountsActivity extends AppCompatActivity implements DiscountsView {

    // Local vars
    protected DecorationPreference mDecorationPreference;

    protected DiscountsPresenter mDiscountsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();

        //TODO analizar si va
//        if (isCustomColorSet()) {
//            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
//        }

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
        mDiscountsPresenter.setMerchantAccessToken(this.getIntent().getStringExtra("merchantAccessToken"));
        mDiscountsPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mDiscountsPresenter.setMerchantBaseUrl(this.getIntent().getStringExtra("merchantBaseUrl"));
        mDiscountsPresenter.setMerchantDiscountsUri(this.getIntent().getStringExtra("merchantDiscountsUri"));
    }

    protected void setContentView() {
        setContentView(R.layout.activity_discounts);
    }

    protected void onValidStart() {
        mDiscountsPresenter.initializeMercadoPago();

        //TODO analizar si va el Timer
        //showTimer();

        mDiscountsPresenter.initialize(mDiscountsPresenter.getPublicKey());
    }

    protected void validateActivityParameters() {
        mDiscountsPresenter.validateParameters();
    }

    protected void initializeControls() {
        //TODO initialize controls
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    //TODO es de la vista
    public void drawReview() {
        //TODO draw review
    }
}
