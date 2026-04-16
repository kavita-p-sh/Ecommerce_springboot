package com.ecommerce.api.util;

/**
 * Stores constant values used in the application.
 */
public class AppConstants {

    private AppConstants(){}

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String SYSTEM = "SYSTEM";


    public static final String REGISTER_SUCCESS = "User registered successfully";
    public static final String LOGOUT_SUCCESS = "Logout successfully";

    public static final String ACCESS_DENIED = "Access denied: insufficient permissions";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username: ";
    public static final String USERNAME_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email Already Exists";
    public static final String PHONE_ALREADY_EXISTS = "Phone Already Exists";

    public static final String USER_UNAUTHENTICATED = "User not authenticated";
    public static final String USERNAME_REQUIRED = "Username is Required";
    public static final String EMAIL_REQUIRED = "Email is Required";
    public static final String PASSWORD_INVALID = "Password is Invalid";
    public static final String USER_DELETED_SUCCESS = "User %s deleted successfully";


    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String PRODUCT_ALREADY_EXISTS = "Product Already Exists";
    public static final String PRODUCT_NAME_EMPTY = "Product name cannot be empty";
    public static final String PRODUCT_PRICE_INVALID = "Price must be greater than 0";
    public static final String PRODUCT_QUANTITY_INVALID = "Quantity cannot be negative";
    public static final String PRODUCT_DESCRIPTION_TOO_LONG = "Description too long";
    public static final String PRODUCT_DELETED_SUCCESS = "Product deleted successfully";


    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_ITEMS_CAN_NOT_EMPTY = "Order items cannot be empty";
    public static final String UNAUTHORIZED_ORDER_ACCESS = "Unauthorized access to order";
    public static final String CANNOT_CANCEL = "Cannot cancel delivered order";
    public static final String CANCELLED = "Cancelled Successfully";


    public static final String STATUS_PLACED = "PLACED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_NOT_FOUND = "Status not found";
    public static final String NOT_ALLOWED_TO_CANCEL_ORDER = "You are not allowed to cancel this order";


    public static final String OUT_OF_STOCK = "Product out of stock: ";
    public static final String NOT_ENOUGH_STOCK = "Not enough stock for product: ";


    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ONLY_ADMIN_ALLOWED = "Only admin can assign admin or manager role";


    public static final String RESOURCE_NOT_FOUND = "Requested resource not found";
    public static final String BAD_REQUEST = "Invalid request";
    public static final String GENERIC_ERROR = "Something went wrong, please try again later";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String INVALID_ROLE = "Invalid Role";
    public static final String ROLE_NOT_ASSIGNED = "No roles assigned to the user";




    public static final String ANONYMOUS_USER = "anonymousUser";
}