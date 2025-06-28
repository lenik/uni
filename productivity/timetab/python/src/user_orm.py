from sqlalchemy import Column, Integer, String, BigInteger, Time, ForeignKey, Text
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from typing import List, Optional, Dict, Any
from .db import Base, SessionLocal
from .time_utils import Time as TimeUtil
import logging

class UserORM(Base):
    __tablename__ = 'user'
    
    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(30), nullable=False)
    display_name = Column(String(80), nullable=False)
    description = Column(String(200))
    passwd = Column(String(30))
    
    # Relationships
    timetables = relationship("TimeTableORM", back_populates="user")
    sectors_groups = relationship("SectorsORM", back_populates="user")
    
    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'name': self.name,
            'display_name': self.display_name,
            'description': self.description,
            'passwd': self.passwd
        }
    
    @classmethod
    def from_dict(cls, data: dict) -> 'UserORM':
        return cls(
            id=data.get('id'),
            name=data['name'],
            display_name=data['display_name'],
            description=data.get('description'),
            passwd=data.get('passwd')
        )
    
    def save(self, session):
        """Save to database (insert if new, update if exists)"""
        if self.id is None:
            session.add(self)
            session.flush()  # Get the ID
            logging.info(f"Created new user: {self.name} (ID: {self.id})")
        else:
            session.merge(self)
            logging.info(f"Updated user: {self.name} (ID: {self.id})")
        session.commit()
        return self
    
    @classmethod
    def get_by_id(cls, session, user_id: int) -> Optional['UserORM']:
        """Get user by ID"""
        return session.query(cls).filter(cls.id == user_id).first()
    
    @classmethod
    def get_by_name(cls, session, name: str) -> Optional['UserORM']:
        """Get user by name"""
        return session.query(cls).filter(cls.name == name).first()
    
    @classmethod
    def get_all(cls, session) -> List['UserORM']:
        """Get all users"""
        return session.query(cls).all()
    
    def delete(self, session):
        """Delete from database"""
        if self.id:
            session.delete(self)
            session.commit()
            logging.info(f"Deleted user: {self.name} (ID: {self.id})")
