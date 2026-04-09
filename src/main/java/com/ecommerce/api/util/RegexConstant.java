package com.ecommerce.api.util;

/**
 * Stores regex patterns for validation.
 * Not meant to be instantiated.
 */
public class RegexConstant {
    private RegexConstant(){}

    public static final String USERNAME = "^[A-Za-z][A-Za-z0-9_ ]{2,49}$";

    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&]).{6,20}$";

    public static final String PHONE = "^(\\+91)?[6-9][0-9]{9}$";

    public static final String PRODUCT_NAME = "^(?=.*[A-Za-z0-9])[A-Za-z0-9 ._-]{2,100}$";


}
