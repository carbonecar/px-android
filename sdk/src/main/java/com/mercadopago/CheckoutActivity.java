package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
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
        if (servicePreference == null) {
            ServicePreference defaultServicePreference = new ServicePreference.Builder()
                    //TODO poner nuestras urls default
//                    .setCreateCheckoutPreferenceURL()
//                    .setCreatePaymentURL()
                    .build();
            servicePreference = defaultServicePreference;
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE) {
//            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_RESULT_REQUEST_CODE) {
//            resolvePaymentResultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE) {
//            resolveCardVaultRequest(resultCode, data);
        } else {
//            resolveErrorRequest(resultCode, data);
        }
    }

//    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//
//            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
//            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
//            mCreatedToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
//            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
//            MPTracker.getInstance().trackScreen("REVIEW_AND_CONFIRM", "3", mMerchantPublicKey, mCheckoutPreference.getSiteId(), BuildConfig.VERSION_NAME, this);
//            showReviewAndConfirm();
//            stopProgressBar();
//        } else {
//            if (!mPaymentMethodEditionRequested) {
//                Intent returnIntent = new Intent();
//                setResult(RESULT_CANCELED, returnIntent);
//                finish();
//            } else {
//                animateBackFromPaymentEdition();
//            }
//        }
//    }
}
