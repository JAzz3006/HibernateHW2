package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @EmbeddedId
    private SubscriptionKey id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student ;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "subscription_date")
    private LocalDateTime subscriptionDate;
}
