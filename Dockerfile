FROM maven:3.9-eclipse-temurin-21

# Install Python 3 and pip
RUN apt-get update && apt-get install -y python3 python3-pip && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install Python Gemini SDK
COPY requirements.txt .
RUN pip3 install --no-cache-dir --break-system-packages -r requirements.txt

# Pre-warm Maven cache using the starter pom.xml
COPY workspace/pom.xml /app/workspace/pom.xml
WORKDIR /app/workspace
RUN mvn dependency:go-offline -B

# Copy the rest of the project
WORKDIR /app
COPY . .

CMD ["python3", "agent/main.py"]