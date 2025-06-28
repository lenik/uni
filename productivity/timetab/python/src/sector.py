from dataclasses import dataclass
from typing import Dict, Any, Optional
from .json_interface import JSONMixin
from collections import OrderedDict

@dataclass
class Sector(JSONMixin):
    seq: str
    ratio: float
    weight: int
    abbr: str
    description: str
    id: Optional[str] = None  # Globally unique ID (optional)
    user: Optional['User'] = None  # Reference to user
    
    @classmethod
    def from_strings(cls, seq: str, ratio: str, weight: int, 
                    abbr: str, description: str, user: Optional['User'] = None,
                    id: Optional[str] = None) -> 'Sector':
        """Create Sector from string values, parsing ratio as percentage"""
        from .time_utils import parse_percentage
        ratio_float = parse_percentage(ratio)
        return cls(seq, ratio_float, weight, abbr, description, id, user)
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any], user: Optional['User'] = None) -> 'Sector':
        """Create Sector from dictionary"""
        return cls.from_strings(
            seq=data['seq'],
            ratio=data['ratio'],
            weight=data['weight'],
            abbr=data['abbr'],
            description=data['description'],
            user=user,
            id=data.get('id')
        )

    def to_dict(self) -> dict:
        """Convert to dictionary with formatted percentage"""
        from .time_utils import format_percentage
        result = OrderedDict()
        if self.id:
            result['id'] = self.id
        if self.user:
            result['user_id'] = self.user.id
        result['seq'] = self.seq
        result['ratio'] = format_percentage(self.ratio, always_one_decimal=True)
        result['weight'] = self.weight
        result['abbr'] = self.abbr
        result['description'] = self.description
        return dict(result)
    
    def to_db(self) -> 'SectorORM':
        """Convert to database ORM model"""
        from .sector_orm import SectorORM
        from .time_utils import format_percentage
        
        data = self.to_dict()
        data['uid'] = self.user.id if self.user else None
        data['ratio'] = format_percentage(self.ratio, always_one_decimal=True)
        return SectorORM.from_dict(data)
    
    @classmethod
    def from_db(cls, orm_obj: 'SectorORM') -> 'Sector':
        """Create from database ORM model"""
        from .time_utils import parse_percentage
        
        return cls(
            id=str(orm_obj.id),
            seq=str(orm_obj.seq),
            ratio=parse_percentage(orm_obj.ratio),
            weight=orm_obj.weight,
            abbr=orm_obj.abbr,
            description=orm_obj.description
        ) 