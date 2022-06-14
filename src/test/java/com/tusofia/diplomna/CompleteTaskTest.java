//package com.tusofia.diplomna;
//
//import com.tusofia.diplomna.model.Task;
//import com.tusofia.diplomna.service.task.TaskService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@RunWith(SpringJUnit4ClassRunner.class)
//public class CompleteTaskTest {
//    private final String COMPLETE_TASK = "/task-complete?id=";
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    TaskService taskService;
//    @Test
//    public void CompleteTask(Long id) throws Exception {
//        Task task = taskService.getById(id);
//        task.setCompleted(true);
//        this.mockMvc
//                .perform(
//                    .put(COMPLETE_TASK + task.getId())
//                        .with(csrf())
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                             .andExpect(redirectedUrl("/task-list"))
//                                 .andExpect(status().is3xxRedirection());
//                )
//    }
//}
