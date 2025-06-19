import unittest
import tempfile
import os
import csv
from unittest.mock import patch, mock_open
from src.time_slots import TimeSlots
from src.models import TimeSlot


class TestTimeSlots(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        # Create sample time slots for testing
        self.sample_slots = [
            TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "", 0),
            TimeSlot.from_strings(2, "07:00", 60, "08:00", "Reserved", "Prepare", 1),
            TimeSlot.from_strings(3, "08:00", 25, "08:25", "FEM", "", 2),
            TimeSlot.from_strings(4, "08:25", 15, "08:40", "Traffic", "Ride to Station", 3),
            TimeSlot.from_strings(5, "08:40", 45, "09:25", "A1-T", "@Train", 4),
            TimeSlot.from_strings(6, "09:25", 15, "09:40", "Traffic", "Walk to work", 5),
        ]
        
        self.time_slots = TimeSlots(self.sample_slots)
        self.temp_dir = tempfile.mkdtemp()
    
    def tearDown(self):
        """Clean up after each test method."""
        if os.path.exists(self.temp_dir):
            import shutil
            shutil.rmtree(self.temp_dir)
    
    def test_init_empty(self):
        """Test initialization with no slots."""
        empty_time_slots = TimeSlots()
        self.assertEqual(len(empty_time_slots), 0)
        self.assertEqual(empty_time_slots.slots, [])
    
    def test_init_with_slots(self):
        """Test initialization with provided slots."""
        self.assertEqual(len(self.time_slots), 6)
        self.assertEqual(self.time_slots.slots, self.sample_slots)
    
    def test_get_available_slots(self):
        """Test get_available_slots method."""
        available_slots = self.time_slots.get_available_slots()
        
        # Should return 2 available slots (A0-L and A1-T)
        self.assertEqual(len(available_slots), 2)
        
        # Check that only slots starting with 'A' are returned
        for slot in available_slots:
            self.assertTrue(slot.is_available())
        
        # Check specific slots
        slot_types = [slot.slot_type for slot in available_slots]
        self.assertIn("A0-L", slot_types)
        self.assertIn("A1-T", slot_types)
    
    def test_get_total_available_time(self):
        """Test get_total_available_time method."""
        # Available slots: A0-L (25 min) + A1-T (45 min) = 70 minutes
        total_time = self.time_slots.get_total_available_time()
        self.assertEqual(total_time, 70)
    
    def test_count_available_slots(self):
        """Test count_available_slots method."""
        count = self.time_slots.count_available_slots()
        self.assertEqual(count, 2)
    
    def test_get_slots(self):
        """Test get_slots method."""
        slots = self.time_slots.get_slots()
        self.assertEqual(slots, self.sample_slots)
    
    def test_add_slot(self):
        """Test add_slot method."""
        new_slot = TimeSlot.from_strings(7, "09:40", 10, "09:50", "A2-Minor", "New slot", 6)
        
        initial_count = len(self.time_slots)
        self.time_slots.add_slot(new_slot)
        
        self.assertEqual(len(self.time_slots), initial_count + 1)
        self.assertIn(new_slot, self.time_slots.slots)
    
    def test_remove_slot(self):
        """Test remove_slot method."""
        slot_to_remove = self.sample_slots[0]
        initial_count = len(self.time_slots)
        
        self.time_slots.remove_slot(slot_to_remove)
        
        self.assertEqual(len(self.time_slots), initial_count - 1)
        self.assertNotIn(slot_to_remove, self.time_slots.slots)
    
    def test_len(self):
        """Test __len__ method."""
        self.assertEqual(len(self.time_slots), 6)
    
    def test_iter(self):
        """Test __iter__ method."""
        slots_list = list(self.time_slots)
        self.assertEqual(slots_list, self.sample_slots)
    
    def test_replace_available_slots(self):
        """Test replace_available_slots method."""
        # Create new allocated slots
        allocated_slots = [
            TimeSlot.from_strings(1, "06:35", 30, "07:05", "B1-Work", "Allocated work", 0),
            TimeSlot.from_strings(5, "08:40", 60, "09:40", "B2-Meeting", "Allocated meeting", 4),
        ]
        
        # Get original available indices
        original_available_indices = {slot.original_index for slot in self.time_slots.get_available_slots()}
        
        self.time_slots.replace_available_slots(allocated_slots)
        
        # Check that available slots were replaced
        for slot in self.time_slots.slots:
            if slot.original_index in original_available_indices:
                self.assertFalse(slot.is_available())
        
        # Check that allocated slots are present
        allocated_slot_types = [slot.slot_type for slot in allocated_slots]
        for slot in self.time_slots.slots:
            if slot.slot_type in allocated_slot_types:
                self.assertIn(slot, allocated_slots)
    
    def test_from_csv_simple(self):
        """Test from_csv method with simple CSV."""
        csv_content = """Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare
3,08:00,25,08:25,FEM,"""
        
        csv_file = os.path.join(self.temp_dir, "timetable.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        time_slots = TimeSlots.from_csv(csv_file)
        
        self.assertEqual(len(time_slots), 3)
        self.assertTrue(all(isinstance(slot, TimeSlot) for slot in time_slots.slots))
        
        # Check first slot
        first_slot = time_slots.slots[0]
        self.assertEqual(first_slot.order, 1)
        self.assertEqual(first_slot.start.to_string(), '06:35')
        self.assertEqual(first_slot.duration, 25)
        self.assertEqual(first_slot.end.to_string(), '07:00')
        self.assertEqual(first_slot.slot_type, 'A0-L')
        self.assertEqual(first_slot.description, '')
        self.assertEqual(first_slot.original_index, 0)
    
    def test_from_csv_with_leading_lines_and_comments(self):
        """Test from_csv method with leading lines and comments."""
        csv_content = """# Timetable file
# Version 1.0
Some header info
Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
# Comment line
2,07:00,60,08:00,Reserved,Prepare
3,08:00,25,08:25,FEM,"""
        
        csv_file = os.path.join(self.temp_dir, "timetable.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        time_slots = TimeSlots.from_csv(csv_file)
        
        self.assertEqual(len(time_slots), 2)  # Comment row should be skipped
        self.assertEqual(time_slots.slots[0].order, 1)
        self.assertEqual(time_slots.slots[1].order, 3)  # Row 2 was a comment
    
    def test_from_csv_file_not_found(self):
        """Test from_csv method with file not found."""
        with self.assertRaises(FileNotFoundError):
            TimeSlots.from_csv('nonexistent_file.csv')
    
    def test_to_csv_success(self):
        """Test to_csv method with successful file writing."""
        with tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.csv') as temp_file:
            temp_filename = temp_file.name
        
        try:
            self.time_slots.to_csv(temp_filename)
            
            # Verify the file was created and contains expected data
            self.assertTrue(os.path.exists(temp_filename))
            
            with open(temp_filename, 'r', newline='', encoding='utf-8') as file:
                reader = csv.reader(file)
                rows = list(reader)
                
                # Check header
                self.assertEqual(rows[0], ['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
                
                # Check data rows
                self.assertEqual(len(rows), 7)  # Header + 6 data rows
                
                # Check first data row
                self.assertEqual(rows[1], ['1', '06:35', '25', '07:00', 'A0-L', ''])
                
                # Check second data row
                self.assertEqual(rows[2], ['2', '07:00', '60', '08:00', 'Reserved', 'Prepare'])
        
        finally:
            # Clean up
            if os.path.exists(temp_filename):
                os.unlink(temp_filename)
    
    def test_to_csv_empty_slots(self):
        """Test to_csv method with empty slots."""
        empty_time_slots = TimeSlots()
        
        with tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.csv') as temp_file:
            temp_filename = temp_file.name
        
        try:
            # Should not raise an exception
            empty_time_slots.to_csv(temp_filename)
            
            # File should not be created or should be empty
            if os.path.exists(temp_filename):
                with open(temp_filename, 'r') as file:
                    content = file.read()
                    self.assertEqual(content, '')
        
        finally:
            # Clean up
            if os.path.exists(temp_filename):
                os.unlink(temp_filename)
    
    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Test with empty slot list
        empty_time_slots = TimeSlots([])
        self.assertEqual(empty_time_slots.get_available_slots(), [])
        self.assertEqual(empty_time_slots.get_total_available_time(), 0)
        self.assertEqual(empty_time_slots.count_available_slots(), 0)
        
        # Test with only non-available slots
        non_available_slots = [
            TimeSlot.from_strings(1, "06:35", 25, "07:00", "Reserved", "", 0),
            TimeSlot.from_strings(2, "07:00", 60, "08:00", "FEM", "", 1),
        ]
        time_slots_no_available = TimeSlots(non_available_slots)
        self.assertEqual(time_slots_no_available.get_available_slots(), [])
        self.assertEqual(time_slots_no_available.get_total_available_time(), 0)
        self.assertEqual(time_slots_no_available.count_available_slots(), 0)
        
        # Test with only available slots
        available_only_slots = [
            TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "", 0),
            TimeSlot.from_strings(2, "07:00", 60, "08:00", "A1-T", "", 1),
        ]
        time_slots_all_available = TimeSlots(available_only_slots)
        self.assertEqual(len(time_slots_all_available.get_available_slots()), 2)
        self.assertEqual(time_slots_all_available.get_total_available_time(), 85)
        self.assertEqual(time_slots_all_available.count_available_slots(), 2)


if __name__ == '__main__':
    unittest.main() 