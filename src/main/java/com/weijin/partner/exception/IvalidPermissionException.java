package com.weijin.partner.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author yindwe
 * Created on 2019/1/11
 */
public class IvalidPermissionException extends AuthenticationException {

    public IvalidPermissionException() {
        super();
    }

    public IvalidPermissionException(String message) {
        super(message);
    }

    public IvalidPermissionException(Throwable cause) {
        super(cause);
    }

    public IvalidPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
