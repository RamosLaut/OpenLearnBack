package com.openlearn.OpenLearn.Repositories;

import com.openlearn.OpenLearn.Model.Entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson,Long> {
}
