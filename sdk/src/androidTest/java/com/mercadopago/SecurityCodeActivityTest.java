package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mromar on 11/21/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SecurityCodeActivityTest {

    @Rule
    public ActivityTestRule<SecurityCodeActivity> mTestRule = new ActivityTestRule<>(SecurityCodeActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;

    private FakeAPI mFakeAPI;

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_TEST_PUBLIC_KEY;

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    //Recoverable Token
    @Test
    public void ifPaymentRecoveryReceivedWithPaymentStatusDetailCallForAuthorizeShowOnlySecurityCode() {
        Token token = StaticMock.getToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();

        validStartIntent.putExtra("cardInfo", JsonUtil.getInstance().toJson(token));
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        validStartIntent.putExtra("token", JsonUtil.getInstance().toJson(token));

        Token mockedClonedToken = StaticMock.getClonedToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedClonedToken), 200, "");

        Token mockedPutSecurityCodeToken = StaticMock.getClonedToken();
        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedPutSecurityCodeToken), 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkCardSecurityCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
        onView(withId(R.id.mpsdkSecurityCodeNextButton)).perform(click());

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        Token tokenResult = JsonUtil.getInstance().fromJson(result.getExtras().getString("token"), Token.class);

        assertFalse(tokenResult.getId().equals(token.getId()));
    }

//    @Test
//    public void ifSecurityCodeInputIsValidCloneTokenAndFinishActivity() {
//        Token token = StaticMock.getToken();
//        Payment payment = StaticMock.getPaymentRejectedCallForAuthorize();
//        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
//        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
//        Issuer issuer  = StaticMock.getIssuer();
//
//        PaymentRecovery paymentRecovery = new PaymentRecovery(token, payment, paymentMethod, payerCost, issuer);
//        validStartIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));
//
//        Token mockedToken = StaticMock.getToken();
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//        mFakeAPI.addResponseToQueue(JsonUtil.getInstance().toJson(mockedToken), 200, "");
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardSecurityCodeContainer)).check(matches(isDisplayed()));
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
//        String tokenJson = JsonUtil.getInstance().toJson(mockedToken);
//        assertTrue(tokenJson.equals(result.getExtras().getString("token")));
//    }
//
//    @Test
//    public void ifSecurityCodeInputForPaymentRecoveryIsNotValidShowError() {
//        Token token = StaticMock.getToken();
//        Payment payment = StaticMock.getPaymentRejectedCallForAuthorize();
//        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
//        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
//        Issuer issuer  = StaticMock.getIssuer();
//
//        PaymentRecovery paymentRecovery = new PaymentRecovery(token, payment, paymentMethod, payerCost, issuer);
//        validStartIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));
//
//        mTestRule.launchActivity(validStartIntent);
//
//        onView(withId(R.id.mpsdkCardSecurityCodeContainer)).check(matches(isDisplayed()));
//        onView(withId(R.id.mpsdkCardSecurityCode)).perform(typeText(StaticMock.DUMMY_SECURITY_CODE.substring(0, 1)));
//        onView(withId(R.id.mpsdkNextButton)).perform(click());
//
//        checkSecurityCodeIsInvalid("11", onView(withId(R.id.mpsdkNextButton)));
//    }
//
//    @Test
//    public void ifAskSecurityCodeAndPressesBackButtonFinishActivityWithCancelResult() {
//        Token token = StaticMock.getToken();
//        Payment payment = StaticMock.getPaymentRejectedCallForAuthorize();
//        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
//        PayerCost payerCost = StaticMock.getPayerCostWithInterests();
//        Issuer issuer  = StaticMock.getIssuer();
//
//        PaymentRecovery paymentRecovery = new PaymentRecovery(token, payment, paymentMethod, payerCost, issuer);
//        validStartIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));
//
//        mTestRule.launchActivity(validStartIntent);
//        onView(withId(R.id.mpsdkBackButton)).perform(click());
//
//        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
//
//    }
}
