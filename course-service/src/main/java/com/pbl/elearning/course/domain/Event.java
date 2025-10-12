package com.pbl.elearning.course.domain;

import com.pbl.elearning.course.domain.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue
    private UUID id;

    // Liên kết nhiều-một tới bài giảng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // Loại sự kiện là QUIZ, NOTE, hay ALERT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    // Thời điểm kích hoạt sự kiện (tính bằng giây)
    @Column(nullable = false)
    private Integer triggerTime;

    // Dữ liệu đi kèm, trong trường hợp này là ID của Quiz
    @Column(nullable = false)
    private String payload;
}
