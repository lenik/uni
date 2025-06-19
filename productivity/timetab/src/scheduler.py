import logging
import random
from typing import List
from .models import TimeSlot, Sector, SectorAllocation
from .time_utils import Time
from .time_slots import TimeSlots
from .sectors import Sectors

class Scheduler:
    def __init__(self, shuffle_sectors: bool = False, large_first: bool = False):
        self.allocated_slots = []
        self.shuffle_sectors = shuffle_sectors
        self.large_first = large_first
    
    def allocate_sectors_proportionally(self, time_slots: TimeSlots, sectors: Sectors) -> TimeSlots:
        """Allocate sectors proportionally to available time slots"""
        
        # Get available slots and calculate totals
        available_slots = time_slots.get_available_slots()
        total_available_time = time_slots.get_total_available_time()
        total_weight = sectors.get_total_weight()
        
        logging.info(f"Total available time: {total_available_time} minutes")
        logging.info(f"Total sector weight: {total_weight}")
        
        # Calculate allocated duration for each sector
        sector_allocations = self._calculate_sector_allocations(sectors, total_available_time, total_weight)
        
        # Apply ordering based on options
        self._apply_sector_ordering(sector_allocations)
        
        # Allocate sectors
        self._allocate_sectors(sector_allocations, available_slots)
        
        # Create new TimeSlots instance with allocated slots
        new_time_slots = TimeSlots(time_slots.get_slots())
        new_time_slots.replace_available_slots(self.allocated_slots)
        
        return new_time_slots
    
    def _apply_sector_ordering(self, sector_allocations: List[SectorAllocation]):
        """Apply ordering to sector allocations based on options"""
        if self.shuffle_sectors:
            logging.info("Shuffling sectors in random order")
            random.shuffle(sector_allocations)
        elif self.large_first:
            logging.info("Ordering sectors by weight (largest first)")
            sector_allocations.sort(key=lambda x: x.sector.weight, reverse=True)
        else:
            # Default: preserve original order from file
            logging.info("Preserving original sector order from file")
        
        # Log the ordering
        order_str = ", ".join([f"{alloc.sector.abbr}({alloc.sector.weight})" for alloc in sector_allocations])
        logging.info(f"Sector allocation order: {order_str}")
    
    def _calculate_sector_allocations(self, sectors: Sectors, total_available_time: int, total_weight: int) -> List[SectorAllocation]:
        """Calculate target duration for each sector"""
        allocations = []
        for sector in sectors:
            percentage = sector.weight / total_weight
            allocated_duration = int(total_available_time * percentage)
            allocation = SectorAllocation(sector, allocated_duration, [])
            allocations.append(allocation)
            logging.info(f"{sector.abbr}: {sector.weight}/{total_weight} = {percentage:.3f} -> {allocated_duration} minutes")
        return allocations
    
    def _allocate_sectors(self, sector_allocations: List[SectorAllocation], available_slots: List[TimeSlot]):
        """Allocate sectors to available time slots"""
        available_slots_copy = available_slots.copy()
        
        for allocation in sector_allocations:
            remaining_duration = allocation.target_duration
            logging.debug(f"Allocating {allocation.sector.abbr}: target {allocation.target_duration} minutes")
            
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
                allocated_part = self._create_allocated_slot(current_slot, take_duration, allocation.sector)
                allocation.allocated_parts.append(allocated_part)
                
                # Update remaining duration
                remaining_duration -= take_duration
                
                # Update or remove the current slot
                if take_duration == current_slot.duration:
                    logging.debug(f"  Slot {current_slot.order} fully used, removing")
                    available_slots_copy.pop(0)
                else:
                    # Update start time and duration
                    current_slot.start = current_slot.start.add_minutes(take_duration)
                    current_slot.duration -= take_duration
                    logging.debug(f"  Slot {current_slot.order} partially used, remaining duration: {current_slot.duration}")
            
            if remaining_duration > 0:
                logging.warning(f"Could not fully allocate {allocation.sector.abbr}: {remaining_duration} minutes remaining")
            
            self.allocated_slots.extend(allocation.allocated_parts)
    
    def _create_allocated_slot(self, original_slot: TimeSlot, duration: int, sector: Sector) -> TimeSlot:
        """Create a new allocated time slot"""
        end_time = original_slot.start.add_minutes(duration)
        return TimeSlot(
            order=original_slot.order,
            start=original_slot.start,
            duration=duration,
            end=end_time,
            slot_type=original_slot.slot_type,
            description=f"{sector.abbr}: {sector.description}",
            original_index=original_slot.original_index
        )