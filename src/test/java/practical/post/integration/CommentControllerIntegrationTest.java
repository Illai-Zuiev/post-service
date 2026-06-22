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
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.comment.CommentRequest;
import practical.post.model.request.comment.CommentSearchRequest;
import practical.post.model.request.comment.UpdateCommentRequest;
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
public class CommentControllerIntegrationTest {

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
    private String userJwt;

    @BeforeAll
    void authorize() {
        User admin = userRepository.findByIdAndDeletedFalse(2).orElseThrow(
                () -> new NotFoundException(
                        ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(2))
        );

        Hibernate.initialize(admin.getRoles());
        adminJwt = "Bearer " + jwtTokenProvider.generateToken(admin);

        User user = userRepository.findByIdAndDeletedFalse(1).orElseThrow(
                () -> new NotFoundException(
                        ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(1))
        );

        Hibernate.initialize(user.getRoles());
        userJwt = "Bearer " + jwtTokenProvider.generateToken(user);
    }

    @Test
    @Transactional
    void saveComment_ok_200() throws Exception {
        CommentRequest request =
                new CommentRequest(
                        1,
                        "integration test comment content"
                );

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/create")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<CommentDto> responseDto =
                parseCommentResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(
                request.getContent(),
                responseDto.getBody().getContent()
        );
        Assertions.assertEquals(
                request.getPostId(),
                responseDto.getBody().getPostId()
        );
    }

    @Test
    @Transactional
    void saveComment_postNotFound_404() throws Exception {
        CommentRequest request =
                new CommentRequest(
                        99999,
                        "integration test comment content"
                );

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/create")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findById_ok_200() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/2")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<CommentDto> responseDto =
                parseCommentResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(2, responseDto.getBody().getId());
    }

    @Test
    void findById_notFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/0")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void update_ok_200() throws Exception {
        UpdateCommentRequest request =
                new UpdateCommentRequest(
                        "updated integration comment"
                );

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/update/2")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<CommentDto> responseDto =
                parseCommentResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(
                request.getContent(),
                responseDto.getBody().getContent()
        );
    }

    @Test
    @Transactional
    void update_notFound_404() throws Exception {
        UpdateCommentRequest request =
                new UpdateCommentRequest(
                        "updated integration comment"
                );

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/update/0")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void update_forbidden_403() throws Exception {
        UpdateCommentRequest request =
                new UpdateCommentRequest(
                        "updated integration comment"
                );

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/update/2")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void delete_ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/delete/2")
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
                                .delete("/comment/delete/0")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void delete_forbidden_403() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/delete/2")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void findAll_ok_200() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/all")
                                .header("Authorization", userJwt)
                                .param("page", "0")
                                .param("limit", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PaginationResponse<CommentSearchDto>> responseDto =
                parsePaginationResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertNotNull(responseDto.getBody());
    }

    @Test
    void findAllWithCriteria_ok_200() throws Exception {
        CommentSearchRequest request = new CommentSearchRequest();

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/search")
                                .header("Authorization", userJwt)
                                .param("page", "0")
                                .param("limit", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PaginationResponse<CommentSearchDto>> responseDto =
                parsePaginationResponse(response.getResponse().getContentAsByteArray());

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertNotNull(responseDto.getBody());
    }

    private CustomResponse<CommentDto> parseCommentResponse(byte[] content) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(
                    content,
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private CustomResponse<PaginationResponse<CommentSearchDto>> parsePaginationResponse(byte[] content) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(
                    content,
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}