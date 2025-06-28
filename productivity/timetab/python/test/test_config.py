import unittest
import tempfile
import os
import configparser
from unittest.mock import patch, mock_open
from src.config import Config


class TestConfig(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.temp_dir = tempfile.mkdtemp()
        self.config_file = os.path.join(self.temp_dir, "test_config.ini")
    
    def tearDown(self):
        """Clean up after each test method."""
        if os.path.exists(self.temp_dir):
            import shutil
            shutil.rmtree(self.temp_dir)
    
    def test_init_with_config_file(self):
        """Test initialization with specific config file."""
        config = Config(self.config_file)
        self.assertEqual(config.config_file, self.config_file)
        self.assertIsInstance(config.config, configparser.ConfigParser)
    
    def test_get_default_config_path(self):
        """Test _get_default_config_path method."""
        config = Config()
        expected_path = os.path.join(os.path.expanduser("~"), ".config", "timetab", "default.ini")
        self.assertEqual(config.config_file, expected_path)
    
    def test_load_config_existing_file(self):
        """Test _load_config with existing config file."""
        # Create a config file
        config_content = """[DEFAULT]
timetable = test_timetable.csv
sectors = test_sectors.csv
"""
        with open(self.config_file, 'w') as f:
            f.write(config_content)
        
        config = Config(self.config_file)
        
        # Verify config was loaded
        self.assertEqual(config.config.get('DEFAULT', 'timetable'), 'test_timetable.csv')
        self.assertEqual(config.config.get('DEFAULT', 'sectors'), 'test_sectors.csv')
    
    def test_create_default_config(self):
        """Test _create_default_config method."""
        config = Config(self.config_file)
        
        # Verify default config was created
        self.assertTrue(os.path.exists(self.config_file))
        
        # Verify default values
        self.assertEqual(config.config.get('DEFAULT', 'timetable'), 'TimeTable.csv')
        self.assertEqual(config.config.get('DEFAULT', 'sectors'), 'Sectors.csv')
    
    def test_get_timetable_path_default(self):
        """Test get_timetable_path with default value."""
        config = Config(self.config_file)
        path = config.get_timetable_path()
        
        # Should be relative to config file directory
        expected_path = os.path.join(self.temp_dir, 'TimeTable.csv')
        self.assertEqual(path, expected_path)
    
    def test_get_timetable_path_custom(self):
        """Test get_timetable_path with custom value."""
        config_content = """[DEFAULT]
timetable = custom_timetable.csv
"""
        with open(self.config_file, 'w') as f:
            f.write(config_content)
        
        config = Config(self.config_file)
        path = config.get_timetable_path()
        
        expected_path = os.path.join(self.temp_dir, 'custom_timetable.csv')
        self.assertEqual(path, expected_path)
    
    def test_get_sectors_path_default(self):
        """Test get_sectors_path with default value."""
        config = Config(self.config_file)
        path = config.get_sectors_path()
        
        expected_path = os.path.join(self.temp_dir, 'Sectors.csv')
        self.assertEqual(path, expected_path)
    
    def test_get_sectors_path_custom(self):
        """Test get_sectors_path with custom value."""
        config_content = """[DEFAULT]
sectors = custom_sectors.csv
"""
        with open(self.config_file, 'w') as f:
            f.write(config_content)
        
        config = Config(self.config_file)
        path = config.get_sectors_path()
        
        expected_path = os.path.join(self.temp_dir, 'custom_sectors.csv')
        self.assertEqual(path, expected_path)
    
    def test_expand_path_absolute(self):
        """Test _expand_path with absolute path."""
        config = Config(self.config_file)
        
        absolute_path = "/absolute/path/file.csv"
        expanded = config._expand_path(absolute_path)
        self.assertEqual(expanded, absolute_path)
    
    def test_expand_path_relative(self):
        """Test _expand_path with relative path."""
        config = Config(self.config_file)
        
        relative_path = "relative_file.csv"
        expanded = config._expand_path(relative_path)
        expected_path = os.path.join(self.temp_dir, 'relative_file.csv')
        self.assertEqual(expanded, expected_path)
    
    @patch('os.path.expanduser')
    def test_expand_path_home_tilde(self, mock_expanduser):
        """Test _expand_path with tilde (home directory)."""
        mock_expanduser.return_value = "/home/testuser"
        config = Config(self.config_file)
        
        tilde_path = "~/file.csv"
        expanded = config._expand_path(tilde_path)
        expected_path = "/home/testuser/file.csv"
        self.assertEqual(expanded, expected_path)
        mock_expanduser.assert_called_once_with("~/file.csv")
    
    def test_expand_path_complex_relative(self):
        """Test _expand_path with complex relative path."""
        config = Config(self.config_file)
        
        complex_path = "subdir/another/file.csv"
        expanded = config._expand_path(complex_path)
        expected_path = os.path.join(self.temp_dir, 'subdir', 'another', 'file.csv')
        self.assertEqual(expanded, expected_path)
    
    def test_fallback_values(self):
        """Test fallback values when config file is missing."""
        # Create config without the specific keys
        config_content = """[DEFAULT]
# Empty config
"""
        with open(self.config_file, 'w') as f:
            f.write(config_content)
        
        config = Config(self.config_file)
        
        # Should use fallback values
        timetable_path = config.get_timetable_path()
        sectors_path = config.get_sectors_path()
        
        self.assertIn('TimeTable.csv', timetable_path)
        self.assertIn('Sectors.csv', sectors_path)
    
    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Test with empty config file
        with open(self.config_file, 'w') as f:
            f.write("")
        
        config = Config(self.config_file)
        
        # Should still work with fallback values
        self.assertIsNotNone(config.get_timetable_path())
        self.assertIsNotNone(config.get_sectors_path())
        
        # Test with malformed config file
        with open(self.config_file, 'w') as f:
            f.write("malformed content")
        
        config = Config(self.config_file)
        
        # Should still work with fallback values
        self.assertIsNotNone(config.get_timetable_path())
        self.assertIsNotNone(config.get_sectors_path())


if __name__ == '__main__':
    unittest.main() 