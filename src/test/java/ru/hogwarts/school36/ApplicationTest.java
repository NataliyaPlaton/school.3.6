package ru.hogwarts.school36;

import net.minidev.json.JSONObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school36.controller.FacultyController;
import ru.hogwarts.school36.controller.StudentController;
import ru.hogwarts.school36.model.Faculty;
import ru.hogwarts.school36.model.Student;
import ru.hogwarts.school36.repository.FacultyRepository;
import ru.hogwarts.school36.repository.StudentRepository;
import ru.hogwarts.school36.service.FacultyService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {
    @LocalServerPort
    private int port;


    @WebMvcTest(controllers = FacultyController.class)
    public static class FacultyControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private FacultyRepository facultyRepository;

        @MockBean
        private StudentRepository studentRepository;

        @SpyBean
        private FacultyService facultyService;

        @InjectMocks
        private FacultyController facultyController;

        @Test
        public void testFaculties() throws Exception {
            final String name = "one";
            final String color = "green";
            final long id = 1;

            Faculty faculty = new Faculty(id, name, color);

            JSONObject facultyObject = new JSONObject();
            facultyObject.put("id", id);
            facultyObject.put("name", name);
            facultyObject.put("color", color);


            when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
            when(facultyRepository.findById(eq(id))).thenReturn(Optional.of(faculty));
            when(facultyRepository.findAllByColor(eq(color))).thenReturn(Collections.singleton(faculty));
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/faculty")
                            .content(facultyObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.color").value(color));

            mockMvc.perform(MockMvcRequestBuilders
                            .put("/faculty")
                            .content(facultyObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.color").value(color));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/faculty/" + id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(id))
                    .andExpect(jsonPath("$[0].name").value(name))
                    .andExpect(jsonPath("$[0].color").value(color));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/faculty?color=" + color)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.color").value(color));

            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/faculty/" + id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

    }
}