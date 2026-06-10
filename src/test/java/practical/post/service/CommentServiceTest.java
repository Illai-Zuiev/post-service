package practical.post.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import practical.post.mapper.CommentMapper;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.dto.comment.CommentDto;
import practical.post.model.dto.comment.CommentSearchDto;
import practical.post.model.entity.Comment;
import practical.post.model.entity.Post;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.model.request.comment.CommentRequest;
import practical.post.model.request.comment.CommentSearchRequest;
import practical.post.model.request.comment.UpdateCommentRequest;
import practical.post.model.response.CustomResponse;
import practical.post.model.response.PaginationResponse;
import practical.post.repository.CommentRepository;
import practical.post.repository.PostRepository;
import practical.post.repository.UserRepository;
import practical.post.service.impl.AccessValidationService;
import practical.post.service.impl.CommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private AccessValidationService accessValidationService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CommentDto testCommentDto;
    private CommentSearchDto testCommentSearchDto;

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("post");

        testComment = new Comment();
        testComment.setId(1);
        testComment.setContent("comment content");
        testComment.setUser(testUser);
        testComment.setPost(testPost);

        testCommentDto = new CommentDto();
        testCommentDto.setId(1);
        testCommentDto.setContent("comment content");

        testCommentSearchDto = new CommentSearchDto();
        testCommentSearchDto.setId(1);
        testCommentSearchDto.setContent("comment content");
    }

    @Test
    void findById_CommentExists_ReturnsCommentDto() {
        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testComment));

        when(commentMapper.convertCommentToCommentDto(testComment))
                .thenReturn(testCommentDto);

        CustomResponse<CommentDto> response =
                commentService.findById(1);

        assertNotNull(response);
        assertEquals(1, response.getBody().getId());
        assertEquals("comment content",
                response.getBody().getContent());

        verify(commentRepository)
                .findByIdAndDeletedFalse(1);

        verify(commentMapper)
                .convertCommentToCommentDto(testComment);
    }

    @Test
    void findById_CommentNotFound_ThrowsNotFoundException() {
        when(commentRepository.findByIdAndDeletedFalse(999))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.findById(999)
        );

        assertEquals(
                ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(999),
                exception.getMessage()
        );

        verify(commentMapper, never())
                .convertCommentToCommentDto(any());
    }

    @Test
    void save_CommentSaved_ReturnsCommentDto() {
        CommentRequest request = new CommentRequest();
        request.setContent("comment content");
        request.setPostId(1);

        when(userRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testUser));

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testPost));

        when(commentMapper.convertCommentRequestToComment(request))
                .thenReturn(testComment);

        when(commentRepository.save(testComment))
                .thenReturn(testComment);

        when(commentMapper.convertCommentToCommentDto(testComment))
                .thenReturn(testCommentDto);

        CustomResponse<CommentDto> response =
                commentService.save(request, 1);

        assertNotNull(response);
        assertEquals(1, response.getBody().getId());

        verify(commentRepository)
                .save(testComment);
    }

    @Test
    void save_UserNotFound_ThrowsNotFoundException() {
        CommentRequest request = new CommentRequest();
        request.setPostId(1);

        when(userRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.save(request, 1)
        );

        assertEquals(
                ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(1),
                exception.getMessage()
        );

        verify(postRepository, never())
                .findByIdAndDeletedFalse(anyInt());
    }

    @Test
    void save_PostNotFound_ThrowsNotFoundException() {
        CommentRequest request = new CommentRequest();
        request.setPostId(1);

        when(userRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testUser));

        when(postRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> commentService.save(request, 1)
        );

        verify(commentRepository, never())
                .save(any());
    }

    @Test
    void update_CommentUpdated_ReturnsCommentDto() {
        UpdateCommentRequest request =
                new UpdateCommentRequest("updated content");

        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testComment));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        when(commentMapper.convertCommentToCommentDto(testComment))
                .thenReturn(testCommentDto);

        CustomResponse<CommentDto> response =
                commentService.update(1, 1, request);

        assertNotNull(response);

        assertEquals(
                "updated content",
                testComment.getContent()
        );

        assertNotNull(testComment.getUpdated());

        verify(commentMapper)
                .convertCommentToCommentDto(testComment);
    }

    @Test
    void update_CommentNotFound_ThrowsNotFoundException() {
        UpdateCommentRequest request =
                new UpdateCommentRequest("updated");

        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> commentService.update(1, 1, request)
        );

        verify(accessValidationService, never())
                .validateOwner(anyInt(), anyInt());
    }

    @Test
    void delete_Success() {
        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testComment));

        doNothing()
                .when(accessValidationService)
                .validateOwner(1, 1);

        commentService.delete(1, 1);

        assertTrue(testComment.isDeleted());
        assertNotNull(testComment.getUpdated());

        verify(accessValidationService)
                .validateOwner(1, 1);
    }

    @Test
    void delete_CommentNotFound_ThrowsNotFoundException() {
        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> commentService.delete(1, 1)
        );
    }

    @Test
    void delete_NotOwner_ThrowsException() {
        when(commentRepository.findByIdAndDeletedFalse(1))
                .thenReturn(Optional.of(testComment));

        doThrow(new RuntimeException("Access denied"))
                .when(accessValidationService)
                .validateOwner(2, 1);

        assertThrows(
                RuntimeException.class,
                () -> commentService.delete(1, 2)
        );
    }

    @Test
    void findAllByPage_ReturnsPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Comment> page = new PageImpl<>(
                List.of(testComment),
                pageable,
                1
        );

        when(commentRepository.findAll(pageable))
                .thenReturn(page);

        when(commentMapper.convertCommentToCommentSearchDto(testComment))
                .thenReturn(testCommentSearchDto);

        CustomResponse<PaginationResponse<CommentSearchDto>> response =
                commentService.findAllByPage(pageable);

        assertNotNull(response);

        assertEquals(
                1,
                response.getBody().getPagination().getTotal()
        );

        verify(commentRepository)
                .findAll(pageable);
    }

    @Test
    void findAllByPageWithCriteria_ReturnsPaginationResponse() {
        Pageable pageable = PageRequest.of(0, 10);

        CommentSearchRequest request =
                new CommentSearchRequest();

        Page<Comment> page = new PageImpl<>(
                List.of(testComment),
                pageable,
                1
        );

        when(commentRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(page);

        when(commentMapper.convertCommentToCommentSearchDto(testComment))
                .thenReturn(testCommentSearchDto);

        CustomResponse<PaginationResponse<CommentSearchDto>> response =
                commentService.findAllByPageWithCriteria(
                        request,
                        pageable
                );

        assertNotNull(response);

        assertEquals(
                1,
                response.getBody().getPagination().getTotal()
        );

        verify(commentRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}
