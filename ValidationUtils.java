package com.studentmanagement.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }
    
    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^\\+?[0-9\\s-]{10,}$";
        return Pattern.matches(phoneRegex, phone);
    }
    
    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }
    
    public static boolean isValidEnrollmentYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= 2000 && year <= currentYear;
    }
    
    public static boolean isAtLeast16YearsOld(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 16;
    }
    
    public static boolean isValidStudentId(String id) {
        return id != null && !id.trim().isEmpty() && id.length() >= 6;
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2;
    }
}
