# Authentication API Documentation

This document describes the authentication system implemented for the SI516 Backend project.

## Overview

The authentication system allows doctors to register accounts and login to receive JWT tokens for secure access to the system. Passwords are stored as BCrypt hashes for security.

## Endpoints

### 1. Register Doctor

**POST** `/auth/register`

Registers a new doctor with username and password.

**Request Body:**
```json
{
  "username": "johndoe",
  "fullName": "Dr. John Doe",
  "officeId": "office123",
  "password": "mySecurePassword123"
}
```

**Response (201 Created):**
```json
{
  "id": "doctor123",
  "username": "johndoe",
  "fullName": "Dr. John Doe",
  "officeId": "office123",
  "password": null
}
```

**Note:** Password is never returned in responses for security.

### 2. Login

**POST** `/auth/login`

Authenticates a doctor and returns a JWT token.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "mySecurePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkb2N0b3IxMjMiLCJ1c2VybmFtZSI6ImpvaG5kb2UiLCJmdWxsTmFtZSI6IkRyLiBKb2huIERvZSIsIm9mZmljZUlkIjoib2ZmaWNlMTIzIiwiaWF0IjoxNzA2MDg5MjAwLCJleHAiOjE3MDYwOTI4MDB9.signature",
  "doctorId": "doctor123",
  "username": "johndoe",
  "fullName": "Dr. John Doe",
  "officeId": "office123"
}
```

## JWT Token Structure

The JWT token contains the following claims:
- `sub`: Doctor ID
- `username`: Doctor's username
- `fullName`: Doctor's full name
- `officeId`: Associated office ID
- `iat`: Token issued time
- `exp`: Token expiration time

## Security Features

1. **Password Hashing**: Passwords are hashed using BCrypt before storage
2. **JWT Tokens**: Stateless authentication using signed JWT tokens
3. **No Password Exposure**: Passwords are never returned in API responses
4. **Token Validation**: JWT tokens are signed and validated on each request
5. **Office Association**: Doctors are tied to offices, enabling office-based permissions

## Error Handling

### Registration Errors
- **400 Bad Request**: Username already exists, missing required fields, or invalid office ID

### Login Errors
- **400 Bad Request**: Invalid username or incorrect password

## Configuration

The following properties can be configured in `application.properties`:

```properties
# JWT Secret (should be a long, secure key in production)
app.jwt.secret=myVerySecretKeyForJWTTokenGeneration123456789

# JWT Token expiration time in milliseconds (8 hours = 28800000)
app.jwt.expiration=28800000
```

## Usage Flow

1. **Doctor Registration**: Doctor creates account via frontend form
   - Frontend sends username, fullName, officeId, and password to `/auth/register`
   - Backend hashes password and stores doctor in database
   - Returns doctor info (without password)

2. **Doctor Login**: Doctor logs in via frontend
   - Frontend sends username and password to `/auth/login`
   - Backend validates credentials and generates JWT token
   - Returns token and doctor information

3. **Audio Recording**: Authenticated doctor records audio
   - Frontend includes JWT token in Authorization header
   - Backend validates token and processes audio with doctor context
   - Office information is automatically inferred from doctor's JWT

## Integration with Existing Endpoints

The existing `/doctors` CRUD endpoints have been updated to support password handling:
- When creating a doctor via `/doctors` POST, if a password is provided, it will be hashed and stored
- Password updates via `/doctors/{id}` PUT are supported
- Passwords are never returned in any doctor responses

## Examples

### Example Registration Flow
```bash
# Register a new doctor
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "drgonzalez",
    "fullName": "Dr. Maria González",
    "officeId": "office001",
    "password": "securePassword123"
  }'
```

### Example Login Flow
```bash
# Login with credentials
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "drgonzalez",
    "password": "securePassword123"
  }'
```

### Example Authenticated Request
```bash
# Use the JWT token for authenticated requests
curl -X GET http://localhost:8080/doctors \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

This authentication system provides the foundation for secure doctor management and enables the audio recording workflow described in the requirements.