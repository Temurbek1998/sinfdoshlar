# Sinfdoshlar — Odnoklassniki / VK uslubidagi ijtimoiy tarmoq (Java)

Spring Boot 3 + Spring Security + Thymeleaf + JPA asosidagi MVP.

## Imkoniyatlar

- Ro'yxatdan o'tish va tizimga kirish (parollar BCrypt bilan shifrlanadi, "Meni eslab qol")
- Profil sahifasi: ism, shahar, tug'ilgan sana, "o'zim haqimda", profilni tahrirlash
- Do'stlar: so'rov yuborish, qabul qilish / rad etish, do'stlikdan chiqarish
- Odamlarni ism, login yoki shahar bo'yicha qidirish
- Lenta: o'zingiz va do'stlaringizning postlari, "Klass" (layk), izohlar, o'z postini o'chirish
- "Siz tanishishingiz mumkin" tavsiyalari
- Shaxsiy xabarlar: suhbatlar ro'yxati, o'qilmagan xabarlar soni
- Odnoklassniki uslubidagi dizayn, telefonda pastki menyu bilan

## Ishga tushirish

Kerak: **Java 17+** va **Maven** (yoki IntelliJ IDEA).

```bash
mvn spring-boot:run
```

yoki jar qilib:

```bash
mvn package
java -jar target/sinfdoshlar-0.1.0.jar
```

Brauzerda oching: http://localhost:8080

Birinchi ishga tushirishda demo foydalanuvchilar yaratiladi (parol hammasida `123456`):
`ali`, `dilnoza`, `jasur`, `malika`. O'chirish uchun `application.properties` da `app.demo-data=false` qiling.

Ma'lumotlar `./data/` papkasidagi H2 fayl bazasida saqlanadi. Bazani ko'rish: http://localhost:8080/h2-console
(JDBC URL: `jdbc:h2:file:./data/sinfdoshlar`, user: `sa`, parol bo'sh).

### PostgreSQL bilan

1. `sinfdoshlar` nomli baza yarating.
2. `src/main/resources/application-postgres.properties` da login/parolni to'g'rilang.
3. `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`

## Internetga joylash (Render + Neon)

1. **Neon**: https://neon.tech da bepul loyiha oching. "Connection details" bo'limidan host, baza nomi, user va parolni oling.
2. **Render**: https://render.com da "New → Blueprint" ni tanlab, shu GitHub repoini ulang (`render.yaml` avtomatik o'qiladi).
3. Render so'ragan muhit o'zgaruvchilarini kiriting:
   - `DATABASE_URL` = `jdbc:postgresql://<neon-host>/<baza>?sslmode=require`
   - `DATABASE_USERNAME` = Neon user
   - `DATABASE_PASSWORD` = Neon parol
4. Deploy tugagach, Render bergan `https://sinfdoshlar-xxxx.onrender.com` manzilini oching.

Docker bilan o'zingizda: `docker build -t sinfdoshlar .` va kerakli o'zgaruvchilar bilan `docker run -p 8080:8080 ...`.

## Testlar

```bash
mvn test
```

## Loyiha tuzilmasi

```
src/main/java/uz/sinfdoshlar/
├── config/       Xavfsizlik sozlamalari, demo ma'lumotlar
├── controller/   Sahifalar (lenta, profil, do'stlar, xabarlar, kirish)
├── model/        JPA entity'lar: User, Post, Friendship, Message
├── repository/   Spring Data repository'lar
├── service/      Biznes mantiq
└── web/          Forma DTO'lari va validatsiya
src/main/resources/
├── templates/    Thymeleaf HTML sahifalar
└── static/css/   Dizayn
```

## Keyingi qadamlar (g'oyalar)

- Avatar va rasm yuklash
- Postlarga izohlar
- WebSocket orqali real vaqtdagi chat
- Guruhlar va sinfdoshlarni maktab/yil bo'yicha topish
- Bildirishnomalar

## Admin panel

`/admin` manzilida: statistika, foydalanuvchilarni bloklash va admin qilish, postlar, rasmlar, qo'shiqlar va videolarni o'chirish.

Kim admin bo'lishi `ADMINS` muhit o'zgaruvchisida yoziladi (loginlar vergul bilan, masalan `ADMINS=bek,ali`).
Render'da: servis → **Environment** → `ADMINS` qo'shing → saqlang (sayt qayta ishga tushadi).
Lokal rejimda `ali` avtomatik admin. Admin panel havolasi chap menyuda va avatar menyusida chiqadi.
