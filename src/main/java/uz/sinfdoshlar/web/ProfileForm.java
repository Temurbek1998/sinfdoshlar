package uz.sinfdoshlar.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import uz.sinfdoshlar.model.User;

import java.time.LocalDate;

public class ProfileForm {

    @NotBlank(message = "Ism-familiyani kiriting")
    @Size(max = 100)
    private String fullName;

    @Size(max = 100)
    private String city;

    @Past(message = "Tug'ilgan sana o'tgan sana bo'lishi kerak")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @Size(max = 1000, message = "Ko'pi bilan 1000 belgi")
    private String about;

    public static ProfileForm of(User user) {
        ProfileForm form = new ProfileForm();
        form.fullName = user.getFullName();
        form.city = user.getCity();
        form.birthDate = user.getBirthDate();
        form.about = user.getAbout();
        return form;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
}
