package practical.post.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import practical.post.mapper.PostMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.post.PostDto;
import practical.post.model.dto.post.PostSearchDto;
import practical.post.model.entity.Post;
import practical.post.model.entity.User;
import practical.post.model.exceptions.DataExistsException;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.post.PostRequest;
import practical.post.model.request.post.PostSearchRequest;
import practical.post.model.request.post.UpdatePostRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.PostRepository;
import practical.post.repository.UserRepository;
import practical.post.service.impl.AccessValidationService;
import practical.post.service.impl.PostServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private AccessValidationService accessValidationService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;
    private PostDto testPostDto;
    private PostSearchDto testPostSearchDto;
    private User testUser;

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("testpost");
        testPost.setContent("testcontent");
        testPost.setUser(testUser);

        testPostDto = new PostDto();
        testPostDto.setId(1);
        testPostDto.setTitle("testpost");
        testPostDto.setContent("testcontent");

        testPostSearchDto = new PostSearchDto();
        testPostSearchDto.setId(1);
        testPostSearchDto.setTitle("testpost");
    }

    @Test
    void findById_PostExists_ReturnsCustomResponseWithPostDto() {
        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        when(postMapper.convertPostToPostDto(testPost))
                .thenReturn(testPostDto);

        CustomResponse<PostDto> response = postService.findById(1);

        assertNotNull(response);
        assertEquals(testPostDto.getId(), response.getBody().getId());
        assertEquals(testPostDto.getTitle(), response.getBody().getTitle());
        assertEquals(testPostDto.getContent(), response.getBody().getContent());

        verify(postRepository).findByIdAndDeletedFalse(1);
        verify(postMapper).convertPostToPostDto(testPost);
    }

    @Test
    void findById_PostNotFound_ThrownNotFoundException() {
        when(postRepository.findByIdAndDeletedFalse(999))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> postService.findById(999)
        );

        assertEquals(
                ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(999),
                exception.getMessage()
        );

        verify(postMapper, never()).convertPostToPostDto(any());
    }

    @Test
    void save_PostSaved_ReturnsCustomResponseWithPostDto() {
        PostRequest request =
                new PostRequest("testpost", "testcontent");

        doNothing()
                .when(accessValidationService)
                .validatePostBeforeCreating(request);

        when(userRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testUser));

        when(postMapper.convertPostRequestToPost(request))
                .thenReturn(testPost);

        when(postRepository.save(testPost))
                .thenReturn(testPost);

        when(postMapper.convertPostToPostDto(testPost))
                .thenReturn(testPostDto);

        CustomResponse<PostDto> response =
                postService.save(request, 1);

        assertNotNull(response);
        assertEquals(testPostDto.getId(), response.getBody().getId());

        verify(accessValidationService)
                .validatePostBeforeCreating(request);

        verify(postRepository)
                .save(testPost);
    }

    @Test
    void save_PostTitleAlreadyExists_ThrownDataExistsException() {
        PostRequest request =
                new PostRequest("testpost", "testcontent");

        doThrow(new DataExistsException(
                ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage("testpost")
        ))
                .when(accessValidationService)
                .validatePostBeforeCreating(request);

        DataExistsException exception = assertThrows(
                DataExistsException.class,
                () -> postService.save(request, 1)
        );

        assertEquals(
                ApiErrorMessage.POST_WITH_THIS_TITLE_EXIST.getMessage("testpost"),
                exception.getMessage()
        );

        verify(userRepository, never())
                .findByIdAndDeletedFalse(anyInt());
    }

    @Test
    void save_PostOwnerNotFound_ThrownNotFoundException() {
        PostRequest request =
                new PostRequest("testpost", "testcontent");

        doNothing()
                .when(accessValidationService)
                .validatePostBeforeCreating(request);

        when(userRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> postService.save(request, 1)
        );

        assertEquals(
                ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(1),
                exception.getMessage()
        );

        verify(postRepository, never())
                .save(any());
    }

    @Test
    void update_PostUpdated_ReturnsCustomResponseWithPostDto() {
        UpdatePostRequest request =
                new UpdatePostRequest("newTitle", "newContent");

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        when(postRepository.existsByTitle("newTitle"))
                .thenReturn(false);

        when(postMapper.convertPostToPostDto(testPost))
                .thenReturn(testPostDto);

        CustomResponse<PostDto> response =
                postService.update(1, 1, request);

        assertNotNull(response);

        verify(postRepository)
                .existsByTitle("newTitle");

        verify(postMapper)
                .convertPostToPostDto(testPost);
    }

    @Test
    void update_PostNotFound_ThrowsNotFoundException() {
        UpdatePostRequest request =
                new UpdatePostRequest("newTitle", "newContent");

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> postService.update(1, 1, request)
        );

        verify(accessValidationService, never())
                .validateOwner(anyInt(), anyInt());
    }

    @Test
    void update_TitleAlreadyExists_ThrowsDataExistsException() {
        UpdatePostRequest request =
                new UpdatePostRequest("newTitle", "content");

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        when(postRepository.existsByTitle("newTitle"))
                .thenReturn(true);

        assertThrows(
                DataExistsException.class,
                () -> postService.update(1, 1, request)
        );

        verify(postMapper, never())
                .convertPostToPostDto(any());
    }

    @Test
    void update_SameTitle_DoesNotCheckExistsByTitle() {
        UpdatePostRequest request =
                new UpdatePostRequest("testpost", "content");

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        when(postMapper.convertPostToPostDto(testPost))
                .thenReturn(testPostDto);

        postService.update(1, 1, request);

        verify(postRepository, never())
                .existsByTitle(anyString());
    }

    @Test
    void delete_Success() {
        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        postService.delete(1, 1);

        assertTrue(testPost.isDeleted());

        verify(postRepository)
                .findByIdAndDeletedFalse(1);

        verify(accessValidationService)
                .validateOwner(1, 1);
    }

    @Test
    void delete_PostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> postService.delete(1, 1)
        );
    }

    @Test
    void delete_NotOwner() {
        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        doThrow(new RuntimeException("Access denied"))
                .when(accessValidationService)
                .validateOwner(2, 1);

        assertThrows(
                RuntimeException.class,
                () -> postService.delete(1, 2)
        );
    }

    @Test
    void findAllByPage_ReturnsPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(
                List.of(testPost),
                pageable,
                1
        );

        when(postRepository.findAll(pageable))
                .thenReturn(page);

        when(postMapper.convertPostToPostSearchDto(testPost))
                .thenReturn(testPostSearchDto);

        CustomResponse<PaginationResponse<PostSearchDto>> response =
                postService.findAllByPage(pageable);

        assertNotNull(response);
        assertEquals(
                1,
                response.getBody().getPagination().getTotal()
        );

        verify(postRepository)
                .findAll(pageable);
    }

    @Test
    void findAllByPageWithCriteria_ReturnsPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 10);

        PostSearchRequest request =
                new PostSearchRequest();

        Page<Post> page = new PageImpl<>(
                List.of(testPost),
                pageable,
                1
        );

        when(postRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(page);

        when(postMapper.convertPostToPostSearchDto(testPost))
                .thenReturn(testPostSearchDto);

        CustomResponse<PaginationResponse<PostSearchDto>> response =
                postService.findAllByPageWithCriteria(
                        request,
                        pageable
                );

        assertNotNull(response);

        assertEquals(
                1,
                response.getBody().getPagination().getTotal()
        );

        verify(postRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}