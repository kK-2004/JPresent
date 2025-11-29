package com.JPresent.service;

import com.JPresent.model.Presentation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * 文件读写服务。
 */
public class FileService {

    private final ObjectMapper mapper;

    public FileService() {
        mapper = new ObjectMapper();
        // 兼容旧版本或其他工具生成的 JSON，忽略未识别字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void save(Presentation presentation, File file) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, presentation);
    }

    public void load(Presentation target, File file) throws IOException {
        Presentation loaded = mapper.readValue(file, Presentation.class);
        target.setSlides(loaded.getSlides());
        target.setCurrentIndex(loaded.getCurrentIndex());
    }
}
