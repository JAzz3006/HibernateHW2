package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name="courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    private int duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "students_count", nullable = true)
    private Integer studentsCount;

    @Column(name = "price")
    private int price;

    @Column(name = "price_per_hour")
    private float pricePerHour;

    @OneToMany(mappedBy = "course")
    private Set<Subscription> courseSubs = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<LinkedPurchaseList> coursePurchases = new HashSet<>();
}
