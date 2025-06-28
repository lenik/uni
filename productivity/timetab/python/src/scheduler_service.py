import logging
import tempfile
import os
from typing import List, Optional, Dict, Any
from fastapi import HTTPException, status
from .user import User
from .sector import Sector
from .sectors import Sectors
from .time_slot import TimeSlot
from .time_table import TimeTable
from .scheduler import Scheduler
from .sector_orm import SectorORM
from .time_table_orm import TimeTableORM
from .time_slot_orm import TimeSlotORM
from .db import get_session

class SchedulerService:
    @staticmethod
    def allocate_sectors(user: User, timetable_id: str, scheduler_options: Dict[str, Any]) -> Dict[str, Any]:
        """Allocate sectors to timetable using scheduler"""
        session = get_session()
        try:
            # Load timetable
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
            
            # Load sectors for user
            sector_orms = SectorORM.get_by_user(session, int(user.id))
            if not sector_orms:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="No sectors found for user"
                )
            
            sectors = Sectors([Sector.from_db(s_orm) for s_orm in sector_orms], user)
            
            # Create scheduler and allocate
            scheduler = Scheduler(
                shuffle_sectors=scheduler_options.get('shuffle', False),
                large_first=scheduler_options.get('large_first', False),
                maxload=scheduler_options.get('maxload'),
                break_minutes=scheduler_options.get('break_minutes', 0)
            )
            
            updated_time_table = scheduler.allocate_sectors_proportionally(time_table, sectors)
            
            # Save updated timetable back to database
            # Remove existing timeslots
            for ts in timeslot_orms:
                ts.delete(session)
            
            # Save new allocated slots
            for slot in updated_time_table:
                timeslot_orm = slot.to_db(int(timetable_id))
                timeslot_orm.save(session)
            
            return {
                "message": "Successfully allocated sectors to timetable",
                "allocated_slots": len(updated_time_table.get_allocated_slots()),
                "total_available_time": updated_time_table.get_total_available_time()
            }
        finally:
            session.close() 