package com.mercadopago;

import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.IllegalFormatCodePointException;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends MercadoPagoActivity {

    // Controls
    protected MPTextView mLastFourDigitsDescription;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mInterestAmountDescription;
    protected MPTextView mTotalAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mPaymentIdDescriptionNumber;
    protected MPTextView mPayerEmail;
    protected MPTextView mDiscountOff;
    protected MPTextView mTotalDescription;
    protected MPTextView mTotalAmount;
    protected MPTextView mDiscountAmount;
    protected View mTopEmailSeparator;
    protected View mDiscountSeparator;
    protected ImageView mPaymentMethodImage;
    protected ImageView mDiscountArrow;
    protected MPTextView mKeepBuyingButton;
    protected LinearLayout mDiscountRow;
    protected LinearLayout mHasDirectDiscount;

    // Activity parameters
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;
    protected Discount mDiscount;

    //Local values
    private boolean mBackPressedOnce;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
        mDiscount = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("discount"), Discount.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if (mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CONGRATS", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());

        setContentView(R.layout.mpsdk_activity_congrats);
    }

    @Override
    protected void initializeControls() {
        mPayerEmail = (MPTextView) findViewById(R.id.mpsdkPayerEmail);
        mLastFourDigitsDescription = (MPTextView) findViewById(R.id.mpsdkLastFourDigitsDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.mpsdkInstallmentsDescription);
        mInterestAmountDescription = (MPTextView) findViewById(R.id.mpsdkInterestAmountDescription);
        mTotalAmountDescription = (MPTextView) findViewById(R.id.mpsdkTotalAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.mpsdkPaymentIdDescription);
        mPaymentIdDescriptionNumber = (MPTextView) findViewById(R.id.mpsdkPaymentIdDescriptionNumber);
        mTopEmailSeparator = findViewById(R.id.mpsdkTopEmailSeparator);
        mPaymentMethodImage = (ImageView) findViewById(R.id.mpsdkPaymentMethodImage);
        mKeepBuyingButton = (MPTextView) findViewById(R.id.mpsdkKeepBuyingCongrats);
        mKeepBuyingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });

        //Discount
        mDiscountRow = (LinearLayout) findViewById(R.id.mpsdkDiscountRow);
        mDiscountAmount = (MPTextView) findViewById(R.id.mpsdkDiscountAmount);
        mHasDirectDiscount = (LinearLayout) findViewById(R.id.mpsdkHasDirectDiscount);
        mDiscountArrow = (ImageView) findViewById(R.id.mpsdkDiscountArrow);
        mDiscountSeparator = findViewById(R.id.mpsdkDiscountSeparator);
        mTotalAmount = (MPTextView) findViewById(R.id.mpsdkTotalAmount);
        mDiscountOff = (MPTextView) findViewById(R.id.mpsdkDiscountOff);
    }

    @Override
    protected void onValidStart() {
        setPaymentEmailDescription();
        setLastFourDigitsCard();
        setDiscountRow();
        setInstallmentsDescription();
        setPaymentIdDescription();
    }

    @Override
    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false);
    }

    private void setPaymentIdDescription() {
        if (isPaymentIdValid()) {
            String message = getString(R.string.mpsdk_payment_id_description_number, String.valueOf(mPayment.getId()));
            mPaymentIdDescriptionNumber.setText(message);
        } else {
            mPaymentIdDescription.setVisibility(View.GONE);
            mPaymentIdDescriptionNumber.setVisibility(View.GONE);
        }
    }

    private void setInterestAmountDescription() {
        setTotalAmountDescription();

        if (hasInterests()) {
            mInterestAmountDescription.setVisibility(View.GONE);
        } else {
            mInterestAmountDescription.setText(getString(R.string.mpsdk_zero_rate));
            mInstallmentsDescription.setVisibility(View.VISIBLE);
        }
    }

    private void setDiscountRow() {
        if (mDiscount != null) {
            setRowVisibility();
            setTotalAmountWithoutDiscount();
            setTotalAmountWithDiscount();
            setDiscountOff();
        }
    }

    private void setTotalAmountWithDiscount() {
        Spanned formattedText = CurrenciesUtil.formatNumber(mDiscount.getTransactionAmountWithDiscount(), mPayment.getCurrencyId(),false,true);
        mDiscountAmount.setText(formattedText);
    }

    private void setTotalAmountWithoutDiscount() {
        Spanned formattedText = CurrenciesUtil.formatNumber(mDiscount.getTransactionAmount(), mPayment.getCurrencyId(),false,true);
        mTotalAmount.setText(formattedText);
        mTotalAmount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void setRowVisibility() {
        mDiscountRow.setVisibility(View.VISIBLE);
        mDiscountAmount.setVisibility(View.VISIBLE);
        mHasDirectDiscount.setVisibility(View.VISIBLE);
        mDiscountArrow.setVisibility(View.GONE);
        mDiscountSeparator.setVisibility(View.INVISIBLE);
    }

    private void setDiscountOff() {
        if (mDiscount.getAmountOff() != null && mDiscount.getAmountOff().compareTo(BigDecimal.ZERO)>0) {
            String discountOff = "$" + mDiscount.getAmountOff();
            mDiscountOff.setText(discountOff);
        } else {
            String discountOff = mDiscount.getPercentOff() + "%";
            mDiscountOff.setText(discountOff);
        }
    }

    private void setTotalAmountDescription() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                mPayment.getCurrencyId(), sb.toString(), false, true);

        mDiscountRow.setVisibility(View.GONE);
        mTotalAmountDescription.setVisibility(View.VISIBLE);

        mTotalAmountDescription.setText(spannedFullAmountText);
    }

    private boolean hasInterests() {
        if (mPayment.getFeeDetails() != null && mPayment.getFeeDetails().size() > 0) {
            for (int i = 0; i < mPayment.getFeeDetails().size(); i++) {
                if (mPayment.getFeeDetails().get(i).isFinancialFree()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setInstallmentsDescription() {
        if (isInstallmentQuantityValid() && isInstallmentAmountValid() && isTotalPaidAmountValid() && CurrenciesUtil.isValidCurrency(mPayment.getCurrencyId())) {
            if (mPayment.getInstallments() > 1) {
                mInstallmentsDescription.setText(getInstallmentsText());
                setInterestAmountDescription();
            } else {
                //Installments quantity 0 or 1
                StringBuilder sb = new StringBuilder();
                sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));

                Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                        mPayment.getCurrencyId(), sb.toString(), false, true);

                mInstallmentsDescription.setText(spannedInstallmentsText);

                mInterestAmountDescription.setVisibility(View.GONE);
            }
        } else {
            mInstallmentsDescription.setVisibility(View.GONE);
            mInterestAmountDescription.setVisibility(View.GONE);
        }
    }

    private void setLastFourDigitsCard() {
        if (isLastFourDigitsValid() && isPaymentMethodValid()) {
            setPaymentMethodImage();
            String message = getString(R.string.mpsdk_last_digits_label) + " " + mPayment.getCard().getLastFourDigits();
            mLastFourDigitsDescription.setText(message);
        } else {
            mLastFourDigitsDescription.setVisibility(View.GONE);
            mPaymentMethodImage.setVisibility(View.GONE);
        }
    }

    private void setPaymentMethodImage() {
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId());
        if (resourceId != 0) {
            mPaymentMethodImage.setImageResource(resourceId);
        } else {
            mPaymentMethodImage.setVisibility(View.GONE);
        }
    }

    private void setPaymentEmailDescription() {
        if (isPayerEmailValid()) {
            String subtitle = String.format(getString(R.string.mpsdk_subtitle_action_activity_congrat), mPayment.getPayer().getEmail());
            mPayerEmail.setText(subtitle);
        } else {
            mPayerEmail.setVisibility(View.GONE);
            mTopEmailSeparator.setVisibility(View.GONE);
        }
    }

    private Boolean isPaymentIdValid() {
        return mPayment.getId() != null && mPayment.getId() >= 0;
    }

    private Boolean isTotalPaidAmountValid() {
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO)) > 0;
    }

    private Boolean isInstallmentAmountValid() {
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getInstallmentAmount() != null &&
                mPayment.getTransactionDetails().getInstallmentAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isInstallmentQuantityValid() {
        return mPayment.getInstallments() != null && mPayment.getInstallments() >= 0;
    }

    private Boolean isPayerEmailValid() {
        return mPayment.getPayer() != null && !isEmpty(mPayment.getPayer().getEmail());
    }

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName());
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethod.getId()) && mPaymentMethod.getId().equals(mPayment.getPaymentMethodId());
    }

    private Boolean isLastFourDigitsValid() {
        return mPayment.getCard() != null && !isEmpty(mPayment.getCard().getLastFourDigits());
    }

    private Spanned getInstallmentsText() {
        StringBuffer sb = new StringBuffer();
        sb.append(mPayment.getInstallments());
        sb.append(" ");
        sb.append(getString(R.string.mpsdk_installments_by));
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), sb.toString(), false, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CONGRATS", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            finishWithOkResult();
        } else {
            Snackbar.make(mKeepBuyingButton, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
    }

    private void resetBackPressedOnceIn(final int mills) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                    mBackPressedOnce = false;
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }).start();
    }
}
