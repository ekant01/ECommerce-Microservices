package com.janapure.microservices.common_lib.exception;

import com.janapure.microservices.common_lib.constant.ErrorCode;
import lombok.Getter;

import javax.swing.*;

@Getter
public class PlatformException extends RuntimeException {

    private final ErrorCode errorCode;

    private final Object[] args;

    public PlatformException(ErrorCode errorCode, Object... args) {
        super(formatmessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String formatmessage(ErrorCode errorCode, Object... args) {
        if (args != null && args.length > 0) {
            return String.format(errorCode.getMessage(), args);
        }
        return errorCode.getMessage();
    }
}
