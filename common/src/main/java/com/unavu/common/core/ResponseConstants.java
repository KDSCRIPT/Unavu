package com.unavu.common.core;

public class ResponseConstants {

    public static final int STATUS_CREATED = 201;
    public static final String MESSAGE_CREATED = "%s created successfully";

    public static final int STATUS_OK = 200;
    public static final String MESSAGE_OK = "%s processed successfully";

    public static final int STATUS_NO_CONTENT = 204;
    public static final String MESSAGE_NO_CONTENT = "%s not found";

    public static final int STATUS_UPDATE_FAILED = 417;
    public static final String MESSAGE_UPDATE_FAILED = "%s update failed. Please try again or contact Dev team";

    public static final int STATUS_DELETE_FAILED = 417;
    public static final String MESSAGE_DELETE_FAILED = "%s delete failed. Please try again or contact Dev team";

    public static final int STATUS_ERROR = 500;
    public static final String MESSAGE_ERROR = "An error occurred. Please try again or contact Dev team";
}
