package com.example.ipro_hr.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResDTO {
    private UUID id;

    private String fullName;

    private String phoneNumber;

    private String profession;

    private String location;

    private String level;

    private String portfolioLink;

    private String salary;

    private String employmentType ;

    private String resume;

}
