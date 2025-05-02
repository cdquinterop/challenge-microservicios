package com.example.personservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    private String action;
    private Long customerId;
    private String name;
    private Boolean status;
}
