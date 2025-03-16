package com.example.ipro_hr.entity;

import com.example.ipro_hr.entity.template.AbsUUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@Entity(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted=true WHERE id=?")
public class User extends AbsUUID {
    @Column(nullable = false, name = "chat_id")
    private String chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String profession;   //kasbi

    private String level;      //Darajasi

//    @Enumerated(EnumType.STRING)
//    private Location location;
    private String location;  //manzili

    @Column(name = "portfolio_link")
    private String portfolioLink;   // Portfolio silkasi

    private String salary; //ish haqi

    private String employmentType;      //ish turi(full time, online, part time)

//    private String resume;
}
