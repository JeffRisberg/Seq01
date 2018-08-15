package com.company.jobServer.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 */
public class RestTools {
    private static ThreadLocal<Gson> gsonPool = ThreadLocal.withInitial(() -> {
        GsonBuilder builder = new GsonBuilder();
        return builder.disableHtmlEscaping().create();
    });

    public static final String SUCCESS_JSON_MESSAGE = "{\"message\":\"ok\"}";

    /**
     * JSON error message
     *
     * @param message
     * @return
     */
    public static String getErrorJson(String message) {
        return getErrorJson(message, Optional.empty());
    }

    public static String getErrorJson(String message, Throwable e) {
        return getErrorJson(message, Optional.of(e));
    }

    public static <T> T parseJson(String json, Class<T> classType) {
        return gsonPool.get().fromJson(json, classType);
    }

    public static <T> T parseJson(Reader json, Class<T> classType) {
        return gsonPool.get().fromJson(json, classType);
    }

    public static String toJson(Object object) {
        return gsonPool.get().toJson(object);
    }

    public static <T extends Message> String toJson(List<T> messages) {
        List<Map<String, Object>> root = new ArrayList<>();
        messages.forEach(message -> root.add(toMap(message)));
        return toJson(root);
    }

    public static <T extends Message> String toJson(T message) {
        return toJson(toMap(message));
    }

    private static <T extends Message> Map<String, Object> toMap(T message) {
        Map<String, Object> attrs = new HashMap<>();
        message.getAllFields().forEach((k, v) -> {
            switch (k.getType()) {
                case MESSAGE:
                    System.out.println (k.isRepeated());
                    if (k.isRepeated()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        List<Message> msgs = (List<Message>)v;
                        msgs.forEach(msg -> list.add(toMap(msg)));
                        attrs.put(k.getJsonName(), list);
                    } else {
                        attrs.put(k.getJsonName(), toMap((Message) v));
                    }
                    break;
                case GROUP:
                default:
                    attrs.put(k.getJsonName(), v);
                    break;
            }
        });
        return attrs;
    }

    public static String getErrorJson(String message, Boolean userError, Optional<Throwable> e) {
        RequestError error = new RequestError();
        error.setErrorCode(500);
        error.setUserError(userError);
        error.setMessage(message);
        StringBuilder sb = new StringBuilder();
        e.ifPresent(throwable -> sb.append(" : ").append(e.toString()));
        error.setErrorMessage(sb.toString());
        return toJson(error);
    }

    public static String getErrorJson(String message, Optional<Throwable> e) {
        RequestError error = new RequestError();
        error.setErrorCode(500);
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        e.ifPresent(throwable -> sb.append(" : ").append(e.toString()));
        error.setErrorMessage(sb.toString());
        return toJson(error);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestError {
        private int errorCode;
        private String errorMessage;
        private String message; // Show to the User if userError=true
        private Boolean userError; // Display to the user?
    }
}
