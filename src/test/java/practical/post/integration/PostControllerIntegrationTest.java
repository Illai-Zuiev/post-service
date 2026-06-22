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
import practical.post.model.dto.post.PostDto;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.repository.UserRepository;
import practical.post.security.JwtTokenProvider;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = PostApplication.class)
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PostControllerIntegrationTest {
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
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(2))
        );
        Hibernate.initialize(admin.getRoles());
        adminJwt = "Bearer " + jwtTokenProvider.generateToken(admin);

        User user = userRepository.findByIdAndDeletedFalse(1).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(1))
        );
        Hibernate.initialize(user.getRoles());
        userJwt = "Bearer " + jwtTokenProvider.generateToken(user);
    }

    @Test
    @Transactional
    void savePost_ok_200() throws Exception {
        PostRequest postRequest = new PostRequest("test_title_beta", "test_content");

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/post/create")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postRequest))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PostDto> responseDto = parseResponse(response.getResponse().getContentAsByteArray());

        PostDto postDto = responseDto.getBody();

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(postRequest.getTitle(), postDto.getTitle());
        Assertions.assertEquals(postRequest.getContent(), postDto.getContent());
    }

    @Test
    @Transactional
    void savePost_conflict_409() throws Exception {
        PostRequest postRequest = new PostRequest("test_title", "test_content");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/post/create")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postRequest))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void findById_ok_200() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/post/1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PostDto> responseDto = parseResponse(response.getResponse().getContentAsByteArray());

        PostDto postDto = responseDto.getBody();

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(1, postDto.getId());
        Assertions.assertEquals("title1", postDto.getTitle());
        Assertions.assertEquals("content111", postDto.getContent());
    }

    @Test
    void findById_notFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/post/0")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void delete_ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/post/delete/1")
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
                                .delete("/post/delete/0")
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
                                .delete("/post/delete/1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    @Transactional
    void update_ok_200() throws Exception {
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("test_title_beta1", "test_content");

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/update/3")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePostRequest))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomResponse<PostDto> responseDto = parseResponse(response.getResponse().getContentAsByteArray());

        PostDto postDto = responseDto.getBody();

        Assertions.assertTrue(responseDto.isSuccess());
        Assertions.assertEquals(updatePostRequest.getTitle(), postDto.getTitle());
        Assertions.assertEquals(updatePostRequest.getContent(), postDto.getContent());
    }

    @Test
    @Transactional
    void update_notFound_404() throws Exception {
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("test_title_beta1", "test_content");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/update/0")
                                .header("Authorization", adminJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePostRequest))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void update_forbidden_403() throws Exception {
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("test_title_beta1", "test_content");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/update/1")
                                .header("Authorization", userJwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePostRequest))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private CustomResponse<PostDto> parseResponse(byte[] content) {
        try {
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(content, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
