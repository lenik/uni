import unittest
import tempfile
import os
import csv
from unittest.mock import patch, mock_open
from src.csv_utils import CSVParser


class TestCSVParser(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.temp_dir = tempfile.mkdtemp()
    
    def tearDown(self):
        """Clean up after each test method."""
        if os.path.exists(self.temp_dir):
            import shutil
            shutil.rmtree(self.temp_dir)
    
    def test_init_with_required_fields(self):
        """Test initialization with required fields."""
        required_fields = ['order', 'start', 'duration', 'end', 'type', 'description']
        parser = CSVParser(required_fields=required_fields)
        self.assertEqual(parser.required_fields, set(required_fields))
    
    def test_init_with_field_mappings(self):
        """Test initialization with field mappings."""
        field_mappings = {'Order': 'order', 'Start': 'start'}
        parser = CSVParser(field_mappings=field_mappings)
        self.assertEqual(parser.field_mappings, field_mappings)
    
    def test_init_without_parameters(self):
        """Test initialization without parameters."""
        parser = CSVParser()
        self.assertEqual(parser.required_fields, set())
        self.assertEqual(parser.field_mappings, {})
    
    def test_is_valid_header_with_required_fields(self):
        """Test _is_valid_header with required fields defined."""
        parser = CSVParser(required_fields=['order', 'start', 'duration'])
        
        # Valid header with all required fields
        valid_fields = ['Order', 'Start', 'Duration', 'End', 'Type']
        self.assertTrue(parser._is_valid_header(valid_fields))
        
        # Invalid header missing required fields
        invalid_fields = ['Order', 'Start']  # Missing Duration
        self.assertFalse(parser._is_valid_header(invalid_fields))
    
    def test_is_valid_header_without_required_fields(self):
        """Test _is_valid_header without required fields defined."""
        parser = CSVParser()
        
        # Any non-empty fields are considered valid
        valid_fields = ['Order', 'Start', 'Duration', 'End', 'Type']
        self.assertTrue(parser._is_valid_header(valid_fields))
        
        # Empty fields are not valid
        invalid_fields = ['', '', '', '']
        self.assertFalse(parser._is_valid_header(invalid_fields))
    
    def test_is_valid_header_empty_fields(self):
        """Test _is_valid_header with empty fields."""
        parser = CSVParser(required_fields=['order', 'start'])
        
        # Header with empty fields
        fields = ['Order', '', 'Start', '  ', 'Type']
        self.assertTrue(parser._is_valid_header(fields))
        
        # Completely empty fields
        fields = ['', '', '', '']
        self.assertFalse(parser._is_valid_header(fields))
    
    def test_find_header_row_simple(self):
        """Test find_header_row with simple CSV."""
        csv_content = """Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        row_num, fields = parser.find_header_row(csv_file)
        
        self.assertEqual(row_num, 1)
        self.assertEqual(fields, ['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
    
    def test_find_header_row_with_leading_lines(self):
        """Test find_header_row with leading lines."""
        csv_content = """Some leading text
More leading text
# This is a comment
Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        row_num, fields = parser.find_header_row(csv_file)
        
        self.assertEqual(row_num, 5)
        self.assertEqual(fields, ['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
    
    def test_find_header_row_with_comments(self):
        """Test find_header_row with comment lines."""
        csv_content = """# This is a comment
# Another comment
Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        row_num, fields = parser.find_header_row(csv_file)
        
        self.assertEqual(row_num, 3)
        self.assertEqual(fields, ['Order', 'Start', 'Duration', 'End', 'Type', 'Description'])
    
    def test_find_header_row_no_header_found(self):
        """Test find_header_row when no valid header is found."""
        csv_content = """Some text
More text
1,2,3,4,5
6,7,8,9,10"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration'])
        
        with self.assertRaises(ValueError) as context:
            parser.find_header_row(csv_file)
        
        # Check that the error message includes the real filename
        self.assertIn(csv_file, str(context.exception))
        self.assertIn("No valid header row found", str(context.exception))
    
    @patch('logging.info')
    def test_parse_csv_simple(self, mock_logging):
        """Test parse_csv with simple CSV."""
        csv_content = """Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        rows = parser.parse_csv(csv_file)
        
        self.assertEqual(len(rows), 2)
        self.assertEqual(rows[0]['Order'], '1')
        self.assertEqual(rows[0]['Start'], '06:35')
        self.assertEqual(rows[1]['Order'], '2')
        self.assertEqual(rows[1]['Type'], 'Reserved')
        
        # Check that logging was called with the real filename
        mock_logging.assert_called_with(f"Successfully parsed {len(rows)} rows from {csv_file}")
    
    def test_parse_csv_with_field_mappings(self):
        """Test parse_csv with field mappings."""
        csv_content = """Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
2,07:00,60,08:00,Reserved,Prepare"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(
            required_fields=['order', 'start', 'duration', 'end', 'type', 'description'],
            field_mappings={
                'Order': 'order',
                'Start': 'start',
                'Duration': 'duration',
                'End': 'end',
                'Type': 'type',
                'Description': 'description'
            }
        )
        
        rows = parser.parse_csv(csv_file)
        
        self.assertEqual(len(rows), 2)
        self.assertEqual(rows[0]['order'], '1')
        self.assertEqual(rows[0]['start'], '06:35')
        self.assertEqual(rows[1]['order'], '2')
        self.assertEqual(rows[1]['type'], 'Reserved')
    
    def test_parse_csv_with_leading_lines_and_comments(self):
        """Test parse_csv with leading lines and comments."""
        csv_content = """Some leading text
# This is a comment
More leading text
Order,Start,Duration,End,Type,Description
1,06:35,25,07:00,A0-L,
# Another comment
2,07:00,60,08:00,Reserved,Prepare
3,08:00,25,08:25,FEM,"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        rows = parser.parse_csv(csv_file)
        
        self.assertEqual(len(rows), 2)  # Comment row should be skipped
        self.assertEqual(rows[0]['Order'], '1')
        self.assertEqual(rows[1]['Order'], '3')  # Row 2 was a comment
    
    def test_parse_csv_file_not_found(self):
        """Test parse_csv with non-existent file."""
        parser = CSVParser()
        non_existent_file = os.path.join(self.temp_dir, "nonexistent.csv")
        
        with self.assertRaises(FileNotFoundError) as context:
            parser.parse_csv(non_existent_file)
        
        # Check that the error message includes the real filename
        self.assertIn(non_existent_file, str(context.exception))
        self.assertIn("File not found", str(context.exception))
    
    def test_parse_csv_path_not_file(self):
        """Test parse_csv with path that is not a file."""
        parser = CSVParser()
        
        # Create a directory
        dir_path = os.path.join(self.temp_dir, "testdir")
        os.makedirs(dir_path, exist_ok=True)
        
        with self.assertRaises(ValueError) as context:
            parser.parse_csv(dir_path)
        
        # Check that the error message includes the real path
        self.assertIn(dir_path, str(context.exception))
        self.assertIn("Path is not a file", str(context.exception))
    
    def test_parse_csv_missing_required_fields(self):
        """Test parse_csv with missing required fields."""
        csv_content = """Order,Start,Duration,End
1,06:35,25,07:00
2,07:00,60,08:00"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser(required_fields=['order', 'start', 'duration', 'end', 'type', 'description'])
        
        with self.assertRaises(ValueError) as context:
            parser.parse_csv(csv_file)
        
        # Check that the error message includes the real filename
        self.assertIn(csv_file, str(context.exception))
        self.assertIn("Missing required fields", str(context.exception))
        self.assertIn("type", str(context.exception))
        self.assertIn("description", str(context.exception))
    
    def test_parse_csv_without_required_fields(self):
        """Test parse_csv without required fields (any header is valid)."""
        csv_content = """Field1,Field2,Field3
value1,value2,value3
value4,value5,value6"""
        
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'w') as f:
            f.write(csv_content)
        
        parser = CSVParser()  # No required fields
        rows = parser.parse_csv(csv_file)
        
        self.assertEqual(len(rows), 2)
        self.assertEqual(rows[0]['Field1'], 'value1')
        self.assertEqual(rows[1]['Field2'], 'value5')
    
    def test_parse_csv_encoding_error(self):
        """Test parse_csv with encoding error."""
        # Create a file with invalid encoding
        csv_file = os.path.join(self.temp_dir, "test.csv")
        with open(csv_file, 'wb') as f:
            f.write(b'\xff\xfe\x00\x00')  # Invalid UTF-8
        
        parser = CSVParser()
        
        with self.assertRaises(ValueError) as context:
            parser.parse_csv(csv_file)
        
        # Check that the error message includes the real filename
        self.assertIn(csv_file, str(context.exception))
        self.assertIn("Encoding error", str(context.exception))
    
    def test_parse_csv_permission_error(self):
        """Test parse_csv with permission error."""
        parser = CSVParser()
        
        # Try to read a system file that requires elevated permissions
        system_file = "/etc/shadow"  # Usually requires root access
        
        # This test might not work on all systems, so we'll check if it exists first
        if os.path.exists(system_file) and not os.access(system_file, os.R_OK):
            with self.assertRaises(ValueError) as context:
                parser.parse_csv(system_file)
            
            # Check that the error message includes the real filename
            self.assertIn(system_file, str(context.exception))
            self.assertIn("Permission denied", str(context.exception))


if __name__ == '__main__':
    unittest.main() 