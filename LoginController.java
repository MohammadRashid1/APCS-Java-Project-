package com.studentmanagement.controllers;

import com.studentmanagement.services.AuthService;
import com.studentmanagement.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        
        // Set default credentials for testing
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        if (!AuthService.login(username, password)) {
            showError("Invalid username or password");
            return;
        }
        
        // Successful login - navigate to dashboard
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Student Management System - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }
}
