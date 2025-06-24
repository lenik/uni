from dataclasses import dataclass
from typing import Dict, Any, Optional
from .json_interface import JSONMixin
from collections import OrderedDict

@dataclass
class Sector(JSONMixin):
    seq: str
    occupy: float
    weight: int
    abbr: str
    description: str
    id: Optional[str] = None  # Globally unique ID (optional)
    user: Optional['User'] = None  # Reference to user
    
    @classmethod
    def from_strings(cls, seq: str, occupy: str, weight: int, 
                    abbr: str, description: str, user: Optional['User'] = None,
                    id: Optional[str] = None) -> 'Sector':
        """Create Sector from string values, parsing occupy as percentage"""
        from .time_utils import parse_percentage
        occupy_float = parse_percentage(occupy)
        return cls(seq, occupy_float, weight, abbr, description, id, user)
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any], user: Optional['User'] = None) -> 'Sector':
        """Create Sector from dictionary"""
        return cls.from_strings(
            seq=data['seq'],
            occupy=data['occupy'],
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
        result['occupy'] = format_percentage(self.occupy, always_one_decimal=True)
        result['weight'] = self.weight
        result['abbr'] = self.abbr
        result['description'] = self.description
        return dict(result) 