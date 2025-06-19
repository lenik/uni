import logging
import random
from typing import List, Dict, Optional
from .models import TimeSlot, Sector, SectorAllocation
from .time_utils import Time
from .time_slots import TimeSlots
from .sectors import Sectors

class Scheduler:
    def __init__(self, shuffle_sectors: bool = False, large_first: bool = False):
        self.allocated_slots = []
        self.shuffle_sectors = shuffle_sectors
        self.large_first = large_first
        self.sector_split_counts: Dict[str, int] = {}  # Track split counts for each sector
    
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
        
        # Post-process to add split information to descriptions
        self._add_split_info_to_descriptions()
        
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
            sector_abbr = allocation.sector.abbr
            logging.debug(f"Allocating {sector_abbr}: target {allocation.target_duration} minutes")
            
            # Initialize split counter for this sector
            if sector_abbr not in self.sector_split_counts:
                self.sector_split_counts[sector_abbr] = 0
            
            while remaining_duration > 0 and available_slots_copy:
                current_slot = available_slots_copy[0]
                
                # Check for slot underflow
                if current_slot.duration <= 0:
                    logging.error(f"Slot underflow detected: slot {current_slot.order} has duration {current_slot.duration}")
                    raise ValueError(f"Invalid slot duration: {current_slot.duration} for slot {current_slot.order}")
                
                # Calculate how much we can take from this slot
                take_duration = min(remaining_duration, current_slot.duration)
                
                logging.debug(f"  Taking {take_duration} minutes from slot {current_slot.order} (has {current_slot.duration} minutes)")
                
                # Assign split number (0 for first part, 1, 2, 3... for subsequent parts)
                split_number = None
                if len(allocation.allocated_parts) > 0:
                    split_number = len(allocation.allocated_parts)  # 1, 2, 3, etc.
                    logging.debug(f"  Creating split part {split_number} for {sector_abbr}")
                
                # Create allocated part
                allocated_part = self._create_allocated_slot(current_slot, take_duration, allocation.sector, split_number)
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
                logging.warning(f"Could not fully allocate {sector_abbr}: {remaining_duration} minutes remaining")
            
            self.allocated_slots.extend(allocation.allocated_parts)
    
    def _add_split_info_to_descriptions(self):
        """Add split information to descriptions for split sectors"""
        # Group slots by sector
        sector_slots = {}
        for slot in self.allocated_slots:
            if slot.split is not None:  # Only process split slots
                sector_abbr = slot.description.split(':')[0]  # Extract sector abbreviation
                if sector_abbr not in sector_slots:
                    sector_slots[sector_abbr] = []
                sector_slots[sector_abbr].append(slot)
        
        # Add split information to descriptions
        for sector_abbr, slots in sector_slots.items():
            # Sort by split number
            slots.sort(key=lambda x: x.split)
            total_parts = len(slots) + 1  # +1 because first part has split=None
            
            # Update descriptions with split information
            for slot in slots:
                current_part = slot.split + 1  # Convert to 1-based for display
                
                # Remove any existing ellipsis or split info
                base_description = slot.description.split('...')[0].split(' (')[0]
                
                if current_part < total_parts:
                    # Non-final part: add ellipsis and split info
                    slot.description = f"{base_description}... ({current_part}/{total_parts})"
                else:
                    # Final part: add only split info
                    slot.description = f"{base_description} ({current_part}/{total_parts})"
            
            # Also update the first part (split=None) if it exists
            first_slots = [s for s in self.allocated_slots if s.split is None and s.description.startswith(f"{sector_abbr}:")]
            for slot in first_slots:
                base_description = slot.description.split('...')[0].split(' (')[0]
                if total_parts > 1:
                    slot.description = f"{base_description}... (1/{total_parts})"
    
    def _create_allocated_slot(self, original_slot: TimeSlot, duration: int, sector: Sector, split_number: Optional[int] = None) -> TimeSlot:
        """Create a new allocated time slot"""
        end_time = original_slot.start.add_minutes(duration)
        
        # Create base description (split info will be added later)
        description = f"{sector.abbr}: {sector.description}"
        
        return TimeSlot(
            order=original_slot.order,
            start=original_slot.start,
            duration=duration,
            end=end_time,
            slot_type=original_slot.slot_type,
            description=description,
            original_index=original_slot.original_index,
            split=split_number
        )