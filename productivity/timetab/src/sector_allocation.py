from dataclasses import dataclass
from typing import List, Optional, Dict, Any

from .json_interface import JSONMixin
from .sector import Sector
from .time_slot import TimeSlot

@dataclass
class SectorAllocation(JSONMixin):
    sector: 'Sector'
    target_duration: int
    allocated_parts: List['TimeSlot']
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any], user: Optional['User'] = None) -> 'SectorAllocation':
        """Create SectorAllocation from dictionary"""
        from .sector import Sector
        from .time_slot import TimeSlot
        
        sector = Sector.from_dict(data['sector'], user)
        allocated_parts = [TimeSlot.from_dict(part, user, sector) for part in data['allocated_parts']]
        return cls(sector, data['target_duration'], allocated_parts)

    def to_dict(self) -> dict:
        """Convert to dictionary"""
        return {
            'sector': self.sector.to_dict(),
            'sector_abbr': self.sector.abbr,
            'target_duration': self.target_duration,
            'allocated_parts': [slot.to_dict() for slot in self.allocated_parts]
        } 