from sqlalchemy import Column, Integer, String, BigInteger, Time, ForeignKey, Text
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from typing import List, Optional, Dict, Any
from .db import Base, SessionLocal
from .time_utils import Time as TimeUtil
import logging

class SectorORM(Base):
    __tablename__ = 'sector'
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    seq = Column(Integer, nullable=False)
    weight = Column(Integer, nullable=False)
    ratio = Column(String(10), nullable=False)  # Store as string like "33.7%"
    abbr = Column(String(10), nullable=False)
    description = Column(String(200), nullable=False)
    uid = Column(Integer, ForeignKey('user.id'), nullable=False)
    
    # Relationships
    user = relationship("UserORM")
    timeslots = relationship("TimeSlotORM", back_populates="sector_rel")
    
    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'seq': self.seq,
            'weight': self.weight,
            'ratio': self.ratio,
            'abbr': self.abbr,
            'description': self.description,
            'uid': self.uid
        }
    
    @classmethod
    def from_dict(cls, data: dict) -> 'SectorORM':
        return cls(
            id=data.get('id'),
            seq=data['seq'],
            weight=data['weight'],
            ratio=data['ratio'],
            abbr=data['abbr'],
            description=data['description'],
            uid=data['uid']
        )
    
    def save(self, session):
        """Save to database (insert if new, update if exists)"""
        if self.id is None:
            session.add(self)
            session.flush()
            logging.info(f"Created new sector: {self.abbr} (ID: {self.id})")
        else:
            session.merge(self)
            logging.info(f"Updated sector: {self.abbr} (ID: {self.id})")
        session.commit()
        return self
    
    @classmethod
    def get_by_id(cls, session, sector_id: int) -> Optional['SectorORM']:
        """Get sector by ID"""
        return session.query(cls).filter(cls.id == sector_id).first()
    
    @classmethod
    def get_by_user(cls, session, user_id: int) -> List['SectorORM']:
        """Get all sectors for a user"""
        return session.query(cls).filter(cls.uid == user_id).order_by(cls.seq).all()
    
    @classmethod
    def get_all(cls, session) -> List['SectorORM']:
        """Get all sectors"""
        return session.query(cls).all()
    
    def delete(self, session):
        """Delete from database"""
        if self.id:
            session.delete(self)
            session.commit()
            logging.info(f"Deleted sector: {self.abbr} (ID: {self.id})")
