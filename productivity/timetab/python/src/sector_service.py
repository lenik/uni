import logging
import tempfile
import os
from typing import List, Optional, Dict, Any
from pathlib import Path
from fastapi import HTTPException, status, UploadFile
from .user import User
from .sector import Sector
from .sectors import Sectors
from .sector_orm import SectorORM
from .db import get_session
from .webmodels import SectorResponse

class SectorService:
    @staticmethod
    def get_user_sectors(user: User) -> List[SectorResponse]:
        """Get all sectors for a user"""
        session = get_session()
        try:
            sector_orms = SectorORM.get_by_user(session, int(user.id))
            sectors = [Sector.from_db(s_orm) for s_orm in sector_orms]
            
            return [
                SectorResponse(
                    id=sector.id,
                    seq=sector.seq,
                    ratio=f"{sector.ratio:.1f}%",
                    weight=sector.weight,
                    abbr=sector.abbr,
                    description=sector.description
                )
                for sector in sectors
            ]
        finally:
            session.close()
    
    @staticmethod
    def create_sector(user: User, sector_data: Dict[str, Any]) -> SectorResponse:
        """Create a new sector for a user"""
        session = get_session()
        try:
            sector = Sector.from_strings(
                seq=sector_data['seq'],
                ratio=sector_data['ratio'],
                weight=sector_data['weight'],
                abbr=sector_data['abbr'],
                description=sector_data['description'],
                user=user
            )
            
            sector_orm = sector.to_db()
            sector_orm.save(session)
            
            return SectorResponse(
                id=str(sector_orm.id),
                seq=sector.seq,
                ratio=f"{sector.ratio:.1f}%",
                weight=sector.weight,
                abbr=sector.abbr,
                description=sector.description
            )
        finally:
            session.close()
    
    @staticmethod
    def import_sectors_from_file(user: User, file: UploadFile) -> Dict[str, Any]:
        """Import sectors from uploaded file"""
        try:
            # Save uploaded file temporarily
            with tempfile.NamedTemporaryFile(delete=False, suffix=Path(file.filename).suffix) as temp_file:
                content = file.file.read()
                temp_file.write(content)
                temp_file_path = temp_file.name
            
            # Import sectors
            sectors = Sectors.from_file(temp_file_path, user)
            
            # Save to database
            session = get_session()
            try:
                # Remove existing sectors for this user
                existing_sectors = SectorORM.get_by_user(session, int(user.id))
                for s in existing_sectors:
                    s.delete(session)
                
                # Save new sectors
                for sector in sectors:
                    sector_orm = sector.to_db()
                    sector_orm.save(session)
                
                logging.info(f"Imported {len(sectors)} sectors for user {user.name}")
                
                return {
                    "message": f"Successfully imported {len(sectors)} sectors",
                    "imported_count": len(sectors),
                    "file_type": Path(file.filename).suffix.lower()
                }
            finally:
                session.close()
                # Clean up temp file
                os.unlink(temp_file_path)
                
        except Exception as e:
            logging.error(f"Error importing sectors: {e}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Failed to import sectors: {str(e)}"
            )
