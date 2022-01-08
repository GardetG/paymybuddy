package com.openclassrooms.paymybuddy.constant;

import java.math.BigDecimal;

/**
 * Utility Class for defining application constants value.
 */
public class ApplicationValue {

  private ApplicationValue() {
    throw new IllegalStateException("Utility class");
  }

  public static final BigDecimal INITIAL_USER_WALLET = BigDecimal.ZERO;
  public static final BigDecimal INITIAL_BANKACCOUNT_BALANCE = BigDecimal.valueOf(500);

}