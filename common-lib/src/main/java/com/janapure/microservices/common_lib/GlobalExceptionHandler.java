package com.janapure.microservices.common_lib;


import com.janapure.microservices.common_lib.constant.ErrorCode;
import com.janapure.microservices.common_lib.exception.PlatformException;
import com.janapure.microservices.common_lib.exception.RestError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {


     @ExceptionHandler(PlatformException.class)
     public ResponseEntity<?> handlePlatformException(PlatformException ex) {
         HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
         RestError restError = processPlatformException(ex);
         if (restError != null){
             status = HttpStatus.valueOf(Integer.parseInt(restError.getStatus()));
         }
         System.out.println("Exception occurred: " + status);
         return new ResponseEntity<>(restError, status);
     }

     @ExceptionHandler(AuthorizationDeniedException.class)
     public  ResponseEntity<?> AccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
         System.out.println("AccessDeniedException occurred: " + ex.getMessage());
         HttpStatus status = HttpStatus.FORBIDDEN;
         RestError restError = new RestError();
         restError.setStatus(String.valueOf(status.value()));
         restError.setCode("ECP-403");
         restError.setError("ACCESS_DENIED");
         restError.setMessage("Access is denied for the requested resource.");
         return new ResponseEntity<>(restError, status);
     }

    private RestError processPlatformException(PlatformException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        Object[] args = ex.getArgs();
        System.out.println("Processing PlatformException: " + errorCode + ", args: " + Arrays.toString(args));
        return processSimpleError(errorCode, args,ex);
    }

    private RestError processSimpleError(ErrorCode errorCode, Object[] args, PlatformException ex) {
        RestError restError = new RestError();
        String code = errorCode.getCode();
        String message = errorCode.getMessage();
        if (args != null && args.length > 0) {
            message += Arrays.toString(args);
        }
        restError.setStatus(getHttpStatus(code));
        restError.setCode(code);
        restError.setError(errorCode.name());
        restError.setMessage(message);
        return restError;
    }

    private String getHttpStatus(String code) {
        // Extract HTTP code from your custom code like ECP-401
        return code.substring(code.indexOf('-') + 1);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // 👈 at least print stack trace
        return ResponseEntity.status(500).body("Unhandled exception: " + ex.getMessage());
    }

}
