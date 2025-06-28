# TimeTab API Server

A FastAPI-based ASGI server for timetable management and sector allocation with user authentication, data import/export, and PDF generation.

## Features

- **User Authentication**: JWT-based login/register system
- **Sector Management**: Create, list, and import sectors for users
- **Timetable Management**: Import/export timetables, list slots
- **Sector Allocation**: Allocate sectors to timetables using the scheduler
- **PDF Generation**: Generate beautiful PDFs for timetables and sectors
- **Data Import/Export**: Support for CSV, ODS, XLSX, and JSON formats

## Setup

### 1. Navigate to Python Directory

```bash
cd python
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

### 3. Environment Configuration

Create a `.env` file in the `python/` directory with the following variables:

```env
# Database Configuration
DATABASE_URL=postgresql://username:password@localhost:5432/timetab

# Security
SECRET_KEY=your-secret-key-change-in-production

# Server Configuration
HOST=0.0.0.0
PORT=8000
RELOAD=true

# Logging
LOG_LEVEL=INFO
```

### 4. Database Setup

The application will automatically create database tables on startup. Make sure your PostgreSQL database is running and accessible.

### 5. Run the Server

```bash
# Using the startup script (from python/ directory)
python run_server.py

# Or directly with uvicorn (from python/ directory)
uvicorn src.api:app --reload --host 0.0.0.0 --port 8000
```

## API Endpoints

### Authentication
- `POST /auth/register` - Register a new user
- `POST /auth/login` - Login and get access token
- `GET /auth/me` - Get current user information

### Sectors
- `GET /sectors` - List all sectors for the current user
- `POST /sectors` - Create a new sector
- `POST /sectors/import` - Import sectors from file

### Timetables
- `GET /timetables` - List all timetables for the current user
- `GET /timetables/{timetable_id}/slots` - Get slots for a specific timetable
- `POST /timetables/import` - Import timetable from file
- `GET /timetables/{timetable_id}/export` - Export timetable to file

### Scheduler
- `POST /timetables/{timetable_id}/allocate` - Allocate sectors to timetable

### PDF Generation
- `POST /pdf/timetable` - Generate PDF for timetable
- `POST /pdf/sectors` - Generate PDF for sectors

### Health Check
- `GET /health` - Health check endpoint

## API Documentation

Once the server is running, you can access:
- **Interactive API Docs**: http://localhost:8000/docs
- **ReDoc Documentation**: http://localhost:8000/redoc

## Usage Examples

### 1. Register a User

```bash
curl -X POST "http://localhost:8000/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "display_name": "John Doe",
    "password": "securepassword123",
    "description": "Test user"
  }'
```

### 2. Login and Get Token

```bash
curl -X POST "http://localhost:8000/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securepassword123"
  }'
```

### 3. Import Sectors (with authentication)

```bash
curl -X POST "http://localhost:8000/sectors/import" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@sectors.csv"
```

### 4. Import Timetable

```bash
curl -X POST "http://localhost:8000/timetables/import" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@timetable.csv"
```

### 5. Allocate Sectors to Timetable

```bash
curl -X POST "http://localhost:8000/timetables/1/allocate" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "shuffle=true" \
  -F "large_first=false" \
  -F "maxload=120" \
  -F "break_minutes=15"
```

### 6. Generate Timetable PDF

```bash
curl -X POST "http://localhost:8000/pdf/timetable" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "timetable_id": "1",
    "include_all_slots": false,
    "title": "My Weekly Schedule",
    "subtitle": "Generated on 2024-01-15"
  }'
```

## File Formats

The API supports the following file formats for import/export:

- **CSV**: Comma-separated values
- **ODS**: LibreOffice Open Document Spreadsheet
- **XLSX**: Microsoft Excel
- **JSON**: JavaScript Object Notation

## Security Notes

- Change the `SECRET_KEY` in production
- Configure CORS appropriately for your domain
- Use HTTPS in production
- Implement proper file storage for PDF downloads
- Add rate limiting for production use

## Development

The codebase is organized as follows:

```
python/
├── src/
│   ├── api.py              # Main FastAPI application
│   ├── auth.py             # Authentication service
│   ├── sector_service.py   # Sector management service
│   ├── time_table_service.py # Timetable management service
│   ├── scheduler_service.py # Scheduler service
│   ├── pdf_generator.py    # PDF generation service
│   ├── webmodels.py        # Pydantic models for API
│   └── *.py               # Core business logic and database models
├── test/                   # Test files
├── requirements.txt        # Python dependencies
├── run_server.py          # Server startup script
└── README_API.md          # This file
```

## Running from Different Directories

### From Project Root:
```bash
cd python
python run_server.py
```

### From Python Directory:
```bash
python run_server.py
```

### Using Uvicorn Directly:
```bash
cd python
uvicorn src.api:app --reload --host 0.0.0.0 --port 8000
``` 