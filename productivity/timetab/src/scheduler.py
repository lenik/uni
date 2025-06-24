import logging
import random
from typing import List, Dict, Optional
from .time_slot import TimeSlot
from .sector import Sector
from .sector_allocation import SectorAllocation
from .time_utils import Time
from .time_slots import TimeSlots
from .sectors import Sectors

class Scheduler:
    def __init__(self, shuffle_sectors: bool = False, large_first: bool = False, 
                 maxload: Optional[int] = None, break_minutes: int = 0):
        self.allocated_slots = []
        self.shuffle_sectors = shuffle_sectors
        self.large_first = large_first
        self.maxload = maxload
        self.break_minutes = break_minutes
        self.accumulated_break_time = 0  # Track accumulated break time for adjacent sectors
    
    def allocate_sectors_proportionally(self, time_slots: TimeSlots, sectors: Sectors) -> TimeSlots:
        """Allocate sectors proportionally to available time slots"""
        
        # Get available slots and calculate totals
        available_slots = time_slots.get_available_slots()
        total_available_time = time_slots.get_total_available_time()
        total_weight = sectors.get_total_weight()
        
        logging.info(f"Total available time: {total_available_time} minutes")
        logging.info(f"Total sector weight: {total_weight}")
        
        if self.maxload:
            logging.info(f"Max load per slice: {self.maxload} minutes")
        if self.break_minutes > 0:
            logging.info(f"Break between slices: {self.break_minutes} minutes")
        
        # Calculate allocated duration for each sector
        sector_allocations = self._calculate_sector_allocations(sectors, total_available_time, total_weight)
        
        # Apply ordering based on options
        self._apply_sector_ordering(sector_allocations)
        
        # Allocate sectors
        self._allocate_sectors(sector_allocations, available_slots)
        
        # Post-process to add split information to descriptions
        self._add_split_info_to_descriptions()
        
        time_slots.dump_json()
        
        # Insert break time slots (pass original slots for reference)
        self._insert_break_slots(time_slots.get_slots())
        
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
        
        # Log the ordering with both abbreviation and ID
        order_str = ", ".join([f"{alloc.sector.abbr}" for alloc in sector_allocations])
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
    
    def _split_duration_with_breaks(self, total_duration: int, sector: Sector) -> List[tuple]:
        """
        Split duration into chunks respecting maxload and break constraints.
        Returns list of tuples: (work_duration, sequence_number)
        Work duration = maxload - break_minutes to leave room for breaks.
        """
        if not self.maxload or total_duration <= (self.maxload - self.break_minutes):
            return [(total_duration, 1)]
        
        chunks = []
        remaining = total_duration
        sequence_number = 1
        
        # Calculate actual work duration per chunk (maxload minus break time)
        work_duration_per_chunk = self.maxload - self.break_minutes
        
        while remaining > 0:
            # Calculate work duration for this chunk
            work_duration = min(remaining, work_duration_per_chunk)
            
            chunks.append((work_duration, sequence_number))
            remaining -= work_duration
            sequence_number += 1
        
        logging.debug(f"Splitting {total_duration} minutes for {sector.abbr} into chunks: {chunks}")
        return chunks
    
    def _allocate_sectors(self, sector_allocations: List[SectorAllocation], available_slots: List[TimeSlot]):
        """Allocate sectors to available time slots"""
        available_slots_copy = available_slots.copy()
        
        for allocation in sector_allocations:
            remaining_duration = allocation.target_duration
            sector = allocation.sector
            logging.debug(f"Allocating {sector.abbr}: target {allocation.target_duration} minutes")
            
            # Split the duration if maxload is specified
            duration_chunks = self._split_duration_with_breaks(remaining_duration, sector)
            
            for chunk_index, (work_duration, sequence_number) in enumerate(duration_chunks):
                # Check if we can use accumulated break time
                actual_work_duration = work_duration
                if chunk_index == 0 and self.accumulated_break_time > 0:
                    # Use accumulated break time for the first chunk
                    actual_work_duration = work_duration + self.accumulated_break_time
                    self.accumulated_break_time = 0
                    logging.debug(f"Using {work_duration} accumulated break minutes for {sector.abbr}")
                
                # Allocate this chunk with the same sequence number for all slots in this chunk
                allocated_duration = self._allocate_chunk(available_slots_copy, actual_work_duration, allocation, sequence_number)
                
                if allocated_duration < actual_work_duration:
                    # Could not fully allocate, accumulate remaining time as break
                    remaining_break = actual_work_duration - allocated_duration
                    self.accumulated_break_time += remaining_break
                    logging.debug(f"Accumulating {remaining_break} minutes as break time")
                    # Don't break here - continue with next chunk if possible
                
                # Update remaining duration for this sector
                remaining_duration -= min(allocated_duration, work_duration)
            
            if remaining_duration > 0:
                logging.warning(f"Could not fully allocate {sector.abbr}: {remaining_duration} minutes remaining")
        
        # Collect all allocated parts from all allocations (avoid duplicates)
        seen_slots = set()
        for allocation in sector_allocations:
            for slot in allocation.allocated_parts:
                # Create a unique identifier for each slot
                slot_id = (slot.start, slot.end, slot.description, slot.split)
                if slot_id not in seen_slots:
                    seen_slots.add(slot_id)
                    self.allocated_slots.append(slot)
    
    def _allocate_chunk(self, available_slots_copy: List[TimeSlot], target_duration: int, 
                       allocation: SectorAllocation, sequence_number: int) -> int:
        """Allocate a single chunk of work"""
        sector = allocation.sector
        allocated_duration = 0
        
        while allocated_duration < target_duration and available_slots_copy:
            current_slot = available_slots_copy[0]
            # Check for slot underflow
            if current_slot.duration <= 0:
                logging.error(f"Slot underflow detected: {current_slot.slot_type}({current_slot.seq}) has duration {current_slot.duration}")
                raise ValueError(f"Invalid slot duration: {current_slot.duration} for slot {current_slot.slot_type}({current_slot.seq})")
            # Calculate how much we can take from this slot
            remaining_needed = target_duration - allocated_duration
            take_duration = min(remaining_needed, current_slot.duration)
            logging.debug(f"  Taking {take_duration} minutes from {current_slot.slot_type}({current_slot.seq}) (has {current_slot.duration} minutes)")
            # Create allocated part with the same sequence number for this chunk
            allocated_part = self._create_allocated_slot(current_slot, take_duration, allocation.sector, sequence_number)
            allocation.allocated_parts.append(allocated_part)
            # Update allocated duration
            allocated_duration += take_duration
            # Update or remove the current slot
            if take_duration == current_slot.duration:
                logging.debug(f"  {current_slot.slot_type}({current_slot.seq}) fully used, removing")
                available_slots_copy.pop(0)
            else:
                # Update start time and duration
                current_slot.start = current_slot.start.add_minutes(take_duration)
                current_slot.duration -= take_duration
                logging.debug(f"  {current_slot.slot_type}({current_slot.seq}) partially used, remaining duration: {current_slot.duration}")
        return allocated_duration
    
    def _insert_break_slots(self, original_slots):
        """Insert break time slots between maxload chunks"""
        if not self.break_minutes or not self.maxload:
            return
        
        # Group slots by sector and sequence number
        sector_chunks = {}
        for slot in self.allocated_slots:
            if slot.sector is None:
                continue  # Skip slots without sector reference
            
            sector_id = slot.sector.abbr
            sequence_number = slot.split if slot.split is not None else 1
            
            if sector_id not in sector_chunks:
                sector_chunks[sector_id] = {}
            if sequence_number not in sector_chunks[sector_id]:
                sector_chunks[sector_id][sequence_number] = []
            sector_chunks[sector_id][sequence_number].append(slot)
        
        # Create a flat list of chunks in chronological order
        all_chunks = []
        for sector_id, chunks in sector_chunks.items():
            for sequence_number in sorted(chunks.keys()):
                chunk_slots = chunks[sequence_number]
                # Find the latest end time in this chunk
                chunk_end = max(slot.end for slot in chunk_slots)
                all_chunks.append({
                    'sector_id': sector_id,
                    'sequence': sequence_number,
                    'end_time': chunk_end,
                    'slots': chunk_slots
                })
        
        # Sort chunks by end time
        all_chunks.sort(key=lambda x: x['end_time'])
        
        # Insert break slots between consecutive chunks and adjust work slot timing
        new_slots = []
        accumulated_break_time = 0  # Track total break time to adjust subsequent work slots
        
        for i, chunk in enumerate(all_chunks):
            # Adjust the start and end times of all slots in this chunk
            for slot in chunk['slots']:
                slot.start = slot.start.add_minutes(accumulated_break_time)
                slot.end = slot.end.add_minutes(accumulated_break_time)
            
            # Add all slots for this chunk
            new_slots.extend(chunk['slots'])
            
            # Insert break slot if this is not the last chunk AND the next chunk is from the same sector
            if i < len(all_chunks) - 1:
                next_chunk = all_chunks[i + 1]
                
                # Only insert break if the next chunk is from the same sector
                if next_chunk['sector_id'] == chunk['sector_id']:
                    # Create break slot starting immediately after this chunk ends
                    break_start = chunk['end_time'].add_minutes(accumulated_break_time)
                    break_end = break_start.add_minutes(self.break_minutes)
                    
                    # Get the sector reference from the first slot in the chunk
                    sector = chunk['slots'][0].sector
                    
                    break_slot = TimeSlot(
                        id=None,  # Break slots don't have IDs
                        seq=chunk['slots'][0].seq,  # Keep for legacy, but prefer abbr elsewhere
                        start=break_start,
                        duration=self.break_minutes,
                        end=break_end,
                        slot_type="Break/Load",
                        description=f"{sector.abbr}: -",  # Use sector abbreviation for display
                        original_index=chunk['slots'][0].original_index,
                        user=chunk['slots'][0].user,  # Preserve user reference
                        sector=sector,  # Direct sector reference
                        split=None
                    )
                    
                    new_slots.append(break_slot)
                    accumulated_break_time += self.break_minutes
                    logging.debug(f"Inserted break slot: {break_start}-{break_end} ({self.break_minutes}min) for {sector.abbr}")
        
        self.allocated_slots = new_slots
    
    def _add_split_info_to_descriptions(self):
        """Add split information to descriptions for split sectors"""
        # Group slots by sector
        sector_slots = {}
        for slot in self.allocated_slots:
            if slot.sector is None:
                continue  # Skip slots without sector reference
            
            sector_id = slot.sector.abbr
            if sector_id not in sector_slots:
                sector_slots[sector_id] = []
            sector_slots[sector_id].append(slot)
        
        # Add split information to descriptions
        for sector_id, slots in sector_slots.items():
            # Group slots by sequence number (maxload chunk)
            chunk_groups = {}
            for slot in slots:
                sequence_number = slot.split if slot.split is not None else 1
                if sequence_number not in chunk_groups:
                    chunk_groups[sequence_number] = []
                chunk_groups[sequence_number].append(slot)
            
            # Count total maxload chunks
            total_chunks = len(chunk_groups)
            
            # Update descriptions with split information
            for sequence_number, chunk_slots in sorted(chunk_groups.items()):
                # Check if this chunk is split across multiple time slots
                is_split_chunk = len(chunk_slots) > 1
                
                for i, slot in enumerate(chunk_slots):
                    current_chunk = sequence_number
                    
                    # Remove any existing ellipsis or split info
                    base_description = slot.description.split('...')[0].split(' (')[0]
                    
                    if current_chunk < total_chunks:
                        # Non-final chunk: add ellipsis
                        if is_split_chunk:
                            # Split chunk: add sequence number
                            slot.description = f"{base_description}... ({current_chunk}/{total_chunks} #{i+1})"
                        else:
                            # Single slot chunk: no sequence number
                            slot.description = f"{base_description}... ({current_chunk}/{total_chunks})"
                    else:
                        # Final chunk: no ellipsis
                        if is_split_chunk:
                            # Split chunk: add sequence number
                            slot.description = f"{base_description} ({current_chunk}/{total_chunks} #{i+1})"
                        else:
                            # Single slot chunk: no sequence number
                            slot.description = f"{base_description} ({current_chunk}/{total_chunks})"
    
    def _create_allocated_slot(self, original_slot: TimeSlot, duration: int, sector: Sector, sequence_number: int) -> TimeSlot:
        """Create a new allocated time slot"""
        # Ensure the end time doesn't exceed the original slot's end time
        calculated_end = original_slot.start.add_minutes(duration)
        end_time = min(calculated_end, original_slot.end)
        
        # Adjust duration if it was truncated
        actual_duration = end_time.to_minutes() - original_slot.start.to_minutes()
        
        # Create base description (split info will be added later)
        description = f"{sector.abbr}: {sector.description}"
        
        return TimeSlot(
            id=original_slot.id,  # Preserve the original ID if it exists
            seq=original_slot.seq,  # Changed from order to seq
            start=original_slot.start,
            duration=actual_duration,
            end=end_time,
            slot_type=original_slot.slot_type,
            description=description,
            original_index=original_slot.original_index,
            user=original_slot.user,  # Preserve user reference
            sector=sector,  # Add the sector reference
            split=sequence_number  # Store the sequence number from maxload division
        )