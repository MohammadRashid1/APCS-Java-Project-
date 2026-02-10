package com.studentmanagement.controllers;

import com.studentmanagement.models.Student;
import com.studentmanagement.services.JsonDataService;
import com.studentmanagement.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentController {
    
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> departmentColumn;
    @FXML private TableColumn<Student, Double> gpaColumn;
    
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> departmentCombo;
    @FXML private TextField gpaField;
    @FXML private Spinner<Integer> yearSpinner;
    @FXML private TextField searchField;
    
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;
    
    private ObservableList<Student> studentList;
    private List<Student> originalList;
    
    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        
        // Setup department combo box
        departmentCombo.getItems().addAll(
            "Computer Science", 
            "Mathematics", 
            "Physics", 
            "Chemistry", 
            "Biology", 
            "Engineering", 
            "Business", 
            "Economics"
        );
        
        // Setup year spinner
        SpinnerValueFactory<Integer> yearFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2000, 2024, 2023);
        yearSpinner.setValueFactory(yearFactory);
        
        // Load data
        loadStudents();
        
        // Setup table selection listener
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithStudent(newSelection);
                    updateButton.setDisable(false);
                    deleteButton.setDisable(false);
                    addButton.setDisable(true);
                }
            });
        
        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }
    
    private void loadStudents() {
        originalList = JsonDataService.loadStudents();
        studentList = FXCollections.observableArrayList(originalList);
        studentTable.setItems(studentList);
    }
    
    private void fillFormWithStudent(Student student) {
        studentIdField.setText(student.getStudentId());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        dobPicker.setValue(student.getDateOfBirth());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        departmentCombo.setValue(student.getDepartment());
        gpaField.setText(String.valueOf(student.getGpa()));
        yearSpinner.getValueFactory().setValue(student.getEnrollmentYear());
    }
    
    @FXML
    private void handleAddStudent() {
        if (!validateForm()) {
            return;
        }
        
        Student student = createStudentFromForm();
        
        // Check if ID already exists
        if (studentList.stream().anyMatch(s -> s.getStudentId().equals(student.getStudentId()))) {
            showAlert("Error", "Student ID already exists!");
            return;
        }
        
        studentList.add(student);
        saveData();
        clearForm();
        showAlert("Success", "Student added successfully!");
    }
    
    @FXML
    private void handleUpdateStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        
        if (selectedStudent == null) {
            showAlert("Error", "Please select a student to update!");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        // Confirm update
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Update");
        alert.setHeaderText("Update Student Record");
        alert.setContentText("Are you sure you want to update this student's information?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update student
            Student updatedStudent = createStudentFromForm();
            
            // Preserve ID if it hasn't changed
            if (!selectedStudent.getStudentId().equals(updatedStudent.getStudentId())) {
                // Check if new ID already exists
                if (studentList.stream().anyMatch(s -> s.getStudentId().equals(updatedStudent.getStudentId()))) {
                    showAlert("Error", "New Student ID already exists!");
                    return;
                }
            }
            
            int index = studentList.indexOf(selectedStudent);
            studentList.set(index, updatedStudent);
            saveData();
            clearForm();
            
            // Refresh table
            studentTable.refresh();
            
            showAlert("Success", "Student updated successfully!");
        }
    }
    
    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        
        if (selectedStudent == null) {
            showAlert("Error", "Please select a student to delete!");
            return;
        }
        
        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Student Record");
        alert.setContentText("Are you sure you want to delete " + 
                            selectedStudent.getFullName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            studentList.remove(selectedStudent);
            saveData();
            clearForm();
            showAlert("Success", "Student deleted successfully!");
            
            // Reset buttons
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
            addButton.setDisable(false);
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
        studentTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            studentList.setAll(originalList);
            return;
        }
        
        List<Student> filtered = originalList.stream()
            .filter(s -> s.getStudentId().toLowerCase().contains(searchText) ||
                        s.getFirstName().toLowerCase().contains(searchText) ||
                        s.getLastName().toLowerCase().contains(searchText) ||
                        s.getEmail().toLowerCase().contains(searchText) ||
                        s.getDepartment().toLowerCase().contains(searchText))
            .toList();
        
        studentList.setAll(filtered);
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/views/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 1000, 600));
            stage.setTitle("Student Management System - Dashboard");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        // Validate Student ID
        if (!ValidationUtils.isValidStudentId(studentIdField.getText())) {
            showAlert("Validation Error", "Please enter a valid Student ID (at least 6 characters)");
            return false;
        }
        
        // Validate Names
        if (!ValidationUtils.isValidName(firstNameField.getText())) {
            showAlert("Validation Error", "Please enter a valid First Name");
            return false;
        }
        
        if (!ValidationUtils.isValidName(lastNameField.getText())) {
            showAlert("Validation Error", "Please enter a valid Last Name");
            return false;
        }
        
        // Validate Date of Birth
        if (dobPicker.getValue() == null) {
            showAlert("Validation Error", "Please select Date of Birth");
            return false;
        }
        
        if (!ValidationUtils.isAtLeast16YearsOld(dobPicker.getValue())) {
            showAlert("Validation Error", "Student must be at least 16 years old");
            return false;
        }
        
        // Validate Email
        if (!ValidationUtils.isValidEmail(emailField.getText())) {
            showAlert("Validation Error", "Please enter a valid Email address");
            return false;
        }
        
        // Validate Phone
        if (!ValidationUtils.isValidPhone(phoneField.getText())) {
            showAlert("Validation Error", "Please enter a valid Phone number");
            return false;
        }
        
        // Validate Department
        if (departmentCombo.getValue() == null) {
            showAlert("Validation Error", "Please select a Department");
            return false;
        }
        
        // Validate GPA
        try {
            double gpa = Double.parseDouble(gpaField.getText());
            if (!ValidationUtils.isValidGPA(gpa)) {
                showAlert("Validation Error", "GPA must be between 0.0 and 4.0");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid GPA");
            return false;
        }
        
        return true;
    }
    
    private Student createStudentFromForm() {
        return new Student(
            studentIdField.getText().trim(),
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            dobPicker.getValue(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            departmentCombo.getValue(),
            Double.parseDouble(gpaField.getText()),
            yearSpinner.getValue()
        );
    }
    
    private void clearForm() {
        studentIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        emailField.clear();
        phoneField.clear();
        departmentCombo.setValue(null);
        gpaField.clear();
        yearSpinner.getValueFactory().setValue(2023);
        searchField.clear();
    }
    
    private void saveData() {
        JsonDataService.saveStudents(new ArrayList<>(studentList));
        originalList = new ArrayList<>(studentList);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
