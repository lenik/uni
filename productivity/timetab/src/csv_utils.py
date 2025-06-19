import csv
import logging
from typing import List, Dict, Set, Optional, Tuple
from pathlib import Path


class CSVParser:
    """Generic CSV parser that handles leading lines, comments, and flexible field matching"""
    
    def __init__(self, required_fields: Optional[List[str]] = None, 
                 field_mappings: Optional[Dict[str, str]] = None):
        """
        Initialize CSV parser.
        
        Args:
            required_fields: List of required field names (case-insensitive)
            field_mappings: Dictionary mapping CSV field names to internal field names
        """
        self.required_fields = set(required_fields or [])
        self.field_mappings = field_mappings or {}
    
    def find_header_row(self, file_path: str) -> Tuple[int, List[str]]:
        """
        Find the header row in a CSV file, skipping leading lines and comments.
        
        Args:
            file_path: Path to the CSV file
            
        Returns:
            Tuple of (header_row_index, field_names)
            
        Raises:
            ValueError: If no valid header row is found
        """
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                for line_num, line in enumerate(file, 1):
                    line = line.strip()
                    
                    # Skip empty lines
                    if not line:
                        continue
                    
                    # Skip comment lines (starting with #)
                    if line.startswith('#'):
                        continue
                    
                    # Try to parse as CSV header
                    try:
                        reader = csv.reader([line])
                        fields = next(reader)
                        
                        # Check if this looks like a header
                        if self._is_valid_header(fields):
                            return line_num, fields
                            
                    except Exception as e:
                        # If CSV parsing fails, skip this line
                        logging.debug(f"Skipping line {line_num} in {file_path}: {e}")
                        continue
            
            raise ValueError(f"No valid header row found in {file_path}")
        except UnicodeDecodeError as e:
            raise ValueError(f"Encoding error reading {file_path}: {e}")
        except PermissionError as e:
            raise ValueError(f"Permission denied reading {file_path}: {e}")
        except Exception as e:
            raise ValueError(f"Unexpected error reading {file_path}: {e}")
    
    def _is_valid_header(self, fields: List[str]) -> bool:
        """
        Check if the given fields look like a valid header.
        
        Args:
            fields: List of field names
            
        Returns:
            True if this looks like a valid header
        """
        if not fields:
            return False
        
        # Check if all required fields are present (case-insensitive)
        field_set = {field.strip().lower() for field in fields if field.strip()}
        
        # If we have required fields defined, check if they're all present
        if self.required_fields:
            required_lower = {field.lower() for field in self.required_fields}
            return required_lower.issubset(field_set)
        
        # If no required fields defined, any non-empty fields are considered valid
        return bool(field_set)
    
    def parse_csv(self, file_path: str) -> List[Dict[str, str]]:
        """
        Parse CSV file with flexible header detection.
        
        Args:
            file_path: Path to the CSV file
            
        Returns:
            List of dictionaries representing rows
            
        Raises:
            FileNotFoundError: If file doesn't exist
            ValueError: If file format is invalid
        """
        # Check if file exists
        if not Path(file_path).exists():
            raise FileNotFoundError(f"File not found: {file_path}")
        
        # Check if file is readable
        if not Path(file_path).is_file():
            raise ValueError(f"Path is not a file: {file_path}")
        
        try:
            # Find the header row
            header_row_num, field_names = self.find_header_row(file_path)
            
            # Validate that all required fields are present
            if self.required_fields:
                missing_fields = self.required_fields - {field.lower() for field in field_names}
                if missing_fields:
                    raise ValueError(f"Missing required fields in {file_path}: {missing_fields}")
            
            # Parse the data rows
            rows = []
            with open(file_path, 'r', encoding='utf-8') as file:
                # Skip to the header row
                for _ in range(header_row_num - 1):
                    next(file, None)
                
                # Create a new reader starting from the header row
                file.seek(0)
                for _ in range(header_row_num - 1):
                    next(file, None)
                
                reader = csv.DictReader(file)
                
                for row_num, row in enumerate(reader, header_row_num + 1):
                    # Skip empty rows
                    if not any(row.values()):
                        continue
                    
                    # Skip comment rows (any field starts with #)
                    if any(field.startswith('#') for field in row.values() if field):
                        continue
                    
                    # Apply field mappings if defined
                    if self.field_mappings:
                        mapped_row = {}
                        for csv_field, internal_field in self.field_mappings.items():
                            if csv_field in row:
                                mapped_row[internal_field] = row[csv_field]
                            else:
                                mapped_row[internal_field] = ''
                        rows.append(mapped_row)
                    else:
                        rows.append(row)
            
            logging.info(f"Successfully parsed {len(rows)} rows from {file_path}")
            return rows
            
        except UnicodeDecodeError as e:
            raise ValueError(f"Encoding error reading {file_path}: {e}")
        except PermissionError as e:
            raise ValueError(f"Permission denied reading {file_path}: {e}")
        except csv.Error as e:
            raise ValueError(f"CSV parsing error in {file_path}: {e}")
        except Exception as e:
            raise ValueError(f"Unexpected error parsing {file_path}: {e}") 