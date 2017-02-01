package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.presenters.InstallmentsConfirmationPresenter;
import com.mercadopago.providers.DiscountProviderImpl;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;

public class InstallmentsConfirmationActivity extends AppCompatActivity {

    // Local vars
    protected DecorationPreference mDecorationPreference;

    protected InstallmentsConfirmationPresenter mPresenter;

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
        mPresenter = new InstallmentsConfirmationPresenter();
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
            DiscountProviderImpl discountProvider = new DiscountProviderImpl(this, mPresenter.getPublicKey());
            mPresenter.attachResourcesProvider(discountProvider);
            mPresenter.attachView(this);
        } catch (IllegalStateException exception) {
            finishWithCancelResult();
        }
    }

    private void setContentView() {
        setContentView(R.layout.activity_installments_confirm);
    }

    private  void initializeControls() {
        //TODO
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
}
