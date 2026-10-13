package uz.sinfdoshlar.web;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Shablonlarda vaqtni o'zbekcha ko'rsatish: ${@fmt.ago(post.createdAt)}. */
@Component("fmt")
public class TimeFormatter {

    private static final String[] MONTHS = {
            "yan", "fev", "mar", "apr", "may", "iyn", "iyl", "avg", "sen", "okt", "noy", "dek"};
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    public String ago(LocalDateTime time) {
        if (time == null) return "";
        LocalDateTime now = LocalDateTime.now();
        long minutes = Duration.between(time, now).toMinutes();
        if (minutes < 1) return "hozirgina";
        if (minutes < 60) return minutes + " daqiqa oldin";
        if (time.toLocalDate().equals(now.toLocalDate())) return "bugun, " + time.format(HM);
        if (time.toLocalDate().equals(now.toLocalDate().minusDays(1))) return "kecha, " + time.format(HM);
        return date(time.toLocalDate());
    }

    public String lastSeen(uz.sinfdoshlar.model.User user) {
        if (user.isOnline()) return "Hozir saytda";
        if (user.getLastSeenAt() == null) return "Hali saytga kirmagan";
        return "Oxirgi tashrif: " + ago(user.getLastSeenAt());
    }

    /** Yoshga mos qo'shimcha: "26 yosh". */
    public String age(Integer years) {
        return years == null ? "" : years + " yosh";
    }

    /** Suhbatdagi xabar vaqti: bugun bo'lsa faqat soat. */
    public String time(LocalDateTime time) {
        if (time == null) return "";
        if (time.toLocalDate().equals(LocalDate.now())) return time.format(HM);
        return date(time.toLocalDate()) + ", " + time.format(HM);
    }

    public String date(LocalDate date) {
        if (date == null) return "";
        String s = date.getDayOfMonth() + " " + MONTHS[date.getMonthValue() - 1];
        return date.getYear() == LocalDate.now().getYear() ? s : s + " " + date.getYear();
    }
}
