package com.example.linememo.dao;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ImageUriConverter {
    @TypeConverter
    public static String listToString(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public List<String> stringToList(String str) {
        if (str == null) return Collections.emptyList();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(str, listType);
    }
}
