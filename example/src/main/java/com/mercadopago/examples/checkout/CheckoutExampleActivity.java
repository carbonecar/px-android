package com.mercadopago.examples.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ColorPickerDialog;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Payment;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CheckoutExampleActivity extends AppCompatActivity {

    private Activity mActivity;
    private ImageView mColorSample;
    private CheckBox mDarkFontEnabled;
    private ProgressBar mProgressBar;
    private View mRegularLayout;
    private Spinner mSpinner;
    private MPButton mContinueButton;
    private ProgressBar mContinueLoading;

    private CheckoutPreference mCheckoutPreference;
    private String mPublicKey;
    private Integer mDefaultColor;
    private Integer mSelectedColor;
    private String mCheckoutPreferenceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_example);
        mActivity = this;
        mColorSample = (ImageView) findViewById(R.id.colorSample);
        mDarkFontEnabled = (CheckBox) findViewById(R.id.darkFontEnabled);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        mSpinner = (Spinner) findViewById(R.id.mpsdkCheckoutSpinner);
        mContinueButton = (MPButton) findViewById(R.id.mpsdkContinueButton);
        mContinueLoading = (ProgressBar) findViewById(R.id.mpsdkCheckoutLoading);
        getInitialPreference();
    }

    private void initSpinner() {
        String[] countries = {"Argentina", "Brasil", "Mexico"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCountryValues(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCountryValues(int position) {
        if (position == 0) {
            mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
            mCheckoutPreferenceId = mCheckoutPreference.getId();
        } else if (position == 1) {
            mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_BR;
            mCheckoutPreferenceId = ExamplesUtils.DUMMY_MERCHANT_PREF_ID_BR;
        } else if (position == 2) {
            mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_MX;
            mCheckoutPreferenceId = ExamplesUtils.DUMMY_MERCHANT_PREF_ID_MX;
        }
    }

    public void changeColor(View view) {
        new ColorPickerDialog(this, mDefaultColor, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mDarkFontEnabled.setEnabled(true);
                mColorSample.setBackgroundColor(color);
                mSelectedColor = color;
            }
        }).show();
    }


    public void onContinueClicked(View view) {
        startMercadoPagoCheckout();
    }

    private void getInitialPreference() {
        mContinueLoading.setVisibility(View.VISIBLE);
        mContinueButton.setVisibility(View.GONE);
        showProgressLayout();
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));
        MerchantServer.createPreference(this, "http://private-9376e-paymentmethodsmla.apiary-mock.com/",
                "merchantUri/merchant_preference", map, new Callback<CheckoutPreference>() {
                    @Override
                    public void success(CheckoutPreference checkoutPreference) {
                        mCheckoutPreference = checkoutPreference;
                        mCheckoutPreferenceId = mCheckoutPreference.getId();
                        initSpinner();
                        mContinueLoading.setVisibility(View.GONE);
                        mContinueButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void failure(ApiException error) {
                        mContinueLoading.setVisibility(View.GONE);
                        showRegularLayout();
                        Toast.makeText(mActivity, getString(R.string.preference_creation_failed), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startMercadoPagoCheckout() {

        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreferenceId(mCheckoutPreferenceId)
                .setDecorationPreference(decorationPreference) //Optional
                .setMerchantBaseUrl(ExamplesUtils.DUMMY_MERCHANT_BASE_URL) //Optional
                .setMerchantGetCustomerUri(ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI) //Optional
                .setMerchantAccessToken(ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN) //Optional
                .startCheckoutActivity();
    }

    private DecorationPreference getCurrentDecorationPreference() {
        DecorationPreference decorationPreference = new DecorationPreference();
        if (mSelectedColor != null) {
            decorationPreference.setBaseColor(mSelectedColor);
            if (mDarkFontEnabled.isChecked()) {
                decorationPreference.enableDarkFont();
            }
        }
        return decorationPreference;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPago.CHECKOUT_REQUEST_CODE) {
            showRegularLayout();
            if (resultCode == RESULT_OK && data != null) {

                // Set message
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                Toast.makeText(mActivity, getString(R.string.payment_received_congrats) + payment.getId(), Toast.LENGTH_LONG).show();

            } else {
                if ((data != null) && (data.getStringExtra("mpException") != null)) {
                    MPException mpException = JsonUtil.getInstance().fromJson(data.getStringExtra("mpException"), MPException.class);
                    Toast.makeText(mActivity, mpException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showRegularLayout();
    }

    private void showRegularLayout() {
        mProgressBar.setVisibility(View.GONE);
        mRegularLayout.setVisibility(View.VISIBLE);
    }

    private void showProgressLayout() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRegularLayout.setVisibility(View.GONE);
    }

    public void resetSelection(View view) {
        mSelectedColor = null;
        mColorSample.setBackgroundColor(mDefaultColor);
        mDarkFontEnabled.setChecked(false);
        mDarkFontEnabled.setEnabled(false);
    }
}
