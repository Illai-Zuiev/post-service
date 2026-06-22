package practical.post.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import practical.post.PostApplication;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.user.UserDto;
import practical.post.model.dto.user.UserSearchDto;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.user.UpdateUserRequest;
import practical.post.model.request.user.UserRequest;
import practical.post.model.request.user.UserSearchRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.UserRepository;
import practical.post.security.JwtTokenProvider;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = PostApplication.class)
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Setter
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Setter
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminJwt;

    @BeforeAll
    void authorize() {
        User admin = userRepository.findByIdAndDeletedFalse(2).orElseThrow(
                () -> new NotFoundException(
                        ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(2))
        );

        Hibernate.initialize(admin.getRoles());

        adminJwt = "Bearer " + jwtTokenProvider.generateToken(admin);
    }

    @Test
    @Transactional
    void saveUser_ok_200() throws Exception {
        UserRequest request = new UserRequest(
                "integration_user",
                "integration_user@gmail.com",
                "password123"
        );

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/user/create")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<UserDto> responseDto =
                parseUserResponse(response.getResponse().getContentAsByteArray());

        UserDto userDto = responseDto.getBody();

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(request.getUsername(), userDto.getUsername());
        Assertions.assertEquals(request.getEmail(), userDto.getEmail());
    }

    @Test
    @Transactional
    void findById_ok_200() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/user/1")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<UserDto> responseDto =
                parseUserResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(1, responseDto.getBody().getId());
    }

    @Test
    void findById_notFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/user/0")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void update_ok_200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "updated_user",
                "updated@gmail.com"
        );

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/user/update/1")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<UserDto> responseDto =
                parseUserResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(request.getUsername(), responseDto.getBody().getUsername());
        Assertions.assertEquals(request.getEmail(), responseDto.getBody().getEmail());
    }

    @Test
    @Transactional
    void update_notFound_404() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest(
                "updated_user",
                "updated@gmail.com"
        );

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/user/update/0")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void delete_ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/user/delete/1")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void delete_notFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/user/delete/0")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findAllUsers_ok_200() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/user/all")
                                .header("Authorization", adminJwt)
                                .param("page", "0")
                                .param("limit", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PaginationResponse<UserSearchDto>> responseDto =
                parsePaginationResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertNotNull(responseDto.getBody());
    }

    @Test
    void findAllUsersWithCriteria_ok_200() throws Exception {
        UserSearchRequest request = new UserSearchRequest();

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/user/search")
                                .header("Authorization", adminJwt)
                                .param("page", "0")
                                .param("limit", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PaginationResponse<UserSearchDto>> responseDto =
                parsePaginationResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertNotNull(responseDto.getBody());
    }

    private CustomResponse<UserDto> parseUserResponse(byte[] content) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private CustomResponse<PaginationResponse<UserSearchDto>> parsePaginationResponse(byte[] content) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}