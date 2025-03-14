# Java 17 yoki kerakli versiyani ishlatish
FROM openjdk:17-jdk-slim

# Ishchi papkani yaratish
WORKDIR /app

# .jar faylni konteyner ichiga nusxalash
COPY target/*.jar app.jar

# Portni ochish (agar kerak bo‘lsa)
EXPOSE 8080

# JAR faylni ishga tushirish
CMD ["java", "-jar", "app.jar"]