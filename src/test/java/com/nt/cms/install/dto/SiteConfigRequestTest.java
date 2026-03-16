package com.nt.cms.install.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SiteConfigRequest 테스트
 * 
 * @author CMS Team
 */
class SiteConfigRequestTest {

    @Test
    @DisplayName("기본값이 올바르게 설정되어야 한다")
    void defaultValues() {
        // given
        SiteConfigRequest request = new SiteConfigRequest();

        // then
        assertEquals("CMS Core", request.getSiteName());
        assertEquals("C:/cms/files", request.getUploadPath());
    }

    @Test
    @DisplayName("커스텀 값이 올바르게 설정되어야 한다")
    void customValues() {
        // given
        SiteConfigRequest request = new SiteConfigRequest();
        request.setSiteName("My Website");
        request.setUploadPath("D:/uploads");

        // then
        assertEquals("My Website", request.getSiteName());
        assertEquals("D:/uploads", request.getUploadPath());
    }
}
