from dataclasses import dataclass
from typing import Dict, Any, Optional
from .json_interface import JSONMixin
from collections import OrderedDict

@dataclass
class User(JSONMixin):
    name: str
    display_name: str
    id: Optional[str] = None  # Optional since users can be newly created
    description: Optional[str] = None
    passwd: Optional[str] = None
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'User':
        """Create User from dictionary"""
        return cls(
            name=data['name'],
            display_name=data['display_name'],
            id=data.get('id'),  # Use .get() since id is optional
            description=data.get('description'),
            passwd=data.get('passwd')
        )

    def to_dict(self) -> dict:
        """Convert to dictionary"""
        result = OrderedDict()
        if self.id:
            result['id'] = self.id
        result['name'] = self.name
        result['display_name'] = self.display_name
        if self.description:
            result['description'] = self.description
        if self.passwd:
            result['passwd'] = self.passwd
        return dict(result)

    def to_db(self) -> 'UserORM':
        """Convert to database ORM model"""
        from .user_orm import UserORM
        return UserORM.from_dict(self.to_dict())
    
    @classmethod
    def from_db(cls, orm_obj: 'UserORM') -> 'User':
        """Create from database ORM model"""
        return cls(
            id=str(orm_obj.id),
            name=orm_obj.name,
            display_name=orm_obj.display_name,
            description=orm_obj.description,
            passwd=orm_obj.passwd
        )

