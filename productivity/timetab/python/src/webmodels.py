from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime

# Authentication models
class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"

class TokenData(BaseModel):
    username: Optional[str] = None

class UserCreate(BaseModel):
    username: str = Field(..., min_length=3, max_length=30)
    display_name: str = Field(..., min_length=1, max_length=80)
    password: str = Field(..., min_length=6)
    description: Optional[str] = Field(None, max_length=200)

class UserLogin(BaseModel):
    username: str
    password: str

class UserResponse(BaseModel):
    id: str
    name: str
    display_name: str
    description: Optional[str] = None

# Sector models
class SectorCreate(BaseModel):
    seq: str
    ratio: str
    weight: int = Field(..., gt=0)
    abbr: str = Field(..., min_length=1, max_length=10)
    description: str = Field(..., max_length=200)

class SectorResponse(BaseModel):
    id: Optional[str] = None
    seq: str
    ratio: str
    weight: int
    abbr: str
    description: str

class SectorsResponse(BaseModel):
    sectors: List[SectorResponse]
    total_sectors: int
    total_weight: int

# TimeSlot models
class TimeSlotCreate(BaseModel):
    seq: int
    start: str
    duration: int = Field(..., gt=0)
    end: str
    slot_type: str = Field(..., max_length=30)
    description: str = Field(..., max_length=200)

class TimeSlotResponse(BaseModel):
    id: Optional[str] = None
    seq: int
    start: str
    duration: int
    end: str
    slot_type: str
    description: str
    sector: Optional[SectorResponse] = None
    split: Optional[int] = None

class TimeTableResponse(BaseModel):
    slots: List[TimeSlotResponse]
    total_slots: int
    available_slots: int
    allocated_slots: int
    total_available_time: int

# File upload models
class FileUploadResponse(BaseModel):
    message: str
    imported_count: int
    file_type: str

# PDF generation models
class PDFRequest(BaseModel):
    timetable_id: Optional[str] = None
    include_all_slots: bool = False
    title: Optional[str] = None
    subtitle: Optional[str] = None

class PDFResponse(BaseModel):
    message: str
    pdf_url: str
    filename: str

# Service models
class ServiceResponse(BaseModel):
    success: bool
    message: str
    data: Optional[Dict[str, Any]] = None

# Error models
class ErrorResponse(BaseModel):
    detail: str
    error_code: Optional[str] = None 