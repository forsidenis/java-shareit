package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.clear();
    }

    @Test
    public void createUser_validUser_returnsCreated() throws Exception {
        UserDto userDto = new UserDto(null, "Test User", "test@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void createUser_emptyName_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "", "test@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_invalidEmail_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "Test User", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_duplicateEmail_returnsConflict() throws Exception {
        // Создаем первого пользователя
        UserDto user1 = new UserDto(null, "User1", "duplicate@example.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        // Пытаемся создать второго пользователя с тем же email
        UserDto user2 = new UserDto(null, "User2", "duplicate@example.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isConflict()); // Изменено с 400 на 409
    }

    @Test
    public void updateUser_validUpdate_returnsUpdatedUser() throws Exception {
        // Создаем пользователя для обновления с уникальным email
        UserDto createDto = new UserDto(null, "Original Name", "update-test-1@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Обновляем пользователя - только имя
        UserDtoForUpdate updateDto = new UserDtoForUpdate("Updated Name", null);

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("update-test-1@example.com"));
    }

    @Test
    public void updateUser_updateEmail_returnsUpdatedUser() throws Exception {
        // Создаем пользователя для обновления с уникальным email
        UserDto createDto = new UserDto(null, "Original Name", "update-test-2@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Обновляем пользователя - только email
        UserDtoForUpdate updateDto = new UserDtoForUpdate(null, "updated-email@example.com");

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Original Name"))
                .andExpect(jsonPath("$.email").value("updated-email@example.com"));
    }

    @Test
    public void updateUser_withInvalidEmail_returnsBadRequest() throws Exception {
        // Создаем пользователя для обновления с уникальным email
        UserDto createDto = new UserDto(null, "Original Name", "update-test-3@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Пытаемся обновить с невалидным email
        UserDtoForUpdate updateDto = new UserDtoForUpdate("Updated Name", "invalid-email");

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_withEmptyEmail_returnsBadRequest() throws Exception {
        // Создаем пользователя для обновления с уникальным email
        UserDto createDto = new UserDto(null, "Original Name", "update-test-4@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Пытаемся обновить с пустым email
        UserDtoForUpdate updateDto = new UserDtoForUpdate("Updated Name", "");

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_duplicateEmail_returnsConflict() throws Exception {
        // Создаем первого пользователя
        UserDto user1 = new UserDto(null, "User1", "duplicate-email-1@example.com");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        // Создаем второго пользователя
        UserDto user2 = new UserDto(null, "User2", "duplicate-email-2@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Пытаемся обновить второго пользователя с email первого
        UserDtoForUpdate updateDto = new UserDtoForUpdate(null, "duplicate-email-1@example.com");

        mockMvc.perform(patch("/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict()); // Изменено с 400 на 409
    }

    @Test
    public void updateUser_userNotFound_returnsNotFound() throws Exception {
        UserDtoForUpdate updateDto = new UserDtoForUpdate("Updated Name", null);

        mockMvc.perform(patch("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserById_exists_returnsUser() throws Exception {
        // Создаем пользователя
        UserDto createDto = new UserDto(null, "Test User", "get-test@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Получаем пользователя по ID
        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("get-test@example.com"));
    }

    @Test
    public void getUserById_notExists_returnsNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers_returnsAllUsers() throws Exception {
        // Создаем несколько пользователей
        UserDto user1 = new UserDto(null, "User1", "all-users-1@example.com");
        UserDto user2 = new UserDto(null, "User2", "all-users-2@example.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        // Получаем всех пользователей
        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        List<UserDto> users = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        assertEquals(2, users.size());
    }

    @Test
    public void deleteUser_exists_deletesUser() throws Exception {
        // Создаем пользователя
        UserDto createDto = new UserDto(null, "To Delete", "delete-test@example.com");
        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Удаляем пользователя
        mockMvc.perform(delete("/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());

        // Проверяем, что пользователь удален
        mockMvc.perform(get("/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_notExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", 999L))
                .andExpect(status().isNoContent());
    }
}
