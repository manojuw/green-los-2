package com.mixo.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class EBillDto {
    private int chId;
    private String mobileNo;
    private String email;
    private String channel;
    private String imei;
    private String os;
    private String appName;
    private String ip;
    private String billerId;
    private List<Map<String, String>> customerParams; // Handles key-value pairs
}
