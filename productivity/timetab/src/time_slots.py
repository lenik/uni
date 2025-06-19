import csv
import logging
from typing import List, Optional
from .models import TimeSlot
from .time_utils import Time
from .csv_utils import CSVParser


class TimeSlots:
    def __init__(self, slots: Optional[List[TimeSlot]] = None):
        self.slots = slots or []
    
    @classmethod
    def from_csv(cls, filename: str) -> 'TimeSlots':
        """Create TimeSlots instance from CSV file"""
        slots = []
        try:
            # Create CSV parser with required fields and field mappings
            parser = CSVParser(
                required_fields=['order', 'start', 'duration', 'end', 'type', 'description'],
                field_mappings={
                    'Order': 'order',
                    'Start': 'start', 
                    'Duration': 'duration',
                    'End': 'end',
                    'Type': 'type',
                    'Description': 'description'
                }
            )
            
            rows = parser.parse_csv(filename)
            
            for i, row in enumerate(rows):
                slot = TimeSlot.from_strings(
                    order=int(row['order']),
                    start=row['start'],
                    duration=int(row['duration']),
                    end=row['end'],
                    slot_type=row['type'],
                    description=row['description'],
                    original_index=i
                )
                slots.append(slot)
                
            logging.info(f"Successfully loaded {len(slots)} time slots from {filename}")
        except FileNotFoundError:
            logging.error(f"Timetable file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading timetable file {filename}: {e}")
            raise
        
        return cls(slots)
    
    def to_csv(self, filename: str):
        """Write time slots to CSV file"""
        if not self.slots:
            return
        
        try:
            with open(filename, 'w', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                writer.writerow(['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
                
                for i, slot in enumerate(self.slots, 1):
                    slot_dict = slot.to_dict()
                    writer.writerow([
                        i,  # Reorder from 1
                        slot_dict['start'],
                        slot_dict['duration'],
                        slot_dict['end'],
                        slot_dict['slot_type'],
                        slot_dict['description']
                    ])
            logging.info(f"Successfully wrote {len(self.slots)} time slots to {filename}")
        except Exception as e:
            logging.error(f"Error writing timetable file {filename}: {e}")
            raise
    
    def get_available_slots(self) -> List[TimeSlot]:
        """Get all available time slots (Type starts with 'A')"""
        return [slot for slot in self.slots if slot.is_available()]
    
    def get_total_available_time(self) -> int:
        """Calculate total available time in minutes"""
        available_slots = self.get_available_slots()
        return sum(slot.duration for slot in available_slots)
    
    def count_available_slots(self) -> int:
        """Count number of available time slots"""
        return len(self.get_available_slots())
    
    def replace_available_slots(self, allocated_slots: List[TimeSlot]):
        """Replace available slots with allocated ones"""
        available_indices = {slot.original_index for slot in self.get_available_slots()}
        
        # Keep non-available slots
        new_slots = [slot for i, slot in enumerate(self.slots) if i not in available_indices]
        
        # Add allocated slots
        new_slots.extend(allocated_slots)
        
        # Sort by original order
        new_slots.sort(key=lambda x: x.order)
        
        self.slots = new_slots
    
    def get_slots(self) -> List[TimeSlot]:
        """Get all time slots"""
        return self.slots
    
    def add_slot(self, slot: TimeSlot):
        """Add a time slot"""
        self.slots.append(slot)
    
    def remove_slot(self, slot: TimeSlot):
        """Remove a time slot"""
        self.slots.remove(slot)
    
    def __len__(self) -> int:
        return len(self.slots)
    
    def __iter__(self):
        return iter(self.slots)
    