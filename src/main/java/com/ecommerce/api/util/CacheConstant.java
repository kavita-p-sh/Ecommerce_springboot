package com.ecommerce.api.util;

/**
 * Cache related Constants
 * Contains cache names used in the application.
 */
public class CacheConstant {

    private CacheConstant() {
    }

    public static final String USERS = "users";
    public static final String ALL_USERS_KEY = "'allUsers'";
    public static final String ORDERS_BY_USER_KEY = "'user_' + #username";
    public static final String USERNAME_USER_KEY= "'username_' + #username";
    public static final String EMAIL_USER_KEY= "'email_' + #email";
    public static final String PHONE_USER_KEY="'phone_' + #phoneNumber";
    public static final String RESULT_USER_KEY ="'username_' + #result.username";

    public static final String PRODUCTS = "products";
    public static final String ALL_PRODUCTS_KEY= "'allProducts'";


    public static final String ORDERS = "orders";
    public static final String ORDERS_BY_USER = "ordersByUser";
    public static final String ALL_ORDERS_KEY = "'allOrders'";



}


