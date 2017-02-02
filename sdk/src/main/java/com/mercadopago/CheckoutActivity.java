package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.presenters.CheckoutPresenter;
import com.mercadopago.providers.CheckoutProvider;
import com.mercadopago.providers.CheckoutProviderImpl;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.CheckoutActivityView;

import java.math.BigDecimal;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutActivity extends AppCompatActivity implements CheckoutActivityView {

    protected CheckoutPresenter mPresenter;
    protected Activity mActivity;
    protected CheckoutProvider mResourcesProvider;

    protected FlowPreference mFlowPreference;
    protected Site mSite;
    protected String mMerchantPublicKey;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new CheckoutPresenter();
        }
        mActivity = this;
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");

        try {
            mResourcesProvider = new CheckoutProviderImpl(this, mMerchantPublicKey);
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void onValidStart() {

        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(mResourcesProvider);

        getActivityParameters();
//        setContentView();

        mPresenter.start();
    }

    private void getActivityParameters() {
        CheckoutPreference checkoutPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("checkoutPreference"), CheckoutPreference.class);
        DecorationPreference decorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        ServicePreference servicePreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("servicePreference"), ServicePreference.class);
        mFlowPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("flowPreference"), FlowPreference.class);

        mSite = new Site(checkoutPreference.getSiteId(), checkoutPreference.getItems().get(0).getCurrencyId());

        initializeMercadoPagoContext(decorationPreference, servicePreference, checkoutPreference);
    }

    private void initializeMercadoPagoContext(DecorationPreference decorationPreference,
                                              ServicePreference servicePreference, CheckoutPreference checkoutPreference) {
        new MercadoPagoContext.Builder()
                .setPublicKey(mMerchantPublicKey)
                .setDecorationPreference(decorationPreference)
                .setServicePreference(servicePreference)
                .setCheckoutPreference(checkoutPreference)
                .initialize();
    }



//    private void onValidStart() {
//        CheckoutPreference checkoutPreference = MercadoPagoContext.getInstance().getCheckoutPreference();
//        if (checkoutPreference != null && checkoutPreference.getSiteId() != null && checkoutPreference.getItems() != null
//                && !checkoutPreference.getItems().isEmpty()) {
//            mSite = new Site(checkoutPreference.getSiteId(), checkoutPreference.getItems().get(0).getCurrencyId());
//        }
//        startPaymentVaultActivity();
//    }

    private void onInvalidStart(String message) {

    }

//    private void setContentView() {
//        setContentView(R.layout.mpsdk_activity_checkout);
//    }

    @Override
    public void startPaymentVaultActivity() {
        BigDecimal amount = MercadoPagoContext.getInstance().getCheckoutPreference().getAmount();
        new MercadoPagoComponents.Activities.PaymentVaultActivityBuilder()
                .setActivity(this)
                .setSite(mSite)
                .setAmount(amount)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPaymentMethodSearch(mPresenter.getPaymentMethodSearch())
                .setCards(mPresenter.getSavedCards())
                .startActivity();
    }
}
