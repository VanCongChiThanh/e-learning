package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
}
