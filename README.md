# PrepVector Backend 🚀

Backend service for **PrepVector**, an AI-powered study platform that allows users to upload study material and ask questions based on their documents using **Retrieval-Augmented Generation (RAG)**.

Built with **Java, Spring Boot, PostgreSQL, Qdrant, and Google Gemini**.

## 🛠️ Tech Stack

* **Java**
* **Spring Boot**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **Qdrant** — Vector database
* **Google Gemini** — LLM
* **Maven**
* **REST APIs**

## 🏗️ Architecture

```text
                    ┌─────────────────┐
                    │   Frontend      │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Spring Boot    │
                    │     Backend     │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
       ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
       │ PostgreSQL  │ │   Qdrant    │ │   Gemini    │
       │             │ │Vector Store │ │     API     │
       └─────────────┘ └─────────────┘ └─────────────┘
```

## ✨ Features

* 📄 Upload study documents
* 🗃️ Store document metadata in PostgreSQL
* ✂️ Process and chunk documents
* 🔢 Generate vector representations
* 🔎 Semantic similarity search using Qdrant
* 🧠 Retrieve relevant document context
* 🤖 Generate AI-powered answers using Gemini
* 🌐 RESTful APIs
* 📦 Configurable file upload limits

## 🔄 RAG Pipeline

```text
Document Upload
      ↓
Document Processing
      ↓
Text Extraction & Chunking
      ↓
Vector Embeddings
      ↓
Qdrant Vector Database
      ↓
User Question
      ↓
Semantic Search
      ↓
Relevant Document Chunks
      ↓
Gemini
      ↓
Generated Answer
```

The RAG pipeline allows the application to retrieve relevant information from uploaded study material before generating an answer.

## 📋 Prerequisites

Before running the backend, install:

* Java 17+
* Maven
* PostgreSQL
* Qdrant
* Google Gemini API access

## ⚙️ Configuration

Create your local `application.properties` file and configure the required services.

Example configuration:

```properties
spring.application.name=backend

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/<DATABASE_NAME>
spring.datasource.username=<DATABASE_USERNAME>
spring.datasource.password=<DATABASE_PASSWORD>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads/

# Gemini
gemini.api.key=<GEMINI_API_KEY>

# Qdrant
qdrant.host=localhost
qdrant.port=6334
qdrant.collection=prepvector
```



## 🗄️ PostgreSQL Setup

Create a PostgreSQL database for the application:

```sql
CREATE DATABASE prepvector;
```

Then update the database configuration in your local `application.properties`.

## 🔎 Qdrant Setup

PrepVector uses **Qdrant** for storing and searching vector embeddings.

Default local configuration:

```properties
qdrant.host=localhost
qdrant.port=6334
qdrant.collection=prepvector
```

Qdrant can also be run using Docker:

```bash
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
```

## ▶️ Running Locally

Clone the repository:

```bash
git clone https://github.com/nikshay17/PrepVector-Backend.git
```

Navigate to the project:

```bash
cd PrepVector-Backend
```

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

## 📁 File Upload

Uploaded files are stored in the configured upload directory:

```text
uploads/
```

The current maximum file size is:

```text
10 MB
```

## 🧠 Why RAG?

Instead of sending an entire document directly to an LLM, PrepVector:

1. Processes the uploaded document.
2. Splits it into smaller chunks.
3. Converts the chunks into vector representations.
4. Stores them in Qdrant.
5. Searches for the most relevant chunks when a question is asked.
6. Sends the relevant context to Gemini.
7. Generates a context-aware answer.

This improves the relevance of responses while reducing the amount of unnecessary information sent to the LLM.


## 👨‍💻 Author

**Nikshay Kataria**

GitHub: https://github.com/nikshay17

## 📄 License

This project is intended for educational and development purposes.
