package org.example;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "linked_purchase_list")
public class LinkedPurchaseList {
    @EmbeddedId
    private LinkedPLKey id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;
}
