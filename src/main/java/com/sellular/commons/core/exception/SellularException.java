package com.sellular.commons.core.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellularException extends RuntimeException {

    private String code;

    private String header;

    private Integer statusCode;

    private String message;

    @Builder
    public SellularException(final String message, final String code, final String header, final Integer statusCode) {
        super(message);
        this.message = message;
        this.code = code;
        this.header = header;
        this.statusCode = statusCode;
    }

    public <T extends ErrorCodeInterface> SellularException(final T errorCode, final Integer statusCode) {
        super(errorCode.getErrorMessage());
        this.code = errorCode.getErrorCode();
        this.message = errorCode.getErrorMessage();
        this.header = errorCode.getErrorHeader();
        this.statusCode = statusCode;
    }
}
