import unittest
from src.time_slot import TimeSlot
from src.user import User
from src.sector import Sector
from src.sector_allocation import SectorAllocation
from src.time_utils import Time


class TestUser(unittest.TestCase):
    """Test User dataclass creation and attributes."""
    
    def test_user_creation(self):
        """Test User dataclass creation and attributes."""
        user = User(
            name="john_doe",
            display_name="John Doe"
        )
        
        self.assertEqual(user.name, "john_doe")
        self.assertEqual(user.display_name, "John Doe")
        self.assertIsNone(user.id)
        
        # Test with id
        user_with_id = User(
            name="jane_doe",
            display_name="Jane Doe",
            id="user456"
        )
        self.assertEqual(user_with_id.name, "jane_doe")
        self.assertEqual(user_with_id.display_name, "Jane Doe")
        self.assertEqual(user_with_id.id, "user456")
    
    def test_user_from_dict(self):
        """Test User.from_dict method."""
        user_data = {
            'name': 'john_doe',
            'display_name': 'John Doe'
        }
        user = User.from_dict(user_data)
        
        self.assertEqual(user.name, "john_doe")
        self.assertEqual(user.display_name, "John Doe")
        self.assertIsNone(user.id)
        
        # Test with id
        user_data_with_id = {
            'name': 'jane_doe',
            'display_name': 'Jane Doe',
            'id': 'user456'
        }
        user_with_id = User.from_dict(user_data_with_id)
        
        self.assertEqual(user_with_id.name, "jane_doe")
        self.assertEqual(user_with_id.display_name, "Jane Doe")
        self.assertEqual(user_with_id.id, "user456")
    
    def test_user_to_dict(self):
        """Test User.to_dict method."""
        user = User(name="john_doe", display_name="John Doe")
        user_dict = user.to_dict()
        
        expected_dict = {
            'name': 'john_doe',
            'display_name': 'John Doe'
        }
        self.assertEqual(user_dict, expected_dict)
        
        # Test with id
        user_with_id = User(name="jane_doe", display_name="Jane Doe", id="user456")
        user_dict_with_id = user_with_id.to_dict()
        
        expected_dict_with_id = {
            'name': 'jane_doe',
            'display_name': 'Jane Doe',
            'id': 'user456'
        }
        self.assertEqual(user_dict_with_id, expected_dict_with_id)


class TestTimeSlot(unittest.TestCase):
    """Test TimeSlot dataclass creation and attributes."""
    
    def test_timeslot_creation(self):
        """Test TimeSlot dataclass creation and attributes."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        
        slot = TimeSlot(
            id="slot123",
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="A0-L",
            description="Morning work",
            original_index=0,
            user=user,
            sector=sector
        )
        
        self.assertEqual(slot.id, "slot123")
        self.assertEqual(slot.seq, 1)
        self.assertEqual(slot.start, Time(6, 35))
        self.assertEqual(slot.duration, 25)
        self.assertEqual(slot.end, Time(7, 0))
        self.assertEqual(slot.slot_type, "A0-L")
        self.assertEqual(slot.description, "Morning work")
        self.assertEqual(slot.original_index, 0)
        self.assertEqual(slot.user, user)
        self.assertEqual(slot.sector, sector)
    
    def test_timeslot_from_strings(self):
        """Test TimeSlot.from_strings method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        
        slot = TimeSlot.from_strings(
            seq=1,
            start="06:35",
            duration=25,
            end="07:00",
            slot_type="A0-L",
            description="Morning work",
            original_index=0,
            user=user,
            sector=sector,
            id="slot123"
        )
        
        self.assertEqual(slot.id, "slot123")
        self.assertEqual(slot.seq, 1)
        self.assertEqual(slot.start, Time(6, 35))
        self.assertEqual(slot.duration, 25)
        self.assertEqual(slot.end, Time(7, 0))
        self.assertEqual(slot.slot_type, "A0-L")
        self.assertEqual(slot.description, "Morning work")
        self.assertEqual(slot.original_index, 0)
        self.assertEqual(slot.user, user)
        self.assertEqual(slot.sector, sector)
    
    def test_timeslot_from_dict(self):
        """Test TimeSlot.from_dict method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        
        slot_data = {
            'id': 'slot123',
            'seq': 1,
            'start': '06:35',
            'duration': 25,
            'end': '07:00',
            'slot_type': 'A0-L',
            'description': 'Morning work',
            'original_index': 0,
            'sector': {
                'seq': '1',
                'ratio': '33.7%',
                'weight': 100,
                'abbr': 'TAX',
                'description': 'Tax work'
            }
        }
        
        slot = TimeSlot.from_dict(slot_data, user, sector)
        
        self.assertEqual(slot.id, "slot123")
        self.assertEqual(slot.seq, 1)
        self.assertEqual(slot.start, Time(6, 35))
        self.assertEqual(slot.duration, 25)
        self.assertEqual(slot.end, Time(7, 0))
        self.assertEqual(slot.slot_type, "A0-L")
        self.assertEqual(slot.description, "Morning work")
        self.assertEqual(slot.original_index, 0)
        self.assertEqual(slot.user, user)
        self.assertEqual(slot.sector, sector)
    
    def test_timeslot_to_dict(self):
        """Test TimeSlot.to_dict method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        
        slot = TimeSlot(
            id="slot123",
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="A0-L",
            description="Morning work",
            original_index=0,
            user=user,
            sector=sector
        )
        
        slot_dict = slot.to_dict()
        expected_dict = {
            'id': 'slot123',
            'user_id': 'user123',
            'seq': 1,
            'start': '06:35',
            'duration': 25,
            'end': '07:00',
            'slot_type': 'A0-L',
            'description': 'Morning work',
            'original_index': 0,
            'sector': {
                'seq': '1',
                'ratio': '33.7%',
                'weight': 100,
                'abbr': 'TAX',
                'description': 'Tax work',
                'user_id': 'user123'
            }
        }
        self.assertEqual(slot_dict, expected_dict)
    
    def test_timeslot_is_available(self):
        """Test TimeSlot.is_available method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        
        # Available slot
        available_slot = TimeSlot(
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="A0-L",
            description="Available",
            original_index=0,
            user=user
        )
        self.assertTrue(available_slot.is_available())
        
        # Non-available slot
        non_available_slot = TimeSlot(
            seq=2,
            start=Time(7, 0),
            duration=30,
            end=Time(7, 30),
            slot_type="B1",
            description="Not available",
            original_index=1,
            user=user
        )
        self.assertFalse(non_available_slot.is_available())
    
    def test_timeslot_is_generated(self):
        """Test TimeSlot.is_generated method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        
        # Generated slot
        generated_slot = TimeSlot(
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="Break/Load",
            description="Break",
            original_index=0,
            user=user
        )
        self.assertTrue(generated_slot.is_generated())
        
        # Non-generated slot
        non_generated_slot = TimeSlot(
            seq=2,
            start=Time(7, 0),
            duration=30,
            end=Time(7, 30),
            slot_type="A0-L",
            description="Not generated",
            original_index=1,
            user=user
        )
        self.assertFalse(non_generated_slot.is_generated())


class TestSector(unittest.TestCase):
    """Test Sector dataclass creation and attributes."""
    
    def test_sector_creation(self):
        """Test Sector dataclass creation and attributes."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(
            id="sector123",
            seq="1",
            ratio=33.7,
            weight=100,
            abbr="TAX",
            description="Tax work",
            user=user
        )
        
        self.assertEqual(sector.id, "sector123")
        self.assertEqual(sector.seq, "1")
        self.assertEqual(sector.ratio, 33.7)
        self.assertEqual(sector.weight, 100)
        self.assertEqual(sector.abbr, "TAX")
        self.assertEqual(sector.description, "Tax work")
        self.assertEqual(sector.user, user)
    
    def test_sector_from_strings(self):
        """Test Sector.from_strings method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector.from_strings(
            seq="1",
            ratio="33.7%",
            weight=100,
            abbr="TAX",
            description="Tax work",
            user=user,
            id="sector123"
        )
        
        self.assertEqual(sector.id, "sector123")
        self.assertEqual(sector.seq, "1")
        self.assertEqual(sector.ratio, 33.7)
        self.assertEqual(sector.weight, 100)
        self.assertEqual(sector.abbr, "TAX")
        self.assertEqual(sector.description, "Tax work")
        self.assertEqual(sector.user, user)
    
    def test_sector_to_dict(self):
        """Test Sector.to_dict method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(
            id="sector123",
            seq="1",
            ratio=33.7,
            weight=100,
            abbr="TAX",
            description="Tax work",
            user=user
        )
        
        sector_dict = sector.to_dict()
        expected_dict = {
            'id': 'sector123',
            'user_id': 'user123',
            'seq': '1',
            'ratio': '33.7%',
            'weight': 100,
            'abbr': 'TAX',
            'description': 'Tax work'
        }
        self.assertEqual(sector_dict, expected_dict)
    
    def test_sector_to_dict_with_whole_number_percentage(self):
        """Test Sector.to_dict with whole number percentage."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(
            id="sector123",
            seq="1",
            ratio=50.0,
            weight=100,
            abbr="TAX",
            description="Tax work",
            user=user
        )
        
        sector_dict = sector.to_dict()
        self.assertEqual(sector_dict['ratio'], '50.0%')
    
    def test_sector_repr(self):
        """Test Sector string representation."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(
            id="sector123",
            seq="1",
            ratio=33.7,
            weight=100,
            abbr="TAX",
            description="Tax work",
            user=user
        )
        
        repr_str = repr(sector)
        
        self.assertIn("Sector", repr_str)
        self.assertIn("seq='1'", repr_str)
        self.assertIn("weight=100", repr_str)


class TestSectorAllocation(unittest.TestCase):
    """Test SectorAllocation dataclass creation and attributes."""
    
    def test_sector_allocation_creation(self):
        """Test SectorAllocation dataclass creation and attributes."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        time_slot = TimeSlot(
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="A0-L",
            description="Morning work",
            original_index=0,
            user=user,
            sector=sector
        )
        
        allocation = SectorAllocation(
            sector=sector,
            target_duration=120,
            allocated_parts=[time_slot]
        )
        
        self.assertEqual(allocation.sector, sector)
        self.assertEqual(allocation.target_duration, 120)
        self.assertEqual(allocation.allocated_parts, [time_slot])
    
    def test_sector_allocation_from_dict(self):
        """Test SectorAllocation.from_dict method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        
        allocation_data = {
            'sector': {
                'id': 'sector123',
                'seq': '1',
                'ratio': '33.7%',
                'weight': 100,
                'abbr': 'TAX',
                'description': 'Tax work',
                'user_id': 'user123'
            },
            'target_duration': 120,
            'allocated_parts': [{
                'id': 'slot123',
                'seq': 1,
                'start': '06:35',
                'duration': 25,
                'end': '07:00',
                'slot_type': 'A0-L',
                'description': 'Morning work',
                'original_index': 0,
                'user_id': 'user123',
                'sector': {
                    'seq': '1',
                    'ratio': '33.7%',
                    'weight': 100,
                    'abbr': 'TAX',
                    'description': 'Tax work'
                }
            }]
        }
        
        allocation = SectorAllocation.from_dict(allocation_data, user)
        
        self.assertEqual(allocation.sector.seq, "1")
        self.assertEqual(allocation.target_duration, 120)
        self.assertEqual(len(allocation.allocated_parts), 1)
        self.assertEqual(allocation.allocated_parts[0].seq, 1)
    
    def test_sector_allocation_to_dict(self):
        """Test SectorAllocation.to_dict method."""
        user = User(id="user123", name="john_doe", display_name="John Doe")
        sector = Sector(seq="1", ratio=33.7, weight=100, abbr="TAX", description="Tax work", user=user)
        time_slot = TimeSlot(
            seq=1,
            start=Time(6, 35),
            duration=25,
            end=Time(7, 0),
            slot_type="A0-L",
            description="Morning work",
            original_index=0,
            user=user,
            sector=sector
        )
        
        allocation = SectorAllocation(
            sector=sector,
            target_duration=120,
            allocated_parts=[time_slot]
        )
        
        allocation_dict = allocation.to_dict()
        
        self.assertEqual(allocation_dict['target_duration'], 120)
        self.assertEqual(len(allocation_dict['allocated_parts']), 1)
        self.assertEqual(allocation_dict['allocated_parts'][0]['seq'], 1)


if __name__ == '__main__':
    unittest.main() 