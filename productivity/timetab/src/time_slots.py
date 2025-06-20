import csv
import logging
from typing import List, Optional
from pathlib import Path
from .models import TimeSlot
from .time_utils import Time
from .csv_utils import CSVParser
from .ods_utils import ODSParser


class TimeSlots:
    def __init__(self, slots: Optional[List[TimeSlot]] = None):
        self.slots = slots or []
    
    @classmethod
    def from_csv(cls, filename: str) -> 'TimeSlots':
        """Create TimeSlots instance from CSV file"""
        slots = []
        try:
            # Create CSV parser with timetable required fields - using exact CSV column names
            parser = CSVParser(
                required_fields={'Seq', 'Start', 'Duration', 'End', 'Type', 'Description'}
            )
            
            rows = parser.parse_csv(filename)
            
            for i, row in enumerate(rows):
                slot = TimeSlot.from_strings(
                    seq=int(row['Seq']),
                    start=row['Start'],
                    duration=int(row['Duration']),
                    end=row['End'],
                    slot_type=row['Type'],
                    description=row['Description'],
                    original_index=i
                )
                slots.append(slot)
                
            logging.info(f"Successfully loaded {len(slots)} time slots from {filename}")
            for slot in slots:
                logging.debug(f"Loaded slot: {slot.slot_type}({slot.seq}) - {slot.start}-{slot.end}")
        except FileNotFoundError:
            logging.error(f"Timetable file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading timetable file {filename}: {e}")
            raise
        
        return cls(slots)
    
    @classmethod
    def from_ods(cls, filename: str) -> 'TimeSlots':
        """Create TimeSlots instance from ODS file"""
        slots = []
        try:
            # Create ODS parser with timetable required fields - using exact CSV column names
            parser = ODSParser(required_fields={'Seq', 'Start', 'Duration', 'End', 'Type', 'Description'})
            sheets = parser.parse_ods(filename)
            
            if not sheets:
                raise ValueError(f"No timetable data found in {filename}")
            
            # Use the first sheet found
            sheet_name = list(sheets.keys())[0]
            rows = sheets[sheet_name]
            
            for i, row in enumerate(rows):
                slot = TimeSlot.from_strings(
                    seq=int(row['Seq']),
                    start=row['Start'],
                    duration=int(row['Duration']),
                    end=row['End'],
                    slot_type=row['Type'],
                    description=row['Description'],
                    original_index=i
                )
                slots.append(slot)
                
            logging.info(f"Successfully loaded {len(slots)} time slots from sheet '{sheet_name}' in {filename}")
        except FileNotFoundError:
            logging.error(f"Timetable file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading timetable file {filename}: {e}")
            raise
        
        return cls(slots)
    
    @classmethod
    def from_file(cls, filename: str) -> 'TimeSlots':
        """Create TimeSlots instance from file (CSV or ODS)"""
        file_path = Path(filename)
        
        if file_path.suffix.lower() == '.ods':
            return cls.from_ods(filename)
        elif file_path.suffix.lower() == '.csv':
            return cls.from_csv(filename)
        else:
            raise ValueError(f"Unsupported file format: {filename}. Supported formats: .csv, .ods")
    
    def to_csv(self, filename: str, all_slots: bool = False):
        """
        Write time slots to CSV file
        
        Args:
            filename: Output file path
            all_slots: If True, write all time slots. If False, write only available/allocated slots.
        """
        if not self.slots:
            return
        
        # Determine which slots to write
        if all_slots:
            slots_to_write = self.slots
            logging.info(f"Writing all {len(slots_to_write)} time slots to {filename}")
        else:
            # Write only available slots (including allocated ones)
            slots_to_write = self.get_available_slots()
            logging.info(f"Writing {len(slots_to_write)} available/allocated time slots to {filename}")
        
        if not slots_to_write:
            logging.warning("No slots to write to output file")
            return
        
        try:
            with open(filename, 'w', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                # Add split column to header - using exact CSV column names
                writer.writerow(['Seq', 'Start', 'Duration', 'End', 'Type', 'Description', 'Split'])
                
                for i, slot in enumerate(slots_to_write, 1):
                    slot_dict = slot.to_dict()
                    writer.writerow([
                        i,  # Reorder from 1
                        slot_dict['start'],
                        slot_dict['duration'],
                        slot_dict['end'],
                        slot_dict['slot_type'],
                        slot_dict['description'],
                        slot_dict.get('split', '')  # Empty string if no split
                    ])
            logging.info(f"Successfully wrote {len(slots_to_write)} time slots to {filename}")
        except Exception as e:
            logging.error(f"Error writing timetable file {filename}: {e}")
            raise
    
    def get_available_slots(self) -> List[TimeSlot]:
        """Get all available time slots (Type starts with 'A')"""
        return [slot for slot in self.slots if slot.is_available()]
    
    def get_allocated_slots(self) -> List[TimeSlot]:
        """Get all allocated time slots (originally available slots with sector descriptions)"""
        return [slot for slot in self.slots if 
                (slot.is_available() and slot.sector is not None) or  # Work slots with sector reference
                slot.is_generated()]  # Break slots
    
    def count_allocated_slots(self) -> int:
        """Count number of allocated time slots"""
        return len(self.get_allocated_slots())
    
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
        
        # Sort by original order (not by start time)
        new_slots.sort(key=lambda x: x.seq)
        
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
    