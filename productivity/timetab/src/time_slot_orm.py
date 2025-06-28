from sqlalchemy import Column, Integer, String, BigInteger, Time, ForeignKey, Text
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from typing import List, Optional, Dict, Any
from .db import Base, SessionLocal
from .time_utils import Time as TimeUtil
import logging

class TimeSlotORM(Base):
    __tablename__ = 'timeslot'
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    parent = Column(BigInteger, ForeignKey('timetab.id'), nullable=False)
    seq = Column(Integer, nullable=False)
    start = Column(Time, nullable=False)
    duration = Column(Integer, nullable=False)
    end = Column(Time, nullable=False)
    type = Column(String(30), nullable=False)
    description = Column(String(200), nullable=False)
    sector = Column(BigInteger, ForeignKey('sector.id'))
    
    # Relationships
    parent_table = relationship("TimeTableORM", back_populates="timeslots")
    sector_rel = relationship("SectorORM", back_populates="timeslots")
    
    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'parent': self.parent,
            'seq': self.seq,
            'start': self.start.strftime('%H:%M') if self.start else None,
            'duration': self.duration,
            'end': self.end.strftime('%H:%M') if self.end else None,
            'type': self.type,
            'description': self.description,
            'sector': self.sector
        }
    
    @classmethod
    def from_dict(cls, data: dict) -> 'TimeSlotORM':
        start_time = None
        end_time = None
        if data.get('start'):
            start_time = TimeUtil.from_string(data['start']).to_time()
        if data.get('end'):
            end_time = TimeUtil.from_string(data['end']).to_time()
            
        return cls(
            id=data.get('id'),
            parent=data['parent'],
            seq=data['seq'],
            start=start_time,
            duration=data['duration'],
            end=end_time,
            type=data['type'],
            description=data['description'],
            sector=data.get('sector')
        )
    
    def save(self, session):
        """Save to database (insert if new, update if exists)"""
        if self.id is None:
            session.add(self)
            session.flush()
            logging.info(f"Created new timeslot: {self.type} (ID: {self.id})")
        else:
            session.merge(self)
            logging.info(f"Updated timeslot: {self.type} (ID: {self.id})")
        session.commit()
        return self
    
    @classmethod
    def get_by_id(cls, session, timeslot_id: int) -> Optional['TimeSlotORM']:
        """Get timeslot by ID"""
        return session.query(cls).filter(cls.id == timeslot_id).first()
    
    @classmethod
    def get_by_parent(cls, session, parent_id: int) -> List['TimeSlotORM']:
        """Get all timeslots in a timetable"""
        return session.query(cls).filter(cls.parent == parent_id).order_by(cls.seq).all()
    
    @classmethod
    def get_all(cls, session) -> List['TimeSlotORM']:
        """Get all timeslots"""
        return session.query(cls).all()
    
    def delete(self, session):
        """Delete from database"""
        if self.id:
            session.delete(self)
            session.commit()
            logging.info(f"Deleted timeslot: {self.type} (ID: {self.id})") 
