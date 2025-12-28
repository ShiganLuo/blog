package com.baofeng.blog.common.advice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final Logger logger = LoggerFactory.getLogger(CustomLocalDateTimeDeserializer.class);
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        if (text.isEmpty()) {
            return null;
        }

        // 1️⃣ ISO-8601（前端最常见）
        try {
            return LocalDateTime.parse(text, ISO_FORMATTER);
        } catch (DateTimeParseException ignored) {
            logger.error(text,ignored.getMessage());
        }

        // 2️⃣ yyyy-MM-dd HH:mm:ss
        try {
            return LocalDateTime.parse(text, DATETIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            logger.error(text,ignored.getMessage());
        }

        // 3️⃣ yyyy-MM-dd → 补 00:00:00
        try {
            return LocalDate.parse(text, DATE_FORMATTER).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            logger.error(text,ignored.getMessage());
        }
        logger.error("无法解析的日期时间格式: " + text);
        throw new IOException("无法解析的日期时间格式: " + text);
    }
}
