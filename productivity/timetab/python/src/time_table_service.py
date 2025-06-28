import logging
import tempfile
import os
from typing import List, Optional, Dict, Any
from pathlib import Path
from fastapi import HTTPException, status, UploadFile
from .user import User
from .time_slot import TimeSlot
from .time_table import TimeTable
from .time_table_orm import TimeTableORM
from .time_slot_orm import TimeSlotORM
from .db import get_session
from .webmodels import SectorResponse, TimeSlotResponse, TimeTableResponse

class TimeTableService:
    @staticmethod
    def get_user_timetables(user: User) -> List[Dict[str, Any]]:
        """Get all timetables for a user"""
        session = get_session()
        try:
            timetable_orms = TimeTableORM.get_by_user(session, int(user.id))
            timetables = []
            
            for tt_orm in timetable_orms:
                time_table = TimeTable.from_db(tt_orm)
                
                # Load timeslots for this timetable
                timeslot_orms = TimeSlotORM.get_by_parent(session, tt_orm.id)
                for ts_orm in timeslot_orms:
                    timeslot = TimeSlot.from_db(ts_orm)
                    time_table.add_slot(timeslot)
                
                timetables.append({
                    "id": str(tt_orm.id),
                    "label": time_table.label,
                    "description": time_table.description,
                    "total_slots": len(time_table),
                    "available_slots": time_table.count_available_slots(),
                    "allocated_slots": time_table.count_allocated_slots(),
                    "total_available_time": time_table.get_total_available_time()
                })
            
            return timetables
        finally:
            session.close()
    
    @staticmethod
    def get_timetable_slots(user: User, timetable_id: str) -> TimeTableResponse:
        """Get all slots for a specific timetable"""
        session = get_session()
        try:
            timetable_orm = TimeTableORM.get_by_id(session, int(timetable_id))
            if not timetable_orm:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Timetable not found"
                )
            
            # Verify ownership
            if timetable_orm.uid != int(user.id):
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Access denied"
                )
            
            time_table = TimeTable.from_db(timetable_orm)
            
            # Load timeslots for this timetable
            timeslot_orms = TimeSlotORM.get_by_parent(session, int(timetable_id))
            for ts_orm in timeslot_orms:
                timeslot = TimeSlot.from_db(ts_orm)
                time_table.add_slot(timeslot)
            
            # Convert to response format
            slots = []
            for slot in time_table.slots:
                sector_response = None
                if slot.sector:
                    sector_response = SectorResponse(
                        id=slot.sector.id,
                        seq=slot.sector.seq,
                        ratio=f"{slot.sector.ratio:.1f}%",
                        weight=slot.sector.weight,
                        abbr=slot.sector.abbr,
                        description=slot.sector.description
                    )
                
                slots.append(TimeSlotResponse(
                    id=slot.id,
                    seq=slot.seq,
                    start=slot.start.to_string(),
                    duration=slot.duration,
                    end=slot.end.to_string(),
                    slot_type=slot.slot_type,
                    description=slot.description,
                    sector=sector_response,
                    split=slot.split
                ))
            
            return TimeTableResponse(
                slots=slots,
                total_slots=len(time_table),
                available_slots=time_table.count_available_slots(),
                allocated_slots=time_table.count_allocated_slots(),
                total_available_time=time_table.get_total_available_time()
            )
        finally:
            session.close()
    
    @staticmethod
    def import_timetable_from_file(user: User, file: UploadFile) -> Dict[str, Any]:
        """Import timetable from uploaded file"""
        try:
            # Save uploaded file temporarily
            with tempfile.NamedTemporaryFile(delete=False, suffix=Path(file.filename).suffix) as temp_file:
                content = file.file.read()
                temp_file.write(content)
                temp_file_path = temp_file.name
            
            # Import timetable
            time_table = TimeTable.from_file(temp_file_path, user)
            
            # Save to database
            session = get_session()
            try:
                # Remove existing timetables for this user
                existing_timetables = TimeTableORM.get_by_user(session, int(user.id))
                for tt in existing_timetables:
                    # Remove associated timeslots first
                    timeslots = TimeSlotORM.get_by_parent(session, tt.id)
                    for ts in timeslots:
                        ts.delete(session)
                    tt.delete(session)
                
                # Save new timetable
                timetable_orm = time_table.to_db(int(user.id))
                timetable_orm.save(session)
                
                # Save timeslots
                for slot in time_table:
                    timeslot_orm = slot.to_db(timetable_orm.id)
                    timeslot_orm.save(session)
                
                logging.info(f"Imported timetable with {len(time_table)} slots for user {user.name}")
                
                return {
                    "message": f"Successfully imported timetable with {len(time_table)} slots",
                    "imported_count": len(time_table),
                    "file_type": Path(file.filename).suffix.lower(),
                    "timetable_id": str(timetable_orm.id)
                }
            finally:
                session.close()
                # Clean up temp file
                os.unlink(temp_file_path)
                
        except Exception as e:
            logging.error(f"Error importing timetable: {e}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Failed to import timetable: {str(e)}"
            )
    
    @staticmethod
    def export_timetable(user: User, timetable_id: str, format: str = "csv") -> Dict[str, Any]:
        """Export timetable to specified format"""
        session = get_session()
        try:
            timetable_orm = TimeTableORM.get_by_id(session, int(timetable_id))
            if not timetable_orm:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Timetable not found"
                )
            
            # Verify ownership
            if timetable_orm.uid != int(user.id):
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Access denied"
                )
            
            time_table = TimeTable.from_db(timetable_orm)
            
            # Load timeslots for this timetable
            timeslot_orms = TimeSlotORM.get_by_parent(session, int(timetable_id))
            for ts_orm in timeslot_orms:
                timeslot = TimeSlot.from_db(ts_orm)
                time_table.add_slot(timeslot)
            
            # Create export file
            with tempfile.NamedTemporaryFile(delete=False, suffix=f".{format}") as temp_file:
                if format.lower() == "csv":
                    time_table.to_csv(temp_file.name, all_slots=True)
                elif format.lower() == "json":
                    import json
                    with open(temp_file.name, 'w') as f:
                        json.dump(time_table.to_dict(), f, indent=2)
                else:
                    raise HTTPException(
                        status_code=status.HTTP_400_BAD_REQUEST,
                        detail=f"Unsupported export format: {format}"
                    )
                
                return {
                    "message": f"Successfully exported timetable",
                    "file_path": temp_file.name,
                    "format": format,
                    "slot_count": len(time_table)
                }
        finally:
            session.close()
