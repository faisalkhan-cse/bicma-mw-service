package com.bicmamwservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public  class CustomErrorResponse {
    private String error;
    private String message;


}