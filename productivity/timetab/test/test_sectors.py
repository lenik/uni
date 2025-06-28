import unittest
import tempfile
import os
import csv
from unittest.mock import patch, mock_open
from src.sectors import Sectors
from src.sector import Sector


class TestSectors(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.sample_sectors = [
            Sector(seq="8", ratio="33.7%", weight=100, abbr="TAX", 
                  description="税务应用/公司事项"),
            Sector(seq="6", ratio="6.7%", weight=20, abbr="MIS", 
                  description="企业管理软件/WebApp"),
            Sector(seq="2", ratio="6.7%", weight=20, abbr="LANG", 
                  description="编程语言学习，支持工具，框架等"),
            Sector(seq="5", ratio="6.7%", weight=20, abbr="CAD", 
                  description="建筑，BIM，@zlq, DWG tk, ..."),
        ]
        
        self.sectors = Sectors(self.sample_sectors)
    
    def test_init_empty(self):
        """Test initialization with no sectors."""
        empty_sectors = Sectors()
        self.assertEqual(len(empty_sectors), 0)
        self.assertEqual(empty_sectors.sectors, [])
    
    def test_init_with_sectors(self):
        """Test initialization with provided sectors."""
        self.assertEqual(len(self.sectors), 4)
        self.assertEqual(self.sectors.sectors, self.sample_sectors)
    
    def test_get_total_weight(self):
        """Test get_total_weight method."""
        total_weight = self.sectors.get_total_weight()
        expected_weight = 100 + 20 + 20 + 20  # 160
        self.assertEqual(total_weight, expected_weight)
    
    def test_get_total_weight_empty(self):
        """Test get_total_weight with empty sectors."""
        empty_sectors = Sectors()
        self.assertEqual(empty_sectors.get_total_weight(), 0)
    
    def test_get_sectors(self):
        """Test get_sectors method."""
        sectors = self.sectors.get_sectors()
        self.assertEqual(sectors, self.sample_sectors)
    
    def test_add_sector(self):
        """Test add_sector method."""
        new_sector = Sector(seq="13", ratio="6.7%", weight=20, abbr="SCI", 
                           description="科学计算，分析，统计，预测，CUDA等等")
        
        initial_count = len(self.sectors)
        self.sectors.add_sector(new_sector)
        
        self.assertEqual(len(self.sectors), initial_count + 1)
        self.assertIn(new_sector, self.sectors.sectors)
    
    def test_remove_sector(self):
        """Test remove_sector method."""
        sector_to_remove = self.sample_sectors[0]
        initial_count = len(self.sectors)
        
        self.sectors.remove_sector(sector_to_remove)
        
        self.assertEqual(len(self.sectors), initial_count - 1)
        self.assertNotIn(sector_to_remove, self.sectors.sectors)
    
    def test_get_sector_by_seq_found(self):
        """Test get_sector_by_seq method when sector is found."""
        sector = self.sectors.get_sector_by_seq("8")
        self.assertIsNotNone(sector)
        self.assertEqual(sector.seq, "8")
        self.assertEqual(sector.abbr, "TAX")
    
    def test_get_sector_by_seq_not_found(self):
        """Test get_sector_by_seq method when sector is not found."""
        sector = self.sectors.get_sector_by_seq("999")
        self.assertIsNone(sector)
    
    def test_get_sector_by_abbr_found(self):
        """Test get_sector_by_abbr method when sector is found."""
        sector = self.sectors.get_sector_by_abbr("TAX")
        self.assertIsNotNone(sector)
        self.assertEqual(sector.abbr, "TAX")
        self.assertEqual(sector.seq, "8")
    
    def test_get_sector_by_abbr_not_found(self):
        """Test get_sector_by_abbr method when sector is not found."""
        sector = self.sectors.get_sector_by_abbr("NONEXISTENT")
        self.assertIsNone(sector)
    
    def test_sort_by_weight_descending(self):
        """Test sort_by_weight method with descending order."""
        sorted_sectors = self.sectors.sort_by_weight(reverse=True)
        
        # Should be sorted by weight in descending order
        weights = [sector.weight for sector in sorted_sectors]
        self.assertEqual(weights, [100, 20, 20, 20])
    
    def test_sort_by_weight_ascending(self):
        """Test sort_by_weight method with ascending order."""
        sorted_sectors = self.sectors.sort_by_weight(reverse=False)
        
        # Should be sorted by weight in ascending order
        weights = [sector.weight for sector in sorted_sectors]
        self.assertEqual(weights, [20, 20, 20, 100])
    
    def test_len(self):
        """Test __len__ method."""
        self.assertEqual(len(self.sectors), 4)
    
    def test_iter(self):
        """Test __iter__ method."""
        sectors_list = list(self.sectors)
        self.assertEqual(sectors_list, self.sample_sectors)
    
    @patch('pathlib.Path.is_file', return_value=True)
    @patch('pathlib.Path.exists', return_value=True)
    @patch('builtins.open', new_callable=mock_open, read_data="""Header line 1\nHeader line 2\nHeader line 3\nHeader line 4\nSeq,Ratio,Weight,Abbr,Description\n8,33.7%,100,TAX,税务应用/公司事项\n6,6.7%,20,MIS,企业管理软件/WebApp\n2,6.7%,20,LANG,编程语言学习，支持工具，框架等\n5,6.7%,20,CAD,建筑，BIM，@zlq, DWG tk, ...""")
    @patch('src.csv_utils.CSVParser.parse_csv')
    def test_from_csv_with_headers(self, mock_parse_csv, mock_open, mock_exists, mock_is_file):
        """Test from_csv method with header lines."""
        # Mock the CSV parser to return our test data
        mock_parse_csv.return_value = [
            {'Seq': '8', 'Ratio': '33.7%', 'Weight': '100', 'Abbr': 'TAX', 'Description': '税务应用/公司事项'},
            {'Seq': '6', 'Ratio': '6.7%', 'Weight': '20', 'Abbr': 'MIS', 'Description': '企业管理软件/WebApp'},
            {'Seq': '2', 'Ratio': '6.7%', 'Weight': '20', 'Abbr': 'LANG', 'Description': '编程语言学习，支持工具，框架等'},
            {'Seq': '5', 'Ratio': '6.7%', 'Weight': '20', 'Abbr': 'CAD', 'Description': '建筑，BIM，@zlq, DWG tk, ...'},
        ]
        
        sectors = Sectors.from_csv('test_file.csv')
        
        # Check that sectors were loaded correctly
        self.assertEqual(len(sectors), 4)
        
        # Check first sector
        first_sector = sectors.sectors[0]
        self.assertEqual(first_sector.seq, "8")
        self.assertEqual(first_sector.ratio, 33.7)  # Should be parsed as float
        self.assertEqual(first_sector.weight, 100)
        self.assertEqual(first_sector.abbr, "TAX")
        self.assertEqual(first_sector.description, "税务应用/公司事项")
    
    def test_from_csv_file_not_found(self):
        """Test from_csv method with file not found."""
        with self.assertRaises(FileNotFoundError):
            Sectors.from_csv('nonexistent_file.csv')
    
    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Test with empty sector list
        empty_sectors = Sectors([])
        self.assertEqual(empty_sectors.get_total_weight(), 0)
        self.assertEqual(empty_sectors.sort_by_weight(), [])
        self.assertIsNone(empty_sectors.get_sector_by_seq("1"))
        self.assertIsNone(empty_sectors.get_sector_by_abbr("TEST"))
        
        # Test with single sector
        single_sector = Sectors([self.sample_sectors[0]])
        self.assertEqual(single_sector.get_total_weight(), 100)
        self.assertEqual(len(single_sector.sort_by_weight()), 1)


if __name__ == '__main__':
    unittest.main() 