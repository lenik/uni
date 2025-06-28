import unittest
import tempfile
import os
import sys
from unittest.mock import patch, mock_open, MagicMock
from io import StringIO
from src.main import setup_logging, main


class TestMain(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.temp_dir = tempfile.mkdtemp()
        self.timetable_file = os.path.join(self.temp_dir, "TimeTable.csv")
        self.sectors_file = os.path.join(self.temp_dir, "Sectors.csv")
        
        # Create sample files
        self._create_sample_files()
    
    def tearDown(self):
        """Clean up after each test method."""
        if os.path.exists(self.temp_dir):
            import shutil
            shutil.rmtree(self.temp_dir)
    
    def _create_sample_files(self):
        """Create sample CSV files for testing."""
        # Create sample timetable
        timetable_content = """Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare
3,08:00,25,08:25,FEM,
4,08:25,15,08:40,Traffic,Ride to Station
5,08:40,45,09:25,A1-T,@Train"""
        
        with open(self.timetable_file, 'w') as f:
            f.write(timetable_content)
        
        # Create sample sectors
        sectors_content = """Header line 1
Header line 2
Header line 3
Header line 4
Sector,Ratio,Weight,Abbr,Description
8,33.7%,100,TAX,税务应用/公司事项
6,6.7%,20,MIS,企业管理软件/WebApp
2,6.7%,20,LANG,编程语言学习，支持工具，框架等"""
        
        with open(self.sectors_file, 'w') as f:
            f.write(sectors_content)
    
    def test_setup_logging_verbose(self):
        """Test setup_logging with verbose flag."""
        with patch('logging.basicConfig') as mock_basic_config:
            setup_logging(verbose=True, quiet=False)
            mock_basic_config.assert_called_once()
            call_args = mock_basic_config.call_args
            self.assertEqual(call_args[1]['level'], 10)  # DEBUG level
    
    def test_setup_logging_quiet(self):
        """Test setup_logging with quiet flag."""
        with patch('logging.basicConfig') as mock_basic_config:
            setup_logging(verbose=False, quiet=True)
            mock_basic_config.assert_called_once()
            call_args = mock_basic_config.call_args
            self.assertEqual(call_args[1]['level'], 40)  # ERROR level
    
    def test_setup_logging_normal(self):
        """Test setup_logging with normal flags."""
        with patch('logging.basicConfig') as mock_basic_config:
            setup_logging(verbose=False, quiet=False)
            mock_basic_config.assert_called_once()
            call_args = mock_basic_config.call_args
            self.assertEqual(call_args[1]['level'], 20)  # INFO level
    
    @patch('sys.argv', ['timetab', '--timetable', 'test_timetable.csv', '--sectors', 'test_sectors.csv'])
    @patch('src.main.Config')
    @patch('src.main.TimeTable')
    @patch('src.main.Sectors')
    @patch('src.main.Scheduler')
    def test_main_success(self, mock_scheduler_class, mock_sectors_class, mock_time_table_class, mock_config_class):
        """Test main function with successful execution."""
        # Setup mocks
        mock_config = MagicMock()
        mock_config.get_timetable_path.return_value = self.timetable_file
        mock_config.get_sectors_path.return_value = self.sectors_file
        mock_config_class.return_value = mock_config
        
        mock_time_table = MagicMock()
        mock_time_table.count_available_slots.return_value = 2
        mock_time_table_class.from_file.return_value = mock_time_table
        
        mock_sectors = MagicMock()
        mock_sectors_class.from_file.return_value = mock_sectors
        
        mock_scheduler = MagicMock()
        mock_scheduler.allocate_sectors_proportionally.return_value = mock_time_table
        mock_scheduler_class.return_value = mock_scheduler
        
        # Capture stdout
        with patch('sys.stdout', new=StringIO()) as mock_stdout:
            result = main()
        
        # Verify result
        self.assertEqual(result, 0)
        
        # Verify mocks were called
        mock_config_class.assert_called_once_with(None)
        mock_time_table_class.from_file.assert_called_once_with('test_timetable.csv')
        mock_sectors_class.from_file.assert_called_once_with('test_sectors.csv')
        mock_scheduler_class.assert_called_once()
        mock_scheduler.allocate_sectors_proportionally.assert_called_once_with(mock_time_table, mock_sectors)
        # No file should be written by default
        mock_time_table.to_csv.assert_not_called()
    
    @patch('sys.argv', ['timetab', '--config', 'test_config.ini'])
    @patch('src.main.Config')
    @patch('src.main.TimeTable')
    @patch('src.main.Sectors')
    @patch('src.main.Scheduler')
    def test_main_with_config_file(self, mock_scheduler_class, mock_sectors_class, mock_time_table_class, mock_config_class):
        """Test main function with config file."""
        # Setup mocks
        mock_config = MagicMock()
        mock_config.get_timetable_path.return_value = self.timetable_file
        mock_config.get_sectors_path.return_value = self.sectors_file
        mock_config_class.return_value = mock_config
        
        mock_time_table = MagicMock()
        mock_time_table.count_available_slots.return_value = 2
        mock_time_table_class.from_file.return_value = mock_time_table
        
        mock_sectors = MagicMock()
        mock_sectors_class.from_file.return_value = mock_sectors
        
        mock_scheduler = MagicMock()
        mock_scheduler.allocate_sectors_proportionally.return_value = mock_time_table
        mock_scheduler_class.return_value = mock_scheduler
        
        result = main()
        
        # Verify config was called with the specified file
        mock_config_class.assert_called_once_with('test_config.ini')
        self.assertEqual(result, 0)
    
    @patch('sys.argv', ['timetab', '--output', 'custom_output.csv'])
    @patch('src.main.Config')
    @patch('src.main.TimeTable')
    @patch('src.main.Sectors')
    @patch('src.main.Scheduler')
    def test_main_with_custom_output(self, mock_scheduler_class, mock_sectors_class, mock_time_table_class, mock_config_class):
        """Test main function with custom output file."""
        # Setup mocks
        mock_config = MagicMock()
        mock_config.get_timetable_path.return_value = self.timetable_file
        mock_config.get_sectors_path.return_value = self.sectors_file
        mock_config_class.return_value = mock_config
        
        mock_time_table = MagicMock()
        mock_time_table.count_available_slots.return_value = 2
        mock_time_table_class.from_file.return_value = mock_time_table
        
        mock_sectors = MagicMock()
        mock_sectors_class.from_file.return_value = mock_sectors
        
        mock_scheduler = MagicMock()
        mock_scheduler.allocate_sectors_proportionally.return_value = mock_time_table
        mock_scheduler_class.return_value = mock_scheduler
        
        result = main()
        
        # Verify custom output file was used
        mock_time_table.to_csv.assert_called_once_with('custom_output.csv', all_slots=False)
        self.assertEqual(result, 0)
    
    @patch('sys.argv', ['timetab'])
    @patch('src.main.Config')
    def test_main_file_not_found(self, mock_config_class):
        """Test main function with file not found error."""
        # Setup mock to raise FileNotFoundError
        mock_config = MagicMock()
        mock_config.get_timetable_path.side_effect = FileNotFoundError("File not found")
        mock_config_class.return_value = mock_config
        
        result = main()
        
        # Should return error code 1
        self.assertEqual(result, 1)
    
    @patch('sys.argv', ['timetab'])
    @patch('src.main.Config')
    @patch('src.main.TimeTable')
    def test_main_value_error(self, mock_time_table_class, mock_config_class):
        """Test main function with ValueError."""
        # Setup mocks
        mock_config = MagicMock()
        mock_config.get_timetable_path.return_value = self.timetable_file
        mock_config.get_sectors_path.return_value = self.sectors_file
        mock_config_class.return_value = mock_config
        
        # Setup TimeTable to raise ValueError
        mock_time_table_class.from_file.side_effect = ValueError("Invalid data")
        
        result = main()
        
        # Should return error code 1
        self.assertEqual(result, 1)
    
    @patch('sys.argv', ['timetab', '--timetable', 'test_timetable.csv', '--sectors', 'test_sectors.csv', '--output', 'test_output.csv'])
    @patch('src.main.Config')
    @patch('src.main.TimeTable')
    @patch('src.main.Sectors')
    @patch('src.main.Scheduler')
    def test_main_with_output_file(self, mock_scheduler_class, mock_sectors_class, mock_time_table_class, mock_config_class):
        """Test main function with output file specified."""
        # Setup mocks
        mock_config = MagicMock()
        mock_config.get_timetable_path.return_value = self.timetable_file
        mock_config.get_sectors_path.return_value = self.sectors_file
        mock_config_class.return_value = mock_config
        
        mock_time_table = MagicMock()
        mock_time_table.count_available_slots.return_value = 2
        mock_time_table_class.from_file.return_value = mock_time_table
        
        mock_sectors = MagicMock()
        mock_sectors_class.from_file.return_value = mock_sectors
        
        mock_scheduler = MagicMock()
        mock_scheduler.allocate_sectors_proportionally.return_value = mock_time_table
        mock_scheduler_class.return_value = mock_scheduler
        
        # Capture stdout
        with patch('sys.stdout', new=StringIO()) as mock_stdout:
            result = main()
        
        # Verify result
        self.assertEqual(result, 0)
        
        # Verify mocks were called
        mock_config_class.assert_called_once_with(None)
        mock_time_table_class.from_file.assert_called_once_with('test_timetable.csv')
        mock_sectors_class.from_file.assert_called_once_with('test_sectors.csv')
        mock_scheduler_class.assert_called_once()
        mock_scheduler.allocate_sectors_proportionally.assert_called_once_with(mock_time_table, mock_sectors)
        # File should be written when output is specified
        mock_time_table.to_csv.assert_called_once_with('test_output.csv', all_slots=False) 