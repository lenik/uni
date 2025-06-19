from dataclasses import dataclass
from typing import List
from .time_utils import Time

@dataclass
class TimeSlot:
    order: int
    start: Time
    duration: int
    end: Time
    slot_type: str
    description: str
    original_index: int
    
    def is_available(self) -> bool:
        """
        Check if this time slot is available (slot type starts with 'A').
        
        Returns:
            True if the slot is available, False otherwise
        """
        return self.slot_type.startswith('A')
    
    @classmethod
    def from_strings(cls, order: int, start: str, duration: int, end: str, 
                    slot_type: str, description: str, original_index: int) -> 'TimeSlot':
        """Create TimeSlot from string time values"""
        start_time = Time.from_string(start)
        end_time = Time.from_string(end)
        return cls(order, start_time, duration, end_time, slot_type, description, original_index)
    
    def to_dict(self) -> dict:
        """Convert to dictionary with string time values"""
        return {
            'order': self.order,
            'start': self.start.to_string(),
            'duration': self.duration,
            'end': self.end.to_string(),
            'slot_type': self.slot_type,
            'description': self.description,
            'original_index': self.original_index
        }

@dataclass
class Sector:
    sector_id: str
    occupy: float  # Changed from str to float
    weight: int
    abbr: str
    description: str
    
    @classmethod
    def from_strings(cls, sector_id: str, occupy: str, weight: int, 
                    abbr: str, description: str) -> 'Sector':
        """Create Sector from string values, parsing occupy as percentage"""
        from .time_utils import parse_percentage
        occupy_float = parse_percentage(occupy)
        return cls(sector_id, occupy_float, weight, abbr, description)
    
    def to_dict(self) -> dict:
        """Convert to dictionary with formatted percentage"""
        from .time_utils import format_percentage
        return {
            'sector_id': self.sector_id,
            'occupy': format_percentage(self.occupy),
            'weight': self.weight,
            'abbr': self.abbr,
            'description': self.description
        }

@dataclass
class SectorAllocation:
    sector: Sector
    target_duration: int
    allocated_parts: List[TimeSlot]
