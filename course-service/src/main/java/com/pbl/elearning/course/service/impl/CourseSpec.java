package com.pbl.elearning.course.service.impl;

import java.util.UUID;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.enrollment.models.Enrollment;

public class CourseSpec {

    public static Specification<Course> userNotPurchased(UUID userId) {
        return (root, query, cb) -> {
            query.distinct(true);

            Subquery<Enrollment> sub = query.subquery(Enrollment.class);
            Root<Enrollment> e = sub.from(Enrollment.class);

            sub.select(e)
                    .where(
                            cb.and(
                                    cb.equal(e.get("course"), root),
                                    cb.equal(e.get("user").get("userId"), userId) 
            ));

            return cb.not(cb.exists(sub));
        };
    }

}
