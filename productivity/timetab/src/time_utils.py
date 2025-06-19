from typing import Union, Optional
import re


class Time:
    """Custom Time class for handling time in HH:MM format"""
    
    def __init__(self, hours: int, minutes: int):
        """Initialize Time with hours and minutes"""
        if not (0 <= hours <= 23):
            raise ValueError(f"Hours must be between 0 and 23, got {hours}")
        if not (0 <= minutes <= 59):
            raise ValueError(f"Minutes must be between 0 and 59, got {minutes}")
        
        self.hours = hours
        self.minutes = minutes
    
    @classmethod
    def from_string(cls, time_str: str) -> 'Time':
        """Create Time instance from string in HH:MM format"""
        if not isinstance(time_str, str):
            raise ValueError(f"Time string must be a string, got {type(time_str)}")
        
        # Handle empty string
        if not time_str.strip():
            raise ValueError("Time string cannot be empty")
        
        # Parse HH:MM format
        match = re.match(r'^(\d{1,2}):(\d{2})$', time_str.strip())
        if not match:
            raise ValueError(f"Invalid time format: {time_str}. Expected HH:MM")
        
        hours = int(match.group(1))
        minutes = int(match.group(2))
        
        return cls(hours, minutes)
    
    @classmethod
    def from_minutes(cls, total_minutes: int) -> 'Time':
        """Create Time instance from total minutes since midnight"""
        if total_minutes < 0:
            raise ValueError(f"Total minutes cannot be negative: {total_minutes}")
        
        hours = total_minutes // 60
        minutes = total_minutes % 60
        
        return cls(hours, minutes)
    
    def to_minutes(self) -> int:
        """Convert to total minutes since midnight"""
        return self.hours * 60 + self.minutes
    
    def to_string(self) -> str:
        """Convert to HH:MM string format"""
        return f"{self.hours:02d}:{self.minutes:02d}"
    
    def add_minutes(self, minutes: int) -> 'Time':
        """Add minutes and return new Time instance"""
        total_minutes = self.to_minutes() + minutes
        return Time.from_minutes(total_minutes)
    
    def subtract_minutes(self, minutes: int) -> 'Time':
        """Subtract minutes and return new Time instance"""
        total_minutes = self.to_minutes() - minutes
        if total_minutes < 0:
            raise ValueError(f"Cannot subtract {minutes} minutes from {self}")
        return Time.from_minutes(total_minutes)
    
    def __eq__(self, other) -> bool:
        if not isinstance(other, Time):
            return False
        return self.hours == other.hours and self.minutes == other.minutes
    
    def __lt__(self, other) -> bool:
        if not isinstance(other, Time):
            raise TypeError(f"Cannot compare Time with {type(other)}")
        return self.to_minutes() < other.to_minutes()
    
    def __le__(self, other) -> bool:
        return self < other or self == other
    
    def __gt__(self, other) -> bool:
        if not isinstance(other, Time):
            raise TypeError(f"Cannot compare Time with {type(other)}")
        return self.to_minutes() > other.to_minutes()
    
    def __ge__(self, other) -> bool:
        return self > other or self == other
    
    def __str__(self) -> str:
        return self.to_string()
    
    def __repr__(self) -> str:
        return f"Time({self.hours}, {self.minutes})"
    
    def __hash__(self) -> int:
        return hash((self.hours, self.minutes))


def parse_time_to_minutes(time_str: str) -> int:
    """Convert time string (HH:MM) to minutes since midnight"""
    time_obj = Time.from_string(time_str)
    return time_obj.to_minutes()


def minutes_to_time(minutes: int) -> str:
    """Convert minutes since midnight to time string (HH:MM)"""
    time_obj = Time.from_minutes(minutes)
    return time_obj.to_string()


def parse_percentage(percentage_str: str) -> float:
    """
    Parse percentage string to float.
    
    Args:
        percentage_str: String like "33.7%" or "6.7%"
        
    Returns:
        Float value (e.g., 33.7, 6.7)
        
    Raises:
        ValueError: If the string is not a valid percentage
    """
    if not isinstance(percentage_str, str):
        raise ValueError(f"Percentage must be a string, got {type(percentage_str)}")
    
    # Remove whitespace
    percentage_str = percentage_str.strip()
    
    # Handle empty string
    if not percentage_str:
        raise ValueError("Percentage string cannot be empty")
    
    # Check if it ends with %
    if not percentage_str.endswith('%'):
        raise ValueError(f"Percentage must end with '%', got: {percentage_str}")
    
    # Extract the numeric part
    numeric_part = percentage_str[:-1]
    
    try:
        value = float(numeric_part)
        if value < 0:
            raise ValueError(f"Percentage cannot be negative: {percentage_str}")
        return value
    except ValueError as e:
        raise ValueError(f"Invalid percentage format: {percentage_str}. {str(e)}")


def format_percentage(value: float) -> str:
    """
    Format float as percentage string, removing trailing zeros.
    
    Args:
        value: Float value to format
        
    Returns:
        Formatted percentage string (e.g., "33.7%", "6%")
    """
    if not isinstance(value, (int, float)):
        raise ValueError(f"Value must be numeric, got {type(value)}")
    
    if value < 0:
        raise ValueError(f"Percentage cannot be negative: {value}")
    
    # Convert to string and remove trailing zeros after decimal point
    formatted = f"{value:.1f}"
    if formatted.endswith('.0'):
        formatted = formatted[:-2]  # Remove .0
    
    return f"{formatted}%"