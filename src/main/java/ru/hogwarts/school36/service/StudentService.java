package ru.hogwarts.school36.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school36.exception.StudentNotFoundException;
import ru.hogwarts.school36.model.Faculty;
import ru.hogwarts.school36.model.Student;
import ru.hogwarts.school36.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {


    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        student.setId(null);
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

   public void deleteStudent(long id) {
       studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        return studentRepository.findAllByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        if (min < 0 || max < 0 || max < min) {
            throw new IllegalArgumentException();
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getNumberFacultyOfStudent(long student_id) {
        return studentRepository.findById(student_id).get().getFaculty();
    }
}



