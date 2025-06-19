import unittest
from src.models import TimeSlot, Sector, SectorAllocation
from src.time_utils import Time


class TestTimeSlot(unittest.TestCase):
    
    def test_time_slot_creation(self):
        """Test TimeSlot dataclass creation and attributes."""
        start_time = Time(6, 35)
        end_time = Time(7, 0)
        
        slot = TimeSlot(
            order=1,
            start=start_time,
            duration=25,
            end=end_time,
            slot_type="A0-L",
            description="Test slot",
            original_index=0
        )
        
        self.assertEqual(slot.order, 1)
        self.assertEqual(slot.start, start_time)
        self.assertEqual(slot.duration, 25)
        self.assertEqual(slot.end, end_time)
        self.assertEqual(slot.slot_type, "A0-L")
        self.assertEqual(slot.description, "Test slot")
        self.assertEqual(slot.original_index, 0)
    
    def test_from_strings(self):
        """Test TimeSlot.from_strings method."""
        slot = TimeSlot.from_strings(
            order=1,
            start="06:35",
            duration=25,
            end="07:00",
            slot_type="A0-L",
            description="Test slot",
            original_index=0
        )
        
        self.assertEqual(slot.order, 1)
        self.assertEqual(slot.start, Time(6, 35))
        self.assertEqual(slot.duration, 25)
        self.assertEqual(slot.end, Time(7, 0))
        self.assertEqual(slot.slot_type, "A0-L")
        self.assertEqual(slot.description, "Test slot")
        self.assertEqual(slot.original_index, 0)
    
    def test_to_dict(self):
        """Test TimeSlot.to_dict method."""
        slot = TimeSlot.from_strings(
            order=1,
            start="06:35",
            duration=25,
            end="07:00",
            slot_type="A0-L",
            description="Test slot",
            original_index=0
        )
        
        slot_dict = slot.to_dict()
        expected_dict = {
            'order': 1,
            'start': '06:35',
            'duration': 25,
            'end': '07:00',
            'slot_type': 'A0-L',
            'description': 'Test slot',
            'original_index': 0
        }
        
        self.assertEqual(slot_dict, expected_dict)
    
    def test_is_available_available_slots(self):
        """Test is_available method with available slot types."""
        # Test available slots (starting with 'A')
        available_slot = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        self.assertTrue(available_slot.is_available())
        
        available_slot2 = TimeSlot.from_strings(5, "08:40", 45, "09:25", "A1-T", "@Train", 4)
        self.assertTrue(available_slot2.is_available())
        
        available_slot3 = TimeSlot.from_strings(10, "12:30", 30, "13:00", "A3-N", "", 9)
        self.assertTrue(available_slot3.is_available())
    
    def test_is_available_non_available_slots(self):
        """Test is_available method with non-available slot types."""
        # Test non-available slots
        reserved_slot = TimeSlot.from_strings(2, "07:00", 60, "08:00", "Reserved", "Prepare", 1)
        self.assertFalse(reserved_slot.is_available())
        
        fem_slot = TimeSlot.from_strings(3, "08:00", 25, "08:25", "FEM", "", 2)
        self.assertFalse(fem_slot.is_available())
        
        traffic_slot = TimeSlot.from_strings(4, "08:25", 15, "08:40", "Traffic", "Ride to Station", 3)
        self.assertFalse(traffic_slot.is_available())
        
        break_slot = TimeSlot.from_strings(12, "13:00", 60, "14:00", "Break/Sleep", "Nap", 11)
        self.assertFalse(break_slot.is_available())
    
    def test_time_slot_equality(self):
        """Test TimeSlot equality comparison."""
        slot1 = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        slot2 = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        slot3 = TimeSlot.from_strings(2, "06:35", 25, "07:00", "A0-L", "Test", 0)
        
        self.assertEqual(slot1, slot2)
        self.assertNotEqual(slot1, slot3)
    
    def test_time_slot_repr(self):
        """Test TimeSlot string representation."""
        slot = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        repr_str = repr(slot)
        
        self.assertIn("TimeSlot", repr_str)
        self.assertIn("order=1", repr_str)
        self.assertIn("start=Time(6, 35)", repr_str)


class TestSector(unittest.TestCase):
    
    def test_sector_creation(self):
        """Test Sector dataclass creation and attributes."""
        sector = Sector(
            sector_id="1",
            occupy=33.7,
            weight=100,
            abbr="TAX",
            description="税务应用/公司事项"
        )
        
        self.assertEqual(sector.sector_id, "1")
        self.assertEqual(sector.occupy, 33.7)
        self.assertEqual(sector.weight, 100)
        self.assertEqual(sector.abbr, "TAX")
        self.assertEqual(sector.description, "税务应用/公司事项")
    
    def test_from_strings(self):
        """Test Sector.from_strings method."""
        sector = Sector.from_strings(
            sector_id="1",
            occupy="33.7%",
            weight=100,
            abbr="TAX",
            description="税务应用/公司事项"
        )
        
        self.assertEqual(sector.sector_id, "1")
        self.assertEqual(sector.occupy, 33.7)
        self.assertEqual(sector.weight, 100)
        self.assertEqual(sector.abbr, "TAX")
        self.assertEqual(sector.description, "税务应用/公司事项")
    
    def test_to_dict(self):
        """Test Sector.to_dict method."""
        sector = Sector.from_strings(
            sector_id="1",
            occupy="33.7%",
            weight=100,
            abbr="TAX",
            description="税务应用/公司事项"
        )
        
        sector_dict = sector.to_dict()
        expected_dict = {
            'sector_id': '1',
            'occupy': '33.7%',
            'weight': 100,
            'abbr': 'TAX',
            'description': '税务应用/公司事项'
        }
        
        self.assertEqual(sector_dict, expected_dict)
    
    def test_to_dict_whole_number(self):
        """Test Sector.to_dict with whole number percentage."""
        sector = Sector(
            sector_id="1",
            occupy=50.0,
            weight=100,
            abbr="TEST",
            description="Test"
        )
        
        sector_dict = sector.to_dict()
        self.assertEqual(sector_dict['occupy'], '50%')  # Should remove trailing .0
    
    def test_sector_equality(self):
        """Test Sector equality comparison."""
        sector1 = Sector("1", 33.7, 100, "TAX", "Test")
        sector2 = Sector("1", 33.7, 100, "TAX", "Test")
        sector3 = Sector("2", 33.7, 100, "TAX", "Test")
        
        self.assertEqual(sector1, sector2)
        self.assertNotEqual(sector1, sector3)
    
    def test_sector_repr(self):
        """Test Sector string representation."""
        sector = Sector("1", 33.7, 100, "TAX", "Test")
        repr_str = repr(sector)
        
        self.assertIn("Sector", repr_str)
        self.assertIn("sector_id='1'", repr_str)
        self.assertIn("weight=100", repr_str)


class TestSectorAllocation(unittest.TestCase):
    
    def test_sector_allocation_creation(self):
        """Test SectorAllocation dataclass creation and attributes."""
        sector = Sector("1", 33.7, 100, "TAX", "Test")
        time_slot = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        
        allocation = SectorAllocation(
            sector=sector,
            target_duration=100,
            allocated_parts=[time_slot]
        )
        
        self.assertEqual(allocation.sector, sector)
        self.assertEqual(allocation.target_duration, 100)
        self.assertEqual(allocation.allocated_parts, [time_slot])
    
    def test_sector_allocation_equality(self):
        """Test SectorAllocation equality comparison."""
        sector = Sector("1", 33.7, 100, "TAX", "Test")
        time_slot = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        
        allocation1 = SectorAllocation(sector, 100, [time_slot])
        allocation2 = SectorAllocation(sector, 100, [time_slot])
        allocation3 = SectorAllocation(sector, 200, [time_slot])
        
        self.assertEqual(allocation1, allocation2)
        self.assertNotEqual(allocation1, allocation3)
    
    def test_sector_allocation_repr(self):
        """Test SectorAllocation string representation."""
        sector = Sector("1", 33.7, 100, "TAX", "Test")
        time_slot = TimeSlot.from_strings(1, "06:35", 25, "07:00", "A0-L", "Test", 0)
        
        allocation = SectorAllocation(sector, 100, [time_slot])
        repr_str = repr(allocation)
        
        self.assertIn("SectorAllocation", repr_str)
        self.assertIn("target_duration=100", repr_str)


if __name__ == '__main__':
    unittest.main() 