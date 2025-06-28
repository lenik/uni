import json
from abc import ABC, abstractmethod
from typing import Dict, Any, Optional, TypeVar, Generic

T = TypeVar('T')

class IJSONForm(ABC):
    """Interface for JSON serialization/deserialization"""
    
    @classmethod
    @abstractmethod
    def from_json(cls, json_str: str, **kwargs) -> 'IJSONForm':
        """Create instance from JSON string"""
        pass
    
    @classmethod
    @abstractmethod
    def from_dict(cls, data: Dict[str, Any], **kwargs) -> 'IJSONForm':
        """Create instance from dictionary"""
        pass
    
    @abstractmethod
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary"""
        pass
    
    def to_json(self, filename: Optional[str] = None) -> str:
        """
        Convert to JSON string. Optionally write to file.
        
        Args:
            filename: Optional file path to write JSON to
            
        Returns:
            JSON string representation
        """
        json_str = json.dumps(self.to_dict(), ensure_ascii=False, indent=2)
        
        if filename:
            with open(filename, 'w', encoding='utf-8') as f:
                f.write(json_str)
        
        return json_str

class JSONMixin:
    """Mixin providing JSON serialization/deserialization functionality"""
    
    @classmethod
    def from_json(cls, json_str: str, **kwargs):
        """Create instance from JSON string"""
        data = json.loads(json_str)
        return cls.from_dict(data, **kwargs)
    
    def to_json(self, filename: Optional[str] = None) -> str:
        """
        Convert to JSON string. Optionally write to file.
        
        Args:
            filename: Optional file path to write JSON to
            
        Returns:
            JSON string representation
        """
        json_str = json.dumps(self.to_dict(), ensure_ascii=False, indent=2)
        
        if filename:
            with open(filename, 'w', encoding='utf-8') as f:
                f.write(json_str)
        
        return json_str 
    
    def dump_json(self):
        json = self.to_json()
        print(json)
        return json
