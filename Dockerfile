FROM java:8
WORKDIR /app/
COPY ./* ./
RUN javac DataPrinter.java