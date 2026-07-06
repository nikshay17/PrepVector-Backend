# PrepVector Backend

Backend API for **PrepVector**, an AI-powered learning platform that enables users to upload study materials, generate embeddings, and interact with documents through Retrieval-Augmented Generation (RAG).

The backend handles authentication, PDF management, document processing, vector retrieval, and communication with the AI service.

---

## Features

- User Authentication using JWT
- Secure password hashing with bcrypt
- Upload and manage PDF documents
- Cloudinary integration for file storage
- Retrieval-Augmented Generation (RAG)
- Semantic search over uploaded documents
- AI-powered question answering
- User-specific document isolation
- RESTful API architecture
- MongoDB database integration
- Email OTP verification

---

## Tech Stack

### Backend

- Node.js
- Express.js
- MongoDB
- Mongoose

### Authentication

- JWT
- bcrypt

### AI & RAG

- Google Gemini API
- Python Embedding Service
- FAISS Vector Store

### File Handling

- Multer
- Cloudinary

### Email

- Brevo SMTP

---

# Project Structure

```
PrepVector-Backend
│
├── controllers/
├── middleware/
├── models/
├── routes/
├── utils/
├── config/
├── uploads/
├── app.js
├── server.js
├── package.json
└── .env
```

---

# Getting Started

## Clone Repository

```bash
git clone https://github.com/nikshay17/PrepVector-Backend.git

cd PrepVector-Backend
```

---

## Install Dependencies

```bash
npm install
```

---

## Environment Variables

Create a `.env` file.

```env
PORT=5000

MONGO_URI=your_mongodb_connection_string

JWT_SECRET=your_jwt_secret

BREVO_API_KEY=your_brevo_api_key

EMAIL_USER=your_email
EMAIL_PASS=your_password

CLOUDINARY_CLOUD_NAME=xxxx
CLOUDINARY_API_KEY=xxxx
CLOUDINARY_API_SECRET=xxxx

GEMINI_API_KEY=your_gemini_api_key

PYTHON_API=http://localhost:8000
```

---

## Run Development Server

```bash
npm run dev
```

or

```bash
npm start
```

Backend runs on

```
http://localhost:5000
```

---

# API Overview

## Authentication

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/send-otp` | Send OTP |
| POST | `/api/auth/verify-otp` | Verify OTP |

---

## User

| Method | Endpoint |
|---------|----------|
| GET | `/api/user/profile` |

---

## PDF

| Method | Endpoint |
|---------|----------|
| POST | `/api/pdf/upload` |
| GET | `/api/pdf` |
| DELETE | `/api/pdf/:id` |

---

## AI

| Method | Endpoint |
|---------|----------|
| POST | `/api/chat/ask` |

---

# Authentication

Protected routes require:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# Response Format

Success

```json
{
    "success": true,
    "message": "Operation Successful",
    "data": {}
}
```

Error

```json
{
    "success": false,
    "message": "Something went wrong"
}
```

---

# Running with Frontend

Clone frontend

```bash
git clone https://github.com/nikshay17/PrepVector-Frontend.git
```

Run backend

```bash
npm run dev
```

Run frontend

```bash
npm start
```

---

# Future Improvements

- Chat history
- Multiple document collections
- Notes generation
- Quiz generation
- Flashcards
- PDF summarization
- Collaborative workspaces
- Admin dashboard

---

# Contributors

- Nikshay Kataria
- Team PrepVector
