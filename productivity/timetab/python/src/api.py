import logging
import os
from typing import List, Optional
from fastapi import FastAPI, HTTPException, status, Depends, UploadFile, File, Form
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer
from .auth import AuthService
from .sector_service import SectorService
from .time_table_service import TimeTableService
from .scheduler_service import SchedulerService
from .pdf_generator import PDFGenerator
from .webmodels import (
    Token, UserCreate, UserLogin, UserResponse,
    SectorCreate, SectorResponse, SectorsResponse,
    TimeTableResponse, FileUploadResponse, PDFRequest, PDFResponse,
    ServiceResponse
)
from .user import User
from .db import create_tables

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(
    title="TimeTab API",
    description="API for timetable management and sector allocation",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure appropriately for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize database tables
@app.on_event("startup")
async def startup_event():
    create_tables()
    logger.info("Database tables created/verified")

# Authentication endpoints
@app.post("/auth/register", response_model=UserResponse)
async def register_user(user_data: UserCreate):
    """Register a new user"""
    try:
        user = AuthService.create_user(
            username=user_data.username,
            display_name=user_data.display_name,
            password=user_data.password,
            description=user_data.description
        )
        return UserResponse(
            id=user.id,
            name=user.name,
            display_name=user.display_name,
            description=user.description
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error registering user: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to register user"
        )

@app.post("/auth/login", response_model=Token)
async def login_user(user_credentials: UserLogin):
    """Login user and get access token"""
    try:
        user = AuthService.authenticate_user(
            user_credentials.username, 
            user_credentials.password
        )
        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Incorrect username or password",
                headers={"WWW-Authenticate": "Bearer"},
            )
        
        access_token = AuthService.create_access_token(
            data={"sub": user.name}
        )
        return Token(access_token=access_token, token_type="bearer")
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error during login: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Login failed"
        )

@app.get("/auth/me", response_model=UserResponse)
async def get_current_user_info(current_user: User = Depends(AuthService.get_current_user)):
    """Get current user information"""
    return UserResponse(
        id=current_user.id,
        name=current_user.name,
        display_name=current_user.display_name,
        description=current_user.description
    )

# Sector endpoints
@app.get("/sectors", response_model=SectorsResponse)
async def get_user_sectors(current_user: User = Depends(AuthService.get_current_user)):
    """Get all sectors for the current user"""
    try:
        sectors = SectorService.get_user_sectors(current_user)
        total_weight = sum(sector.weight for sector in sectors)
        return SectorsResponse(
            sectors=sectors,
            total_sectors=len(sectors),
            total_weight=total_weight
        )
    except Exception as e:
        logger.error(f"Error getting sectors: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to get sectors"
        )

@app.post("/sectors", response_model=SectorResponse)
async def create_sector(
    sector_data: SectorCreate,
    current_user: User = Depends(AuthService.get_current_user)
):
    """Create a new sector for the current user"""
    try:
        sector = SectorService.create_sector(current_user, sector_data.dict())
        return sector
    except Exception as e:
        logger.error(f"Error creating sector: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to create sector"
        )

@app.post("/sectors/import", response_model=FileUploadResponse)
async def import_sectors(
    file: UploadFile = File(...),
    current_user: User = Depends(AuthService.get_current_user)
):
    """Import sectors from file (CSV, ODS, XLSX, JSON)"""
    try:
        # Validate file type
        allowed_extensions = {'.csv', '.ods', '.xlsx', '.json'}
        file_extension = os.path.splitext(file.filename)[1].lower()
        
        if file_extension not in allowed_extensions:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Unsupported file type. Allowed: {', '.join(allowed_extensions)}"
            )
        
        result = SectorService.import_sectors_from_file(current_user, file)
        return FileUploadResponse(**result)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error importing sectors: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to import sectors"
        )

# Timetable endpoints
@app.get("/timetables")
async def get_user_timetables(current_user: User = Depends(AuthService.get_current_user)):
    """Get all timetables for the current user"""
    try:
        timetables = TimeTableService.get_user_timetables(current_user)
        return {"timetables": timetables}
    except Exception as e:
        logger.error(f"Error getting timetables: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to get timetables"
        )

@app.get("/timetables/{timetable_id}/slots", response_model=TimeTableResponse)
async def get_timetable_slots(
    timetable_id: str,
    current_user: User = Depends(AuthService.get_current_user)
):
    """Get all slots for a specific timetable"""
    try:
        return TimeTableService.get_timetable_slots(current_user, timetable_id)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error getting timetable slots: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to get timetable slots"
        )

@app.post("/timetables/import", response_model=FileUploadResponse)
async def import_timetable(
    file: UploadFile = File(...),
    current_user: User = Depends(AuthService.get_current_user)
):
    """Import timetable from file (CSV, ODS, XLSX, JSON)"""
    try:
        # Validate file type
        allowed_extensions = {'.csv', '.ods', '.xlsx', '.json'}
        file_extension = os.path.splitext(file.filename)[1].lower()
        
        if file_extension not in allowed_extensions:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Unsupported file type. Allowed: {', '.join(allowed_extensions)}"
            )
        
        result = TimeTableService.import_timetable_from_file(current_user, file)
        return FileUploadResponse(**result)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error importing timetable: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to import timetable"
        )

@app.get("/timetables/{timetable_id}/export")
async def export_timetable(
    timetable_id: str,
    format: str = "csv",
    current_user: User = Depends(AuthService.get_current_user)
):
    """Export timetable to specified format"""
    try:
        result = TimeTableService.export_timetable(current_user, timetable_id, format)
        
        # Return file as download
        return FileResponse(
            path=result["file_path"],
            filename=f"timetable_{timetable_id}.{format}",
            media_type="application/octet-stream"
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error exporting timetable: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to export timetable"
        )

# Scheduler endpoints
@app.post("/timetables/{timetable_id}/allocate", response_model=ServiceResponse)
async def allocate_sectors_to_timetable(
    timetable_id: str,
    shuffle: bool = Form(False),
    large_first: bool = Form(False),
    maxload: Optional[int] = Form(None),
    break_minutes: int = Form(0),
    current_user: User = Depends(AuthService.get_current_user)
):
    """Allocate sectors to timetable using scheduler"""
    try:
        scheduler_options = {
            'shuffle': shuffle,
            'large_first': large_first,
            'maxload': maxload,
            'break_minutes': break_minutes
        }
        
        result = SchedulerService.allocate_sectors(current_user, timetable_id, scheduler_options)
        return ServiceResponse(
            success=True,
            message=result["message"],
            data=result
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error allocating sectors: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to allocate sectors"
        )

# PDF generation endpoints
@app.post("/pdf/timetable", response_model=PDFResponse)
async def generate_timetable_pdf(
    request: PDFRequest,
    current_user: User = Depends(AuthService.get_current_user)
):
    """Generate PDF for timetable"""
    try:
        if not request.timetable_id:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Timetable ID is required"
            )
        
        pdf_generator = PDFGenerator()
        pdf_path = pdf_generator.generate_timetable_pdf(
            user=current_user,
            timetable_id=request.timetable_id,
            title=request.title,
            subtitle=request.subtitle,
            include_all_slots=request.include_all_slots
        )
        
        filename = f"timetable_{request.timetable_id}_{current_user.name}.pdf"
        
        return PDFResponse(
            message="PDF generated successfully",
            pdf_url=f"/pdf/download/{filename}",
            filename=filename
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error generating timetable PDF: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to generate PDF"
        )

@app.post("/pdf/sectors", response_model=PDFResponse)
async def generate_sectors_pdf(
    title: Optional[str] = Form(None),
    current_user: User = Depends(AuthService.get_current_user)
):
    """Generate PDF for sectors"""
    try:
        pdf_generator = PDFGenerator()
        pdf_path = pdf_generator.generate_sectors_pdf(
            user=current_user,
            title=title
        )
        
        filename = f"sectors_{current_user.name}.pdf"
        
        return PDFResponse(
            message="PDF generated successfully",
            pdf_url=f"/pdf/download/{filename}",
            filename=filename
        )
    except Exception as e:
        logger.error(f"Error generating sectors PDF: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to generate PDF"
        )

@app.get("/pdf/download/{filename}")
async def download_pdf(filename: str):
    """Download generated PDF file"""
    # This is a simplified implementation
    # In production, you'd want to implement proper file storage and retrieval
    try:
        # For now, we'll return a placeholder
        # In a real implementation, you'd retrieve the file from storage
        raise HTTPException(
            status_code=status.HTTP_501_NOT_IMPLEMENTED,
            detail="PDF download not yet implemented"
        )
    except Exception as e:
        logger.error(f"Error downloading PDF: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to download PDF"
        )

# Health check endpoint
@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": "timetab-api"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000) 