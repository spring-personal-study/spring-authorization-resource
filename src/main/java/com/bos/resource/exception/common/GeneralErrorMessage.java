package com.bos.resource.exception.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralErrorMessage {
    public static final String ALL_PARAMETER_IS_NULL = "All required fields are not passed or are empty.";
    public static final String INVALID_PARAMETER = "not enough parameters have passed or an invalid parameter value has passed.";
    public static final String NOT_ALLOWED_CORS = "this url cannot call server resources.";
    public static final String MAXIMUM_FILE_SIZE = "Maximum upload size exceeded";

    public static final String UPLOAD_FAIL = "File Upload Fail.";
    public static final String DATE_TIME_PARSE_ERROR = "Invalid Date Time Format Detected.";
}
