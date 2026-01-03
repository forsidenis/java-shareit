package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.UserDto;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ru.practicum.shareit.user.UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        userRepository.clear();
        itemRepository.clear();
    }

    private Long createTestUser(String email) throws Exception {
        UserDto userDto = new UserDto(null, "Test Owner", email);
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(response, UserDto.class);
        return createdUser.getId();
    }

    @Test
    public void createItem_validItem_returnsCreated() throws Exception {
        Long userId = createTestUser("owner1@example.com");
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void createItem_missingHeader_returnsBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createItem_missingName_returnsBadRequest() throws Exception {
        Long userId = createTestUser("owner2@example.com");
        ItemDto itemDto = new ItemDto(null, null, "Description", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createItem_emptyName_returnsBadRequest() throws Exception {
        Long userId = createTestUser("owner3@example.com");
        ItemDto itemDto = new ItemDto(null, "", "Description", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createItem_missingDescription_returnsBadRequest() throws Exception {
        Long userId = createTestUser("owner4@example.com");
        ItemDto itemDto = new ItemDto(null, "Name", null, true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createItem_emptyDescription_returnsBadRequest() throws Exception {
        Long userId = createTestUser("owner5@example.com");
        ItemDto itemDto = new ItemDto(null, "Name", "", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createItem_missingAvailable_returnsBadRequest() throws Exception {
        Long userId = createTestUser("owner6@example.com");
        ItemDto itemDto = new ItemDto(null, "Name", "Description", null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItem_validUpdate_returnsUpdatedItem() throws Exception {
        Long userId = createTestUser("owner7@example.com");

        // Создаем вещь
        ItemDto createDto = new ItemDto(null, "Old Name", "Old description", true, null);
        MvcResult createResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        // Обновляем вещь
        ItemDtoForUpdate updateDto = new ItemDtoForUpdate("New Name", "New description", false);

        mockMvc.perform(patch("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void updateItem_partialUpdate_returnsUpdatedItem() throws Exception {
        Long userId = createTestUser("owner8@example.com");

        ItemDto createDto = new ItemDto(null, "Item", "Description", true, null);
        MvcResult createResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        // Обновляем только статус доступности
        ItemDtoForUpdate updateDto = new ItemDtoForUpdate(null, null, false);

        mockMvc.perform(patch("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void updateItem_notOwner_returnsForbidden() throws Exception {
        Long userId1 = createTestUser("owner9@example.com");
        Long userId2 = createTestUser("owner10@example.com");

        // Создаем вещь от первого пользователя
        ItemDto createDto = new ItemDto(null, "Item", "Description", true, null);
        MvcResult createResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        // Пытаемся обновить от второго пользователя
        ItemDtoForUpdate updateDto = new ItemDtoForUpdate("Hacked Name", null, null);

        mockMvc.perform(patch("/items/{id}", createdItem.getId())
                        .header("X-Sharer-User-Id", userId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateItem_itemNotFound_returnsNotFound() throws Exception {
        Long userId = createTestUser("owner11@example.com");
        ItemDtoForUpdate updateDto = new ItemDtoForUpdate("New Name", null, null);

        mockMvc.perform(patch("/items/{id}", 999L)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemById_exists_returnsItem() throws Exception {
        Long userId = createTestUser("owner12@example.com");

        ItemDto createDto = new ItemDto(null, "Test Item", "Test description", true, null);
        MvcResult createResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        ItemDto createdItem = objectMapper.readValue(response, ItemDto.class);

        // Получаем вещь по ID
        mockMvc.perform(get("/items/{id}", createdItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItem.getId()))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    @Test
    public void getItemById_notExists_returnsNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByOwner_returnsOwnerItems() throws Exception {
        Long userId = createTestUser("owner13@example.com");

        // Создаем несколько вещей для владельца
        ItemDto item1 = new ItemDto(null, "Item 1", "Description 1", true, null);
        ItemDto item2 = new ItemDto(null, "Item 2", "Description 2", false, null);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item1)));

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item2)));

        // Получаем вещи владельца
        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        List<ItemDto> items = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));

        assertEquals(2, items.size());
    }

    @Test
    public void getItemsByOwner_noHeader_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItems_withResults_returnsMatchingItems() throws Exception {
        Long userId = createTestUser("owner14@example.com");

        // Создаем доступную вещь
        ItemDto availableItem = new ItemDto(null, "Power Drill", "Heavy duty drill", true, null);
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availableItem)));

        // Создаем недоступную вещь (не должна появиться в поиске)
        ItemDto unavailableItem = new ItemDto(null, "Broken Drill", "Doesn't work", false, null);
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unavailableItem)));

        // Ищем "drill"
        MvcResult result = mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        List<ItemDto> items = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));

        assertEquals(1, items.size());
        assertEquals("Power Drill", items.get(0).getName());
        assertTrue(items.get(0).getAvailable());
    }

    @Test
    public void searchItems_emptyText_returnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        List<ItemDto> items = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));

        assertTrue(items.isEmpty());
    }

    @Test
    public void searchItems_noMatchingItems_returnsEmptyList() throws Exception {
        Long userId = createTestUser("owner15@example.com");

        ItemDto item = new ItemDto(null, "Hammer", "Heavy hammer", true, null);
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        MvcResult result = mockMvc.perform(get("/items/search")
                        .param("text", "drill")) // Ищем "drill", а есть только "hammer"
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        List<ItemDto> items = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDto.class));

        assertTrue(items.isEmpty());
    }
}
