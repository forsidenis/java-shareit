package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    public void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(1L)
                .build();
    }

    @Test
    public void createItem_EmptyName_ReturnsBadRequest() throws Exception {
        ItemDto invalidItem = ItemDto.builder()
                .name("")
                .description("Test Description")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItem_EmptyName_ReturnsOk() throws Exception {
        // Теперь используем ItemUpdateDto
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("")
                .description("Updated Description")
                .build();

        when(itemService.updateItem(anyLong(), any(ItemUpdateDto.class), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void searchItems_EmptyText_ReturnsEmptyList() throws Exception {
        when(itemService.searchItems(anyString(), anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void searchItems_NullText_ReturnsEmptyList() throws Exception {
        when(itemService.searchItems(anyString(), anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void createItem_ValidItem_ReturnsCreated() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    public void getItem_ValidRequest_ReturnsOk() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void updateItem_ValidRequest_ReturnsOk() throws Exception {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemResponseDto updatedResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .ownerId(1L)
                .build();

        when(itemService.updateItem(anyLong(), any(ItemUpdateDto.class), anyLong())).thenReturn(updatedResponseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }
}
