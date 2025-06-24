from dataclasses import dataclass
from typing import Dict, Any, Optional
from .json_interface import JSONMixin

@dataclass
class User(JSONMixin):
    name: str
    display_name: str
    id: Optional[str] = None  # Optional since users can be newly created
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'User':
        """Create User from dictionary"""
        return cls(
            name=data['name'],
            display_name=data['display_name'],
            id=data.get('id')  # Use .get() since id is optional
        )

    def to_dict(self) -> dict:
        """Convert to dictionary"""
        result = {
            'name': self.name,
            'display_name': self.display_name
        }
        if self.id:
            result['id'] = self.id
        return result

