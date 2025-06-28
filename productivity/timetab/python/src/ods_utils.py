import logging
from typing import List, Dict, Set, Optional, Tuple
from pathlib import Path
import zipfile
import xml.etree.ElementTree as ET


class ODSParser:
    """Generic parser for LibreOffice Open Document Spreadsheet (.ods) files"""
    
    def __init__(self, required_fields: Set[str]):
        """
        Initialize ODS parser.
        
        Args:
            required_fields: Set of required field names (case-insensitive)
        """
        self.required_fields = required_fields
    
    def parse_ods(self, file_path: str) -> Dict[str, List[Dict[str, str]]]:
        """
        Parse ODS file and extract sheets that match the required fields.
        
        Args:
            file_path: Path to the ODS file
            
        Returns:
            Dictionary mapping sheet names to list of row dictionaries
            
        Raises:
            FileNotFoundError: If file doesn't exist
            ValueError: If file format is invalid
        """
        if not Path(file_path).exists():
            raise FileNotFoundError(f"File not found: {file_path}")
        
        if not Path(file_path).suffix.lower() == '.ods':
            raise ValueError(f"File is not an ODS file: {file_path}")
        
        try:
            # ODS files are ZIP archives containing XML files
            with zipfile.ZipFile(file_path, 'r') as ods_zip:
                # Extract content.xml which contains the spreadsheet data
                content_xml = ods_zip.read('content.xml')
                
                # Parse the XML
                root = ET.fromstring(content_xml)
                
                # Define namespaces used in ODS files
                namespaces = {
                    'office': 'urn:oasis:names:tc:opendocument:xmlns:office:1.0',
                    'table': 'urn:oasis:names:tc:opendocument:xmlns:table:1.0',
                    'text': 'urn:oasis:names:tc:opendocument:xmlns:text:1.0'
                }
                
                result = {}
                
                # Find all table elements (sheets)
                for table in root.findall('.//table:table', namespaces):
                    sheet_name = table.get('table:name', 'Sheet1')
                    sheet_data = self._parse_sheet(table, namespaces)
                    
                    if sheet_data:
                        result[sheet_name] = sheet_data
                
                logging.info(f"Successfully parsed {file_path}: {len(result)} matching sheets")
                return result
                
        except zipfile.BadZipFile:
            raise ValueError(f"Invalid ODS file format: {file_path}")
        except ET.ParseError as e:
            raise ValueError(f"XML parsing error in {file_path}: {e}")
        except Exception as e:
            raise ValueError(f"Unexpected error parsing {file_path}: {e}")
    
    def _parse_sheet(self, table_element, namespaces: Dict[str, str]) -> List[Dict[str, str]]:
        """
        Parse a single sheet (table) from the ODS file.
        
        Args:
            table_element: XML element representing the table
            namespaces: XML namespaces dictionary
            
        Returns:
            List of dictionaries representing rows
        """
        rows = []
        header_row = None
        header_fields = []
        
        # Find all table-row elements
        for row_element in table_element.findall('.//table:table-row', namespaces):
            row_data = []
            
            # Find all table-cell elements in this row
            for cell_element in row_element.findall('.//table:table-cell', namespaces):
                cell_value = self._extract_cell_value(cell_element, namespaces)
                row_data.append(cell_value)
            
            # Skip empty rows
            if not any(cell.strip() for cell in row_data):
                continue
            
            # Skip comment rows (any cell starts with #)
            if any(cell.startswith('#') for cell in row_data if cell):
                continue
            
            # If we haven't found a header yet, try to identify it
            if header_row is None:
                if self._is_valid_header(row_data):
                    header_row = row_data
                    header_fields = [field.strip() for field in row_data if field.strip()]
                    continue
            
            # If we have a header, process data rows
            if header_row is not None:
                row_dict = {}
                for i, field in enumerate(header_fields):
                    if i < len(row_data):
                        row_dict[field] = row_data[i]
                    else:
                        row_dict[field] = ''
                rows.append(row_dict)
        
        return rows
    
    def _extract_cell_value(self, cell_element, namespaces: Dict[str, str]) -> str:
        """
        Extract the text value from a table cell.
        
        Args:
            cell_element: XML element representing the cell
            namespaces: XML namespaces dictionary
            
        Returns:
            String value of the cell
        """
        # Look for text content in the cell
        text_elements = cell_element.findall('.//text:p', namespaces)
        if text_elements:
            return ' '.join(elem.text or '' for elem in text_elements)
        
        # If no text elements, try to get text directly
        return cell_element.text or ''
    
    def _is_valid_header(self, fields: List[str]) -> bool:
        """
        Check if the given fields contain all required fields.
        
        Args:
            fields: List of field names
            
        Returns:
            True if all required fields are present
        """
        if not fields:
            return False
        
        field_set = {field.strip().lower() for field in fields if field.strip()}
        return self.required_fields.issubset(field_set) 