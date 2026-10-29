package org.example;

import lombok.Data;

@Data
public class SmsMessage {
    private String phoneNumber;
    private String message;
}
