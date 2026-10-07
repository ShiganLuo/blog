package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.BookDTO.*;
import com.baofeng.blog.service.BookExportService;
import com.baofeng.blog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/book")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;
    private final BookExportService bookExportService;

    @GetMapping("/list")
    public ApiResponse<List<BookListResponse>> getBooks() {
        return bookService.getBooks(false);
    }

    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id, false);
    }

    @PostMapping("/add")
    public ApiResponse<String> addBook(@RequestBody @Validated AddBookRequest request) {
        return bookService.addBook(request);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateBook(@RequestBody @Validated UpdateBookRequest request) {
        return bookService.updateBook(request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }

    @PostMapping("/addArticles")
    public ApiResponse<String> addArticles(@RequestBody @Validated AddArticlesRequest request) {
        return bookService.addArticles(request);
    }

    @PostMapping("/removeArticle")
    public ApiResponse<String> removeArticle(@RequestBody @Validated RemoveArticleRequest request) {
        return bookService.removeArticle(request);
    }

    @PostMapping("/reorder")
    public ApiResponse<String> reorder(@RequestBody @Validated ReorderRequest request) {
        return bookService.reorder(request);
    }

    @PutMapping("/renameChapter")
    public ApiResponse<String> renameChapter(@RequestBody @Validated RenameChapterRequest request) {
        return bookService.renameChapter(request);
    }

    /** 导出 ZIP（管理端） */
    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> exportBook(@PathVariable Long id) {
        try {
            BookExportService.ExportFile file = bookExportService.exportZip(id, false);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"书不存在\"}".getBytes(StandardCharsets.UTF_8));
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, buildDisposition(file.filename()))
                    .body(file.data());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"message\":\"导出失败: " + e.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String buildDisposition(String filename) {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + "export.zip" + "\"; filename*=UTF-8''" + encoded;
    }
}
