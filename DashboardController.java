package com.studentmanagement.controllers;

import com.studentmanagement.models.Student;
import com.studentmanagement.services.AuthService;
import com.studentmanagement.services.JsonDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label avgGpaLabel;
    @FXML private Label recentEnrollmentsLabel;
    @FXML private BarChart<String, Number> departmentChart;
    @FXML private Button manageStudentsButton;
    @FXML private Button logoutButton;
    
    @FXML
    private void initialize() {
        // Set welcome message
        String username = AuthService.getCurrentUser().getUsername();
        String role = AuthService.getCurrentUser().getRole();
        welcomeLabel.setText("Welcome, " + username + " (" + role + ")");
        
        // Load statistics
        loadStatistics();
        
        // Setup chart
        setupDepartmentChart();
        
        // Disable manage button for non-admin users
        if (!AuthService.isAdmin()) {
            manageStudentsButton.setDisable(true);
            manageStudentsButton.setText("Manage Students (Admin Only)");
        }
    }
    
    private void loadStatistics() {
        List<Student> students = JsonDataService.loadStudents();
        
        // Total students
        totalStudentsLabel.setText(String.valueOf(students.size()));
        
        // Average GPA
        if (!students.isEmpty()) {
            double totalGpa = students.stream().mapToDouble(Student::getGpa).sum();
            double avgGpa = totalGpa / students.size();
            avgGpaLabel.setText(String.format("%.2f", avgGpa));
        } else {
            avgGpaLabel.setText("0.00");
        }
        
        // Recent enrollments (last 2 years)
        int currentYear = java.time.LocalDate.now().getYear();
        long recent = students.stream()
                .filter(s -> s.getEnrollmentYear() >= currentYear - 2)
                .count();
        recentEnrollmentsLabel.setText(String.valueOf(recent));
    }
    
    private void setupDepartmentChart() {
        List<Student> students = JsonDataService.loadStudents();
        
        // Clear existing data
        departmentChart.getData().clear();
        
        // Create series for chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Students by Department");
        
        // Count students per department
        java.util.Map<String, Long> departmentCount = students.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Student::getDepartment, 
                        java.util.stream.Collectors.counting()
                ));
        
        // Add data to series
        departmentCount.forEach((dept, count) -> {
            series.getData().add(new XYChart.Data<>(dept, count));
        });
        
        departmentChart.getData().add(series);
        
        // Customize chart appearance
        departmentChart.setTitle("Student Distribution by Department");
        departmentChart.setLegendVisible(false);
    }
    
    @FXML
    private void handleManageStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/student_management.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) manageStudentsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("Student Management");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        AuthService.logout();
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.setTitle("Student Management System - Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
