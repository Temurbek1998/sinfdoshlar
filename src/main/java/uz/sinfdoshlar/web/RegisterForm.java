package uz.sinfdoshlar.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RegisterForm {

    @NotBlank(message = "Login kiriting")
    @Size(min = 3, max = 30, message = "Login 3–30 belgidan iborat bo'lsin")
    @Pattern(regexp = "[A-Za-z0-9_.]+", message = "Faqat lotin harflari, raqamlar, _ va .")
    private String username;

    @NotBlank(message = "Ism-familiyani kiriting")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Parol kiriting")
    @Size(min = 6, max = 100, message = "Parol kamida 6 belgi bo'lsin")
    private String password;

    private String confirmPassword;

    @Size(max = 100)
    private String city;

    @Past(message = "Tug'ilgan sana o'tgan sana bo'lishi kerak")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
