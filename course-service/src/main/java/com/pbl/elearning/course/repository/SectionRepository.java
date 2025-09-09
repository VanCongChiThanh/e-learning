package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface SectionRepository extends JpaRepository<Section, UUID > {

}
