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

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    //Kasb turi
    private String profession;

    //Darajasi
//    @Enumerated(EnumType.STRING)
//    private Level level;
    private String level;

    //qaysi viloyatdan
//    @Column(name = "location")
//    @Enumerated(EnumType.STRING)
//    private Location location;
    private String location;

    // Portfolio silkasi
    @Column(name = "portfolio_link")
    private String portfolioLink;

    //Oylik maosh
//    @Enumerated(EnumType.STRING)
//    private SalaryRange salary;
    private String salary;

    //BANDLIK TURI
//    @Enumerated(EnumType.STRING)
//    private EmploymentType employmentType ;
    private String employmentType;

    private String resume;






//BOT dan imegeni olib DATABASE ga hozircha saqlolmadim
    /*@OneToOne(fetch = FetchType.LAZY)
    private Image image;*/


}
