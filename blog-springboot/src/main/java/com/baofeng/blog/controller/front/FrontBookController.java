package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.BookDTO.*;
import com.baofeng.blog.service.BookExportService;
import com.baofeng.blog.controller.admin.AdminBookController;
import com.baofeng.blog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/front/book")
@RequiredArgsConstructor
public class FrontBookController {

    private final BookService bookService;
    private final BookExportService bookExportService;

    /** 上架的书列表 */
    @GetMapping("/list")
    public ApiResponse<java.util.List<BookListResponse>> getBooks() {
        return bookService.getBooks(true);
    }

    /** 书详情 + 章节目录 */
    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id, true);
    }

    /** 章正文 + 上下章 */
    @GetMapping("/{bookId}/chapter/{articleId}")
    public ApiResponse<ChapterResponse> getChapter(@PathVariable Long bookId, @PathVariable Long articleId) {
        return bookService.getChapter(bookId, articleId, true);
    }

    /** 导出 ZIP（仅上架的书） */
    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> exportBook(@PathVariable Long id) {
        try {
            BookExportService.ExportFile file = bookExportService.exportZip(id, true);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"书不存在\"}".getBytes(StandardCharsets.UTF_8));
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, AdminBookController.buildDisposition(file.filename()))
                    .body(file.data());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"message\":\"导出失败: " + e.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8));
        }
    }
}
