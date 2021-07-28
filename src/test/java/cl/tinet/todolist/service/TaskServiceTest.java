package cl.tinet.todolist.service;

import cl.tinet.todolist.dao.TaskRepository;
import cl.tinet.todolist.exceptions.TaskException;
import cl.tinet.todolist.model.Task;
import cl.tinet.todolist.model.TaskRequestTO;
import cl.tinet.todolist.model.TaskResponseTO;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for Task service.
 */
@ExtendWith(SpringExtension.class)
class TaskServiceTest {
    /**
     * Dummy value for id Task attribute.
     */
    private final static Long ID = 1L;

    /**
     * Dummy value for title Task attribute.
     */
    private final static String TITLE = "TITLE";

    /**
     * Dummy value for description Task attribute.
     */
    private final static String DESCRIPTION = "DESCRIPTION";

    /**
     * Dummy value for state Task attribute.
     */
    private final static int STATE = 0;

    /**
     * Dummy value for active Task attribute.
     */
    private final static boolean ACTIVE = true;

    /**
     * Dummy value for createAt Task attribute.
     */
    private final static String CREATE_AT = "01-01-2021 00:00:00";

    /**
     * Dummy value for updatedAt Task attribute.
     */
    private final static String UPDATED_AT = "01-01-2021 00:00:00";

    /**
     * Dummy Task.
     */
    private Task task;

    /**
     * Dummy Task to save.
     */
    private Task taskToSave;

    /**
     * Dummy TaskResponseTO.
     */
    private TaskResponseTO taskResponseTO;

    /**
     * Dummy TaskRequestTO.
     */
    private TaskRequestTO taskRequestTO;

    /**
     * Dummy List to use in test.
     */
    private List<Task> tasks = new ArrayList<>();

    /**
     * Dummy service.
     */
    private TaskService taskService;

    /**
     * Mock service to replace the real repository behavior.
     */
    @MockBean
    private TaskRepository taskRepository;

    /**
     * Setup the necessary state to perform this test.
     */
    @BeforeEach
    void setup() {
        taskService = new TaskService(taskRepository);
        task = Task.builder()
                .id(ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .state(STATE)
                .active(ACTIVE)
                .createAt(CREATE_AT)
                .updatedAt(UPDATED_AT).build();

        taskToSave = Task.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .state(STATE)
                .active(ACTIVE)
                .createAt(CREATE_AT)
                .updatedAt(UPDATED_AT).build();

        taskResponseTO = TaskResponseTO.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .state(task.getState())
                .createAt(task.getCreateAt())
                .updatedAt(task.getUpdatedAt()).build();

        taskRequestTO = new TaskRequestTO();

        taskRequestTO.setTitle(task.getTitle());
        taskRequestTO.setDescription(task.getDescription());

        tasks.add(task);
    }

    /**
     * Test getTask service when is OK
     */
    @Test
    void getTasksOk() {
        when(taskRepository.findAll()).thenReturn(tasks);
        List<Task> tasksTest = taskService.getTasks();
        assertEquals(tasks.size(), tasksTest.size());
    }

    /**
     * Test getTask service when is not OK
     */
    @Test
    void getTasksNOk() {
        when(taskRepository.findAll()).thenThrow(TaskException.class);
        boolean isNotOk = false;
        try{
            taskService.getTasks();
        }catch(TaskException e){
            isNotOk = true;
        }
        assertTrue(isNotOk);
    }

    /**
     * verify if the getTaskById works with a valid id.
     */
    @Test
    void getTaskByIdOk() {
        String id = "1";
        Long taskId = Long.valueOf(id);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Task responseTask = taskService.getTaskById(id);

        assertEquals(task.getId(), responseTask.getId());
    }

    /**
     * verify if the getTaskById works with a valid id,
     * but no result.
     */
    @Test
    void getTaskByIdNOk() {
        String id = "200";
        Long taskId = Long.valueOf(id);
        Task emptyTask = Task.builder().build();

        boolean thereIsNotTaskWithThisId = false;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(emptyTask));

        try {
            taskService.getTaskById(id);
        }catch (TaskException e) {
            thereIsNotTaskWithThisId = true;
        }

        assertTrue(thereIsNotTaskWithThisId);
    }

    /**
     * verify if the getTaskById service, handle an invalid
     * parameter.
     *
     * The repository is not call, the service throws an exception
     * before to execute repository.
     *
     */
    @Test
    void getTaskByIdNotOKParsing() {
        String id = "q";
        boolean isInvalidParameter = false;

        try {
            taskService.getTaskById(id);
        }catch (Exception e) {
            isInvalidParameter = true;
        }
        assertTrue(isInvalidParameter);
    }

    /**
     * Verify insertion of a task.
     */
    @Test
    void setTaskOk() {
        when(taskRepository.save(any())).thenReturn(task);
        TaskResponseTO response = taskService.setTask(taskRequestTO);
        assertEquals(TITLE, response.getTitle());
    }

    /**
     * Verify insertion of a task failed.
     */
    @Test
    void setTaskNoOk() {
        task.setId(null);
        when(taskRepository.save(any())).thenReturn(task);
        boolean taskNotSet = false;

        try {
            taskService.setTask(taskRequestTO);
        }catch (TaskException e) {
            taskNotSet = true;
        }
        assertTrue(taskNotSet);
    }

}