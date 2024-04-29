package com.bicmamwservice.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UnauthorizedException extends RuntimeException {

    String message;
    public UnauthorizedException(String message) {
        super(message);
        this.message=message;
    }
}
