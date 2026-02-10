package com.studentmanagement.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.studentmanagement.models.Student;
import com.studentmanagement.models.User;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonDataService {
    private static final String USERS_FILE = "data/users.json";
    private static final String STUDENTS_FILE = "data/students.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    
    // User operations
    public static List<User> loadUsers() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            // Return default users if file doesn't exist
            return createDefaultUsers();
        }
    }
    
    public static void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static List<User> createDefaultUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("admin", "admin123", "admin"));
        users.add(new User("user", "user123", "user"));
        saveUsers(users);
        return users;
    }
    
    // Student operations
    public static List<Student> loadStudents() {
        try (Reader reader = new FileReader(STUDENTS_FILE)) {
            Type studentListType = new TypeToken<ArrayList<Student>>(){}.getType();
            List<Student> students = gson.fromJson(reader, studentListType);
            return students != null ? students : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public static void saveStudents(List<Student> students) {
        try (Writer writer = new FileWriter(STUDENTS_FILE)) {
            gson.toJson(students, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // LocalDate Adapter for Gson
    private static class LocalDateAdapter implements com.google.gson.JsonSerializer<LocalDate>,
            com.google.gson.JsonDeserializer<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        @Override
        public com.google.gson.JsonElement serialize(LocalDate src, 
                java.lang.reflect.Type typeOfSrc, 
                com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(formatter.format(src));
        }
        
        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, 
                java.lang.reflect.Type typeOfT, 
                com.google.gson.JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}
