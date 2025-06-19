import unittest
from unittest.mock import Mock, patch
from src.scheduler import Scheduler
from src.models import TimeSlot, Sector, SectorAllocation
from src.time_slots import TimeSlots
from src.sectors import Sectors


class TestScheduler(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.scheduler = Scheduler()
        
        # Create sample time slots
        self.sample_slots = [
            TimeSlot(order=1, start="06:35", duration=25, end="07:00", 
                    slot_type="A0-L", description="", original_index=0),
            TimeSlot(order=2, start="07:00", duration=60, end="08:00", 
                    slot_type="Reserved", description="Prepare", original_index=1),
            TimeSlot(order=3, start="08:00", duration=25, end="08:25", 
                    slot_type="FEM", description="", original_index=2),
            TimeSlot(order=4, start="08:25", duration=15, end="08:40", 
                    slot_type="Traffic", description="Ride to Station", original_index=3),
            TimeSlot(order=5, start="08:40", duration=45, end="09:25", 
                    slot_type="A1-T", description="@Train", original_index=4),
        ]
        
        # Create sample sectors
        self.sample_sectors = [
            Sector(sector_id="8", occupy="50%", weight=100, abbr="TAX", 
                  description="税务应用/公司事项"),
            Sector(sector_id="6", occupy="50%", weight=100, abbr="MIS", 
                  description="企业管理软件/WebApp"),
        ]
        
        self.time_slots = TimeSlots(self.sample_slots)
        self.sectors = Sectors(self.sample_sectors)
    
    def test_init(self):
        """Test Scheduler initialization."""
        self.assertEqual(self.scheduler.allocated_slots, [])
    
    def test_calculate_sector_allocations(self):
        """Test _calculate_sector_allocations method."""
        total_available_time = 70  # 25 + 45 minutes
        total_weight = 200  # 100 + 100
        
        allocations = self.scheduler._calculate_sector_allocations(
            self.sectors, total_available_time, total_weight
        )
        
        self.assertEqual(len(allocations), 2)
        
        # Check first allocation (TAX: 100/200 = 50% -> 35 minutes)
        self.assertEqual(allocations[0].sector.abbr, "TAX")
        self.assertEqual(allocations[0].target_duration, 35)
        self.assertEqual(allocations[0].allocated_parts, [])
        
        # Check second allocation (MIS: 100/200 = 50% -> 35 minutes)
        self.assertEqual(allocations[1].sector.abbr, "MIS")
        self.assertEqual(allocations[1].target_duration, 35)
        self.assertEqual(allocations[1].allocated_parts, [])
    
    def test_calculate_sector_allocations_uneven_weights(self):
        """Test _calculate_sector_allocations with uneven weights."""
        sectors = Sectors([
            Sector(sector_id="1", occupy="75%", weight=75, abbr="HIGH", description="High weight"),
            Sector(sector_id="2", occupy="25%", weight=25, abbr="LOW", description="Low weight"),
        ])
        
        total_available_time = 100
        total_weight = 100
        
        allocations = self.scheduler._calculate_sector_allocations(
            sectors, total_available_time, total_weight
        )
        
        # HIGH: 75/100 = 75% -> 75 minutes
        self.assertEqual(allocations[0].target_duration, 75)
        # LOW: 25/100 = 25% -> 25 minutes
        self.assertEqual(allocations[1].target_duration, 25)
    
    def test_create_allocated_slot(self):
        """Test _create_allocated_slot method."""
        original_slot = TimeSlot(order=1, start="06:35", duration=25, end="07:00", 
                                slot_type="A0-L", description="", original_index=0)
        sector = Sector(sector_id="8", occupy="50%", weight=100, abbr="TAX", 
                       description="税务应用/公司事项")
        
        allocated_slot = self.scheduler._create_allocated_slot(original_slot, 20, sector)
        
        self.assertEqual(allocated_slot.order, 1)
        self.assertEqual(allocated_slot.start, "06:35")
        self.assertEqual(allocated_slot.duration, 20)
        self.assertEqual(allocated_slot.end, "06:55")  # 06:35 + 20 minutes
        self.assertEqual(allocated_slot.slot_type, "A0-L")
        self.assertEqual(allocated_slot.description, "TAX: 税务应用/公司事项")
        self.assertEqual(allocated_slot.original_index, 0)
    
    def test_allocate_sectors_proportionally(self):
        """Test allocate_sectors_proportionally method."""
        # Mock the time_slots methods
        with patch.object(self.time_slots, 'get_available_slots') as mock_get_available:
            with patch.object(self.time_slots, 'get_total_available_time') as mock_get_total:
                with patch.object(self.time_slots, 'get_slots') as mock_get_slots:
                    with patch.object(self.time_slots, 'replace_available_slots') as mock_replace:
                        
                        # Setup mocks
                        available_slots = [
                            TimeSlot(order=1, start="06:35", duration=25, end="07:00", 
                                    slot_type="A0-L", description="", original_index=0),
                            TimeSlot(order=5, start="08:40", duration=45, end="09:25", 
                                    slot_type="A1-T", description="@Train", original_index=4),
                        ]
                        mock_get_available.return_value = available_slots
                        mock_get_total.return_value = 70  # 25 + 45
                        mock_get_slots.return_value = self.sample_slots
                        
                        # Call the method
                        result = self.scheduler.allocate_sectors_proportionally(self.time_slots, self.sectors)
                        
                        # Verify mocks were called
                        mock_get_available.assert_called_once()
                        mock_get_total.assert_called_once()
                        mock_get_slots.assert_called_once()
                        mock_replace.assert_called_once()
                        
                        # Verify result is a TimeSlots instance
                        self.assertIsInstance(result, TimeSlots)
    
    def test_allocate_sectors_with_insufficient_time(self):
        """Test allocation when there's insufficient time for all sectors."""
        # Create sectors that need more time than available
        large_sectors = Sectors([
            Sector(sector_id="1", occupy="50%", weight=100, abbr="LARGE1", description="Large sector 1"),
            Sector(sector_id="2", occupy="50%", weight=100, abbr="LARGE2", description="Large sector 2"),
        ])
        
        # Only 70 minutes available, but sectors need more
        available_slots = [
            TimeSlot(order=1, start="06:35", duration=25, end="07:00", 
                    slot_type="A0-L", description="", original_index=0),
            TimeSlot(order=5, start="08:40", duration=45, end="09:25", 
                    slot_type="A1-T", description="@Train", original_index=4),
        ]
        
        # This should not raise an exception, but should log warnings
        with patch('logging.warning') as mock_warning:
            self.scheduler._allocate_sectors(
                [SectorAllocation(large_sectors.sectors[0], 100, []),
                 SectorAllocation(large_sectors.sectors[1], 100, [])],
                available_slots
            )
            
            # Should log warnings about insufficient time
            self.assertTrue(mock_warning.called)
    
    def test_allocate_sectors_with_zero_duration_slot(self):
        """Test allocation with a slot that has zero duration."""
        available_slots = [
            TimeSlot(order=1, start="06:35", duration=0, end="06:35", 
                    slot_type="A0-L", description="", original_index=0),
        ]
        
        allocation = SectorAllocation(self.sample_sectors[0], 10, [])
        
        with self.assertRaises(ValueError):
            self.scheduler._allocate_sectors([allocation], available_slots)
    
    def test_allocate_sectors_with_negative_duration_slot(self):
        """Test allocation with a slot that has negative duration."""
        available_slots = [
            TimeSlot(order=1, start="06:35", duration=-5, end="06:30", 
                    slot_type="A0-L", description="", original_index=0),
        ]
        
        allocation = SectorAllocation(self.sample_sectors[0], 10, [])
        
        with self.assertRaises(ValueError):
            self.scheduler._allocate_sectors([allocation], available_slots)
    
    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Test with empty sectors
        empty_sectors = Sectors([])
        result = self.scheduler.allocate_sectors_proportionally(self.time_slots, empty_sectors)
        self.assertIsInstance(result, TimeSlots)
        
        # Test with empty time slots
        empty_time_slots = TimeSlots([])
        result = self.scheduler.allocate_sectors_proportionally(empty_time_slots, self.sectors)
        self.assertIsInstance(result, TimeSlots)
        
        # Test with zero weight sectors
        zero_weight_sectors = Sectors([
            Sector(sector_id="1", occupy="0%", weight=0, abbr="ZERO", description="Zero weight"),
        ])
        
        with self.assertRaises(ZeroDivisionError):
            self.scheduler._calculate_sector_allocations(zero_weight_sectors, 100, 0)


if __name__ == '__main__':
    unittest.main() 