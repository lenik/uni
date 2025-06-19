import unittest
import tempfile
import os
import zipfile
import xml.etree.ElementTree as ET
from src.ods_utils import ODSParser


class TestODSParser(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.temp_dir = tempfile.mkdtemp()
    
    def tearDown(self):
        """Clean up after each test method."""
        if os.path.exists(self.temp_dir):
            import shutil
            shutil.rmtree(self.temp_dir)
    
    def create_test_ods(self, filename: str, content_xml: str):
        """Create a test ODS file with the given content XML."""
        ods_file = os.path.join(self.temp_dir, filename)
        
        with zipfile.ZipFile(ods_file, 'w') as ods_zip:
            ods_zip.writestr('content.xml', content_xml)
        
        return ods_file
    
    def test_parse_ods_timetable_fields(self):
        """Test parsing ODS file with timetable fields."""
        content_xml = """<?xml version="1.0" encoding="UTF-8"?>
<office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
                        xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                        xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">
    <office:body>
        <office:spreadsheet>
            <table:table table:name="Sheet1">
                <table:table-row>
                    <table:table-cell><text:p>Order</text:p></table:table-cell>
                    <table:table-cell><text:p>Start</text:p></table:table-cell>
                    <table:table-cell><text:p>Duration</text:p></table:table-cell>
                    <table:table-cell><text:p>End</text:p></table:table-cell>
                    <table:table-cell><text:p>Type</text:p></table:table-cell>
                    <table:table-cell><text:p>Description</text:p></table:table-cell>
                </table:table-row>
                <table:table-row>
                    <table:table-cell><text:p>1</text:p></table:table-cell>
                    <table:table-cell><text:p>06:35</text:p></table:table-cell>
                    <table:table-cell><text:p>25</text:p></table:table-cell>
                    <table:table-cell><text:p>07:00</text:p></table:table-cell>
                    <table:table-cell><text:p>A0-L</text:p></table:table-cell>
                    <table:table-cell><text:p></text:p></table:table-cell>
                </table:table-row>
            </table:table>
        </office:spreadsheet>
    </office:body>
</office:document-content>"""
        
        ods_file = self.create_test_ods("test_timetable.ods", content_xml)
        
        # Create parser with timetable fields
        parser = ODSParser(required_fields={'order', 'start', 'duration', 'end', 'type', 'description'})
        sheets = parser.parse_ods(ods_file)
        
        self.assertEqual(len(sheets), 1)
        self.assertIn('Sheet1', sheets)
        
        sheet_data = sheets['Sheet1']
        self.assertEqual(len(sheet_data), 1)  # One data row
        
        row = sheet_data[0]
        self.assertEqual(row['Order'], '1')
        self.assertEqual(row['Start'], '06:35')
        self.assertEqual(row['Duration'], '25')
        self.assertEqual(row['End'], '07:00')
        self.assertEqual(row['Type'], 'A0-L')
        self.assertEqual(row['Description'], '')
    
    def test_parse_ods_sectors_fields(self):
        """Test parsing ODS file with sectors fields."""
        content_xml = """<?xml version="1.0" encoding="UTF-8"?>
<office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
                        xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                        xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">
    <office:body>
        <office:spreadsheet>
            <table:table table:name="Sectors">
                <table:table-row>
                    <table:table-cell><text:p>Sector</text:p></table:table-cell>
                    <table:table-cell><text:p>Occupy</text:p></table:table-cell>
                    <table:table-cell><text:p>Weight</text:p></table:table-cell>
                    <table:table-cell><text:p>Abbr</text:p></table:table-cell>
                    <table:table-cell><text:p>Description</text:p></table:table-cell>
                </table:table-row>
                <table:table-row>
                    <table:table-cell><text:p>8</text:p></table:table-cell>
                    <table:table-cell><text:p>33.7%</text:p></table:table-cell>
                    <table:table-cell><text:p>100</text:p></table:table-cell>
                    <table:table-cell><text:p>TAX</text:p></table:table-cell>
                    <table:table-cell><text:p>公司事项</text:p></table:table-cell>
                </table:table-row>
            </table:table>
        </office:spreadsheet>
    </office:body>
</office:document-content>"""
        
        ods_file = self.create_test_ods("test_sectors.ods", content_xml)
        
        # Create parser with sectors fields
        parser = ODSParser(required_fields={'sector', 'occupy', 'weight', 'abbr', 'description'})
        sheets = parser.parse_ods(ods_file)
        
        self.assertEqual(len(sheets), 1)
        self.assertIn('Sectors', sheets)
        
        sheet_data = sheets['Sectors']
        self.assertEqual(len(sheet_data), 1)  # One data row
        
        row = sheet_data[0]
        self.assertEqual(row['Sector'], '8')
        self.assertEqual(row['Occupy'], '33.7%')
        self.assertEqual(row['Weight'], '100')
        self.assertEqual(row['Abbr'], 'TAX')
        self.assertEqual(row['Description'], '公司事项')
    
    def test_parse_ods_no_matching_fields(self):
        """Test parsing ODS file with no matching fields."""
        content_xml = """<?xml version="1.0" encoding="UTF-8"?>
<office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
                        xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                        xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">
    <office:body>
        <office:spreadsheet>
            <table:table table:name="Sheet1">
                <table:table-row>
                    <table:table-cell><text:p>Field1</text:p></table:table-cell>
                    <table:table-cell><text:p>Field2</text:p></table:table-cell>
                </table:table-row>
                <table:table-row>
                    <table:table-cell><text:p>value1</text:p></table:table-cell>
                    <table:table-cell><text:p>value2</text:p></table:table-cell>
                </table:table-row>
            </table:table>
        </office:spreadsheet>
    </office:body>
</office:document-content>"""
        
        ods_file = self.create_test_ods("test_no_match.ods", content_xml)
        
        # Create parser with timetable fields (should not match)
        parser = ODSParser(required_fields={'order', 'start', 'duration', 'end', 'type', 'description'})
        sheets = parser.parse_ods(ods_file)
        
        # Should return empty result since no matching fields
        self.assertEqual(len(sheets), 0)
    
    def test_parse_ods_file_not_found(self):
        """Test parsing non-existent ODS file."""
        non_existent_file = os.path.join(self.temp_dir, "nonexistent.ods")
        
        parser = ODSParser(required_fields={'order', 'start', 'duration', 'end', 'type', 'description'})
        
        with self.assertRaises(FileNotFoundError):
            parser.parse_ods(non_existent_file)
    
    def test_parse_ods_invalid_format(self):
        """Test parsing invalid ODS file."""
        invalid_file = os.path.join(self.temp_dir, "invalid.ods")
        
        # Create a file that's not a valid ZIP
        with open(invalid_file, 'w') as f:
            f.write("This is not a valid ODS file")
        
        parser = ODSParser(required_fields={'order', 'start', 'duration', 'end', 'type', 'description'})
        
        with self.assertRaises(ValueError) as context:
            parser.parse_ods(invalid_file)
        
        self.assertIn("Invalid ODS file format", str(context.exception))
        self.assertIn(invalid_file, str(context.exception))


if __name__ == '__main__':
    unittest.main() 