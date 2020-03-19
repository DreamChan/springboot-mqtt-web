package cn.dreamchan.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JacksonUtils() {
    }

    public static <T> T readValue(String json, Class<T> valueType) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(formatter);
            return objectMapper.readValue(json, valueType);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static <T> T readValue(String json, Class<T> valueType, SimpleDateFormat formatter) {
        try {
            objectMapper.setDateFormat(formatter);
            return objectMapper.readValue(json, valueType);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String writeValue(Object bean, SimpleDateFormat formatter) {
        try {
            objectMapper.setDateFormat(formatter);
            return objectMapper.writeValueAsString(bean);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String writeValue(Object bean) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
            objectMapper.setDateFormat(formatter);
            return objectMapper.writeValueAsString(bean);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
