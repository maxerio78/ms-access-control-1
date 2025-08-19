# ใช้ Amazon Corretto 8 JRE แบบเบา (Alpine)
FROM amazoncorretto:8-alpine-jre

# ตั้ง working directory
WORKDIR /usr/src/app

# คัดลอกไฟล์ JAR ที่ build แล้วจาก target/
COPY target/*.jar app.jar

# ตั้งค่า JVM options (ปรับ RAM ได้ตามจริง)
ENV JAVA_OPTS="-Xms512m -Xmx2048m"

# เปิดพอร์ต Spring Boot (ปกติ 8080)
EXPOSE 8080

# คำสั่งรันหลัก
CMD ["/usr/bin/java", "-jar", "app.jar"]

