package com.openlearn.OpenLearn.Controllers;

import com.openlearn.OpenLearn.Model.Entities.Course;
import com.openlearn.OpenLearn.Services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    @Autowired
    private CourseService courseService;

    public record CourseRequest(
            String title,
            String description,
            String category,
            Long instructorId,
            String imageUrl
    ){}

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request) {
        try {
            Course newCourse = new Course();
            newCourse.setTitle(request.title());
            newCourse.setDescription(request.description());
            newCourse.setCategory(request.category());
            newCourse.setImageUrl(request.imageUrl());

            Course savedCourse = courseService.createCourse(newCourse, request.instructorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating course: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Critical error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);

        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        } else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course with id: " + id + " not found");
        }
    }

    @GetMapping("instructor/{instructorId}")
    public ResponseEntity<List<Course>> getInstructorCourses(@PathVariable Long instructorId) {
        List<Course> courses = courseService.getAllCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourseById(id);
            return ResponseEntity.ok("Course with id: " + id + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
