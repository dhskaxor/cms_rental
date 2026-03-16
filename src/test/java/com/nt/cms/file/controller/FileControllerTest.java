package com.nt.cms.file.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.test.support.WithMockCustomUser;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FileController 테스트
 */
@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("FileController 테스트")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private FileResponse createTestFileResponse() {
        return FileResponse.builder()
                .id(1L)
                .refType("POST")
                .refId(1L)
                .originalName("test.png")
                .fileSize(100L)
                .mimeType("image/png")
                .createdAt(LocalDateTime.now())
                .downloadUrl("/api/v1/files/1/download")
                .build();
    }

    @Nested
    @DisplayName("파일 업로드 API")
    class UploadFileTest {

        @Test
        @DisplayName("파일 업로드 성공")
        @WithMockUser(authorities = "FILE_CREATE")
        void uploadFile_success() throws Exception {
            FileResponse response = createTestFileResponse();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.png", "image/png", "test content".getBytes());

            given(fileService.uploadFile(any(), eq("POST"), eq(1L))).willReturn(response);

            mockMvc.perform(multipart("/api/v1/files/upload")
                            .file(file)
                            .param("refType", "POST")
                            .param("refId", "1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.originalName").value("test.png"));

            verify(fileService).uploadFile(any(), eq("POST"), eq(1L));
        }
    }

    @Nested
    @DisplayName("파일 정보 조회 API")
    class GetFileInfoTest {

        @Test
        @DisplayName("파일 정보 조회 성공")
        @WithMockUser(authorities = "FILE_READ")
        void getFileInfo_success() throws Exception {
            FileResponse response = createTestFileResponse();
            given(fileService.getFileInfo(1L)).willReturn(response);

            mockMvc.perform(get("/api/v1/files/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));

            verify(fileService).getFileInfo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 파일 조회 시 404")
        @WithMockUser(authorities = "FILE_READ")
        void getFileInfo_notFound() throws Exception {
            doThrow(new BusinessException(ErrorCode.FILE_NOT_FOUND)).when(fileService).getFileInfo(999L);

            mockMvc.perform(get("/api/v1/files/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("파일 목록 조회 API")
    class GetFilesByRefTest {

        @Test
        @DisplayName("파일 목록 조회 성공")
        @WithMockUser(authorities = "FILE_READ")
        void getFilesByRef_success() throws Exception {
            List<FileResponse> responses = List.of(createTestFileResponse());
            given(fileService.getFilesByRef("POST", 1L)).willReturn(responses);

            mockMvc.perform(get("/api/v1/files")
                            .param("refType", "POST")
                            .param("refId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].originalName").value("test.png"));

            verify(fileService).getFilesByRef("POST", 1L);
        }
    }

    @Nested
    @DisplayName("파일 삭제 API")
    class DeleteFileTest {

        @Test
        @DisplayName("파일 삭제 성공")
        @WithMockCustomUser(userId = 1L, authorities = {"FILE_DELETE"})
        void deleteFile_success() throws Exception {
            mockMvc.perform(delete("/api/v1/files/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(fileService).deleteFile(1L, 1L);
        }
    }
}
