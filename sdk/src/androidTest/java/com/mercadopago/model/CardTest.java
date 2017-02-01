package com.mercadopago.model;

import com.mercadopago.DeprecatedCheckoutActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class CardTest extends BaseTest<DeprecatedCheckoutActivity> {

    public CardTest() {
        super(DeprecatedCheckoutActivity.class);
    }

    public void testIsSecurityCodeRequired() {

        Card card = StaticMock.getCard();

        assertTrue(card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredNull() {

        Card card = StaticMock.getCard();
        card.setSecurityCode(null);
        assertTrue(!card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredLengthZero() {

        Card card = StaticMock.getCard();
        card.getSecurityCode().setLength(0);
        assertTrue(!card.isSecurityCodeRequired());
    }
}
