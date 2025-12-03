package com.JPresent.service;

import com.JPresent.model.Presentation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 简单的撤销/重做服务。
 * 通过 JSON 快照保存 Presentation 的完整状态。
 */
public class UndoRedoService {

    private static final int MAX_HISTORY = 5;

    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new ArrayDeque<>();
    private final ObjectMapper mapper;

    public UndoRedoService() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 记录当前状态快照（用于后续撤销）。
     */
    public void snapshot(Presentation presentation) {
        if (presentation == null) {
            return;
        }
        try {
            String json = mapper.writeValueAsString(presentation);
            // 只保留最近 MAX_HISTORY 条记录
            if (undoStack.size() >= MAX_HISTORY) {
                undoStack.removeLast();
            }
            undoStack.push(json);
            redoStack.clear();
        } catch (IOException e) {
            // 快照失败时忽略，以保证功能可用性
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public void undo(Presentation target) throws IOException {
        if (!canUndo() || target == null) {
            return;
        }
        String current = mapper.writeValueAsString(target);
        redoStack.push(current);
        String prev = undoStack.pop();
        restoreFromJson(target, prev);
    }

    public void redo(Presentation target) throws IOException {
        if (!canRedo() || target == null) {
            return;
        }
        String current = mapper.writeValueAsString(target);
        undoStack.push(current);
        String next = redoStack.pop();
        restoreFromJson(target, next);
    }

    private void restoreFromJson(Presentation target, String json) throws IOException {
        Presentation restored = mapper.readValue(json, Presentation.class);
        target.setSlides(restored.getSlides());
        target.setCurrentIndex(restored.getCurrentIndex());
    }
}
