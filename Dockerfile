FROM java:8
WORKDIR /app/
COPY ./* ./
RUN javac -cp . Main.java
