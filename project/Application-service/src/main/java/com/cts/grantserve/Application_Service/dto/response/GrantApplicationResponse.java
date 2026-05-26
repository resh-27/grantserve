package com.cts.grantserve.Application_Service.dto.response;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class GrantApplicationResponse {
    private int status;
    private Long applicationID;
    private String message;
}
