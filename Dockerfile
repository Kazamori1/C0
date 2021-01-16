FROM java:11
WORKDIR /app/
COPY ./* ./
RUN javac -cp . Main.java
