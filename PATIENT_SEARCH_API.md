# Patient Search by Name API

## New Endpoint Added

### GET /patients/by-name/{name}

Retrieves a patient by their name.

**Parameters:**
- `name` (path parameter): The name of the patient to search for

**Example Request:**
```
GET /patients/by-name/John%20Doe
```

**Example Response:**
```json
{
  "id": "patient123",
  "name": "John Doe",
  "dob": "1990-01-01", 
  "email": "john.doe@example.com",
  "phone": "12345678"
}
```

**Error Response (Patient Not Found):**
- Status: 500 Internal Server Error
- Body: Runtime error message indicating patient not found

**Implementation Details:**
- Added `findByName(String name)` method to `PatientRepository`
- Added `getByName(String name)` method to `PatientService` interface
- Implemented `getByName(String name)` in `PatientServiceImpl` 
- Added GET `/patients/by-name/{name}` endpoint in `PatientController`
- Follows the same patterns as existing CRUD operations
- Uses exact name matching (case-sensitive)

## Response Structure

The endpoint returns the same PatientDTO structure as other patient endpoints:
- `id`: MongoDB document ID (maps to `_id` in database)
- `name`: Patient's full name
- `dob`: Date of birth (YYYY-MM-DD format)
- `email`: Patient's email address
- `phone`: Patient's phone number (8 digits)

Note: The MongoDB document will also contain the `_class` field for polymorphic type handling by Spring Data MongoDB, but this is filtered out in the DTO response to maintain clean API contracts.