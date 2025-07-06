package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "student")
    private Set<Subscription> studentSubs = new HashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<LinkedPurchaseList> studentPurchases = new HashSet<>();

}
