package com.openlearn.OpenLearn.Services;

import com.openlearn.OpenLearn.Model.Entities.Course;
import com.openlearn.OpenLearn.Model.Entities.User;
import com.openlearn.OpenLearn.Repositories.CourseRepository;
import com.openlearn.OpenLearn.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Course createCourse(Course course, Long instructorId){

        User instructor = userRepository.findById(instructorId).orElseThrow(()->new RuntimeException("instructor not found wiht ID:" + instructorId));

        course.setInstructor(instructor);

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public List<Course> getAllCoursesByInstructor(Long instructorId){
        return courseRepository.findByInstructorId(instructorId);
    }

    public Optional<Course> getCourseById(Long id){
        return courseRepository.findById(id);
    }

    public void deleteCourseById(Long id){
        if(courseRepository.existsById(id)){
            courseRepository.deleteById(id);
        }else {
            throw new RuntimeException("Cannot be deleted: The course does not exist");
        }

    }
}
