package com.ecommerce.api.exception;

public class IpBlockedException extends RuntimeException{

    public IpBlockedException(String message)
    {
        super(message);
    }

}
