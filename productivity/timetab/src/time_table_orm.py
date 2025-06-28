from sqlalchemy import Column, Integer, String, BigInteger, Time, ForeignKey, Text
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from typing import List, Optional, Dict, Any
from .db import Base, SessionLocal
from .time_utils import Time as TimeUtil
import logging

class TimeTableORM(Base):
    __tablename__ = 'timetab'
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    label = Column(String(80), nullable=False)
    description = Column(String(200))
    uid = Column(Integer, ForeignKey('user.id'), nullable=False)
    
    # Relationships
    user = relationship("UserORM", back_populates="timetables")
    timeslots = relationship("TimeSlotORM", back_populates="parent_table")
    
    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'label': self.label,
            'description': self.description,
            'uid': self.uid
        }
    
    @classmethod
    def from_dict(cls, data: dict) -> 'TimeTableORM':
        return cls(
            id=data.get('id'),
            label=data['label'],
            description=data.get('description'),
            uid=data['uid']
        )
    
    def save(self, session):
        """Save to database (insert if new, update if exists)"""
        if self.id is None:
            session.add(self)
            session.flush()
            logging.info(f"Created new timetable: {self.label} (ID: {self.id})")
        else:
            session.merge(self)
            logging.info(f"Updated timetable: {self.label} (ID: {self.id})")
        session.commit()
        return self
    
    @classmethod
    def get_by_id(cls, session, timetable_id: int) -> Optional['TimeTableORM']:
        """Get timetable by ID"""
        return session.query(cls).filter(cls.id == timetable_id).first()
    
    @classmethod
    def get_by_user(cls, session, user_id: int) -> List['TimeTableORM']:
        """Get all timetables for a user"""
        return session.query(cls).filter(cls.uid == user_id).all()
    
    @classmethod
    def get_all(cls, session) -> List['TimeTableORM']:
        """Get all timetables"""
        return session.query(cls).all()
    
    def delete(self, session):
        """Delete from database"""
        if self.id:
            session.delete(self)
            session.commit()
            logging.info(f"Deleted timetable: {self.label} (ID: {self.id})")
