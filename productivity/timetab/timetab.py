#!/usr/bin/python

import csv
import random
import logging
import argparse
from typing import List, Dict, Tuple
from dataclasses import dataclass

@dataclass
class TimeSlot:
    order: int
    start: str
    duration: int
    end: str
    slot_type: str
    description: str
    original_index: int

@dataclass
class Sector:
    sector_id: str
    occupy: str
    weight: int
    abbr: str
    description: str

def setup_logging(verbose: bool, quiet: bool):
    """Setup logging configuration based on verbosity flags"""
    if quiet:
        level = logging.ERROR
    elif verbose:
        level = logging.DEBUG
    else:
        level = logging.INFO
    
    logging.basicConfig(
        level=level,
        format='%(asctime)s - %(levelname)s - %(message)s',
        datefmt='%H:%M:%S'
    )

def parse_time_to_minutes(time_str: str) -> int:
    """Convert time string (HH:MM) to minutes since midnight"""
    hours, minutes = map(int, time_str.split(':'))
    return hours * 60 + minutes

def minutes_to_time(minutes: int) -> str:
    """Convert minutes since midnight to time string (HH:MM)"""
    hours = minutes // 60
    mins = minutes % 60
    return f"{hours:02d}:{mins:02d}"

def read_timetable(filename: str) -> List[TimeSlot]:
    """Read TimeTable.csv and return list of time slots"""
    timetable = []
    with open(filename, 'r', encoding='utf-8') as file:
        reader = csv.DictReader(file)
        for i, row in enumerate(reader):
            slot = TimeSlot(
                order=int(row['Order']),
                start=row['Start'],
                duration=int(row['Duration']),
                end=row['End'],
                slot_type=row['Type'],
                description=row['Description'],
                original_index=i
            )
            timetable.append(slot)
    return timetable

def read_sectors(filename: str) -> List[Sector]:
    """Read Sectors.csv, skip first 4 lines, and return sectors with weights"""
    sectors = []
    with open(filename, 'r', encoding='utf-8') as file:
        # Skip first 4 lines
        for _ in range(4):
            next(file)
        
        reader = csv.DictReader(file)
        for row in reader:
            sector = Sector(
                sector_id=row['Sector'],
                occupy=row['Occupy'],
                weight=int(row['Weight']),
                abbr=row['Abbr'],
                description=row['Description']
            )
            sectors.append(sector)
    return sectors

def allocate_sectors_proportionally(timetable: List[TimeSlot], sectors: List[Sector]) -> List[TimeSlot]:
    """Allocate sectors proportionally to available time slots"""
    
    # Filter available time slots (Type starts with 'A')
    available_slots = [slot for slot in timetable if slot.slot_type.startswith('A')]
    
    # Calculate total available time and total weight
    total_available_time = sum(slot.duration for slot in available_slots)
    total_weight = sum(sector.weight for sector in sectors)
    
    logging.info(f"Total available time: {total_available_time} minutes")
    logging.info(f"Total sector weight: {total_weight}")
    
    # Calculate allocated duration for each sector
    sector_allocations = []
    for sector in sectors:
        percentage = sector.weight / total_weight
        allocated_duration = int(total_available_time * percentage)
        sector_allocations.append((sector, allocated_duration))
        logging.info(f"{sector.abbr}: {sector.weight}/{total_weight} = {percentage:.3f} -> {allocated_duration} minutes")
    
    # Sort sectors by weight (descending) to allocate larger chunks first
    sector_allocations.sort(key=lambda x: x[0].weight, reverse=True)
    
    # Create a copy of available slots for allocation
    available_slots_copy = available_slots.copy()
    allocated_slots = []
    
    # Allocate each sector
    for sector, target_duration in sector_allocations:
        remaining_duration = target_duration
        allocated_parts = []
        
        logging.debug(f"Allocating {sector.abbr}: target {target_duration} minutes")
        
        while remaining_duration > 0 and available_slots_copy:
            current_slot = available_slots_copy[0]
            
            # Check for slot underflow
            if current_slot.duration <= 0:
                logging.error(f"Slot underflow detected: slot {current_slot.order} has duration {current_slot.duration}")
                raise ValueError(f"Invalid slot duration: {current_slot.duration} for slot {current_slot.order}")
            
            # Calculate how much we can take from this slot
            take_duration = min(remaining_duration, current_slot.duration)
            
            logging.debug(f"  Taking {take_duration} minutes from slot {current_slot.order} (has {current_slot.duration} minutes)")
            
            # Create allocated part
            allocated_part = TimeSlot(
                order=current_slot.order,
                start=current_slot.start,
                duration=take_duration,
                end=minutes_to_time(parse_time_to_minutes(current_slot.start) + take_duration),
                slot_type=current_slot.slot_type,
                description=f"{sector.abbr}: {sector.description}",
                original_index=current_slot.original_index
            )
            allocated_parts.append(allocated_part)
            
            # Update remaining duration
            remaining_duration -= take_duration
            
            # Update or remove the current slot
            if take_duration == current_slot.duration:
                # Slot fully used, remove it
                logging.debug(f"  Slot {current_slot.order} fully used, removing")
                available_slots_copy.pop(0)
            else:
                # Slot partially used, update it
                current_slot.start = minutes_to_time(parse_time_to_minutes(current_slot.start) + take_duration)
                current_slot.duration -= take_duration
                # current_slot.end remains unchanged since we're only taking a portion
                logging.debug(f"  Slot {current_slot.order} partially used, remaining duration: {current_slot.duration}")
        
        if remaining_duration > 0:
            logging.warning(f"Could not fully allocate {sector.abbr}: {remaining_duration} minutes remaining")
        
        allocated_slots.extend(allocated_parts)
    
    # Create final timetable by replacing available slots with allocated ones
    final_timetable = []
    available_slot_indices = {slot.original_index for slot in available_slots}
    
    for i, slot in enumerate(timetable):
        if i in available_slot_indices:
            # This was an available slot, skip it (will be replaced by allocated slots)
            continue
        else:
            # This was not an available slot, keep it as is
            final_timetable.append(slot)
    
    # Insert allocated slots in the correct order
    allocated_slots.sort(key=lambda x: x.original_index)
    final_timetable.extend(allocated_slots)
    
    # Sort by original order
    final_timetable.sort(key=lambda x: x.order)
    
    return final_timetable

def write_output(timetable: List[TimeSlot], filename: str):
    """Write the updated timetable to a new CSV file"""
    if not timetable:
        return
    
    with open(filename, 'w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
        
        for i, slot in enumerate(timetable, 1):
            writer.writerow([
                i,  # Reorder from 1
                slot.start,
                slot.duration,
                slot.end,
                slot.slot_type,
                slot.description
            ])

def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(description='Automatic scheduler tool for allocating sectors to time slots')
    parser.add_argument('-v', '--verbose', action='store_true', help='Enable verbose logging')
    parser.add_argument('-q', '--quiet', action='store_true', help='Suppress all logging except errors')
    parser.add_argument('--timetable', default='TimeTable.csv', help='Input timetable file (default: TimeTable.csv)')
    parser.add_argument('--sectors', default='Sectors.csv', help='Input sectors file (default: Sectors.csv)')
    parser.add_argument('-o', '--output', default='Scheduled_TimeTable.csv', help='Output file (default: Scheduled_TimeTable.csv)')
    
    args = parser.parse_args()
    
    # Setup logging
    setup_logging(args.verbose, args.quiet)
    
    try:
        # Read input files
        logging.info(f"Reading timetable from {args.timetable}")
        timetable = read_timetable(args.timetable)
        logging.info(f"Reading sectors from {args.sectors}")
        sectors = read_sectors(args.sectors)
        
        logging.info(f"Loaded {len(timetable)} time slots")
        logging.info(f"Loaded {len(sectors)} sectors")
        
        # Count available slots
        available_slots = [slot for slot in timetable if slot.slot_type.startswith('A')]
        logging.info(f"Found {len(available_slots)} available time slots (A type)")
        
        # Allocate sectors to timetable
        logging.info("Starting sector allocation...")
        updated_timetable = allocate_sectors_proportionally(timetable, sectors)
        
        # Write output
        logging.info(f"Writing output to {args.output}")
        write_output(updated_timetable, args.output)
        
        logging.info("Scheduling completed successfully!")
        
        # Print summary of allocations (only in non-quiet mode)
        if not args.quiet:
            print("\nAllocation Summary:")
            for slot in updated_timetable:
                if slot.slot_type.startswith('A'):
                    print(f"{slot.start}-{slot.end} ({slot.duration}min, {slot.slot_type}): {slot.description}")
    
    except FileNotFoundError as e:
        logging.error(f"File not found: {e}")
        return 1
    except ValueError as e:
        logging.error(f"Invalid data: {e}")
        return 1
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())