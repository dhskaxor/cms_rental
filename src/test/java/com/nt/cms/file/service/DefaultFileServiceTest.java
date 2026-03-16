package com.nt.cms.file.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.mapper.FileMapper;
import com.nt.cms.file.vo.FileVO;
import com.nt.cms.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * DefaultFileService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultFileService 테스트")
class DefaultFileServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CmsProperties cmsProperties;
    private DefaultFileService fileService;

    @BeforeEach
    void setUp() throws Exception {
        cmsProperties = new CmsProperties();
        CmsProperties.File fileConfig = new CmsProperties.File();
        fileConfig.setUploadPath(Files.createTempDirectory("cms-file-test").toString());
        fileConfig.setMaxSize(3145728L); // 3MB
        cmsProperties.setFile(fileConfig);
        fileService = new DefaultFileService(fileMapper, cmsProperties,
                auditLogService, userMapper, objectMapper);
    }

    private MultipartFile createValidMultipartFile(String name, String content) {
        return new MockMultipartFile(
                "file",
                name,
                "image/png",
                content != null ? content.getBytes() : new byte[0]);
    }

    @Nested
    @DisplayName("파일 업로드")
    class UploadFileTest {

        @Test
        @DisplayName("파일 업로드 성공")
        void uploadFile_success() {
            // given
            MultipartFile file = createValidMultipartFile("test.png", "test image content");
            FileVO savedFile = FileVO.builder()
                    .id(1L)
                    .refType("POST")
                    .refId(1L)
                    .originalName("test.png")
                    .storedName("uuid.png")
                    .filePath("POST/1/uuid.png")
                    .fileSize(20L)
                    .mimeType("image/png")
                    .createdAt(LocalDateTime.now())
                    .deleted(false)
                    .build();

            given(fileMapper.insert(any(FileVO.class))).willAnswer(invocation -> {
                FileVO f = invocation.getArgument(0);
                f.setId(1L);
                return 1;
            });

            // when
            FileResponse response = fileService.uploadFile(file, "POST", 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getOriginalName()).isEqualTo("test.png");
            assertThat(response.getRefType()).isEqualTo("POST");
            assertThat(response.getRefId()).isEqualTo(1L);

            ArgumentCaptor<FileVO> captor = ArgumentCaptor.forClass(FileVO.class);
            verify(fileMapper).insert(captor.capture());
            assertThat(captor.getValue().getOriginalName()).isEqualTo("test.png");
        }

        @Test
        @DisplayName("빈 파일 업로드 시 예외 발생")
        void uploadFile_emptyFile() {
            MultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

            assertThatThrownBy(() -> fileService.uploadFile(emptyFile, "POST", 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
        }

        @Test
        @DisplayName("허용되지 않은 확장자 시 예외 발생")
        void uploadFile_invalidExtension() {
            MultipartFile file = new MockMultipartFile(
                    "file", "test.exe", "application/octet-stream", "malicious".getBytes());

            assertThatThrownBy(() -> fileService.uploadFile(file, "POST", 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        @Test
        @DisplayName("파일 크기 초과 시 예외 발생")
        void uploadFile_sizeExceeded() {
            cmsProperties.getFile().setMaxSize(10L); // 10 bytes
            MultipartFile file = createValidMultipartFile("large.png", "this is more than 10 bytes");

            assertThatThrownBy(() -> fileService.uploadFile(file, "POST", 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    @Nested
    @DisplayName("파일 다운로드")
    class DownloadFileTest {

        @Test
        @DisplayName("존재하지 않는 파일 다운로드 시 예외 발생")
        void downloadFile_notFound() {
            given(fileMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> fileService.downloadFile(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("파일 정보 조회")
    class GetFileInfoTest {

        @Test
        @DisplayName("파일 정보 조회 성공")
        void getFileInfo_success() {
            FileVO fileVO = FileVO.builder()
                    .id(1L)
                    .refType("POST")
                    .refId(1L)
                    .originalName("test.png")
                    .fileSize(100L)
                    .mimeType("image/png")
                    .createdAt(LocalDateTime.now())
                    .build();

            given(fileMapper.findById(1L)).willReturn(fileVO);

            FileResponse response = fileService.getFileInfo(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getOriginalName()).isEqualTo("test.png");
        }

        @Test
        @DisplayName("존재하지 않는 파일 조회 시 예외 발생")
        void getFileInfo_notFound() {
            given(fileMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> fileService.getFileInfo(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("참조별 파일 목록 조회")
    class GetFilesByRefTest {

        @Test
        @DisplayName("파일 목록 조회 성공")
        void getFilesByRef_success() {
            FileVO fileVO = FileVO.builder()
                    .id(1L)
                    .refType("POST")
                    .refId(1L)
                    .originalName("test.png")
                    .build();

            given(fileMapper.findByRef("POST", 1L)).willReturn(List.of(fileVO));

            List<FileResponse> responses = fileService.getFilesByRef("POST", 1L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getOriginalName()).isEqualTo("test.png");
        }

        @Test
        @DisplayName("파일이 없을 때 빈 목록 반환")
        void getFilesByRef_empty() {
            given(fileMapper.findByRef("POST", 1L)).willReturn(Collections.emptyList());

            List<FileResponse> responses = fileService.getFilesByRef("POST", 1L);

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("파일 삭제")
    class DeleteFileTest {

        @Test
        @DisplayName("존재하지 않는 파일 삭제 시 예외 발생")
        void deleteFile_notFound() {
            given(fileMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> fileService.deleteFile(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);
        }

        @Test
        @DisplayName("파일 삭제 성공 - DB soft delete 호출")
        void deleteFile_success() {
            FileVO fileVO = FileVO.builder()
                    .id(1L)
                    .filePath("POST/1/nonexistent.png")
                    .build();

            given(fileMapper.findById(1L)).willReturn(fileVO);
            given(userMapper.findUsernameById(1L)).willReturn("testuser");

            fileService.deleteFile(1L, 1L);

            verify(fileMapper).delete(1L);
        }
    }

    @Nested
    @DisplayName("다중 파일 업로드")
    class UploadFilesTest {

        @Test
        @DisplayName("빈 목록 업로드 시 빈 목록 반환")
        void uploadFiles_emptyList() {
            List<FileResponse> responses = fileService.uploadFiles(
                    Collections.emptyList(), "POST", 1L, 5);

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("파일 개수 초과 시 예외 발생")
        void uploadFiles_countExceeded() {
            MultipartFile f1 = createValidMultipartFile("a.png", "a");
            MultipartFile f2 = createValidMultipartFile("b.png", "b");
            MultipartFile f3 = createValidMultipartFile("c.png", "c");

            assertThatThrownBy(() -> fileService.uploadFiles(
                    List.of(f1, f2, f3), "POST", 1L, 2))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_COUNT_EXCEEDED);
        }
    }
}
