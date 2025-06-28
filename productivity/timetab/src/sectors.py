import csv
import logging
from typing import List, Optional, Dict, Any
from pathlib import Path
from .user import User
from .sector import Sector
from .time_utils import Time
from .csv_utils import CSVParser
from .ods_utils import ODSParser
from .json_interface import JSONMixin

class Sectors(JSONMixin):
    def __init__(self, sectors: Optional[List[Sector]] = None, user: Optional[User] = None):
        self.sectors = sectors or []
        self.user = user
    
    @classmethod
    def from_file(cls, filename: str, user: Optional[User] = None) -> 'Sectors':
        """Create Sectors instance from file based on extension"""
        file_path = Path(filename)
        
        if file_path.suffix.lower() == '.json':
            return cls.from_json(filename, user)
        elif file_path.suffix.lower() == '.csv':
            return cls.from_csv(filename, user)
        elif file_path.suffix.lower() == '.ods':
            return cls.from_ods(filename, user)
        elif file_path.suffix.lower() == '.xlsx':
            return cls.from_xlsx(filename, user)
        else:
            raise ValueError(f"Unsupported file format: {filename}. Supported formats: .csv, .ods, .xlsx, .json")
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any], user: Optional[User] = None) -> 'Sectors':
        """Create Sectors instance from dictionary"""
        sectors = []
        for sector_data in data.get('sectors', []):
            sector = Sector.from_dict(sector_data, user)
            sectors.append(sector)
        return cls(sectors, user)
    
    @classmethod
    def from_csv(cls, filename: str, user: Optional[User] = None) -> 'Sectors':
        """Create Sectors instance from CSV file"""
        sectors = []
        try:
            # Create CSV parser with sectors required fields - using exact CSV column names
            parser = CSVParser(required_fields={'Seq', 'Ratio', 'Weight', 'Abbr', 'Description'})
            rows = parser.parse_csv(filename)
            
            for row in rows:
                sector = Sector.from_strings(
                    seq=row['Seq'],
                    ratio=row['Ratio'],
                    weight=int(row['Weight']),
                    abbr=row['Abbr'],
                    description=row['Description'],
                    user=user
                )
                sectors.append(sector)
                
            logging.info(f"Successfully loaded {len(sectors)} sectors from {filename}")
            for sector in sectors:
                logging.debug(f"Loaded sector: {sector.abbr}({sector.seq}) - {sector.description}")
        except FileNotFoundError:
            logging.error(f"Sectors file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading sectors file {filename}: {e}")
            raise
        
        return cls(sectors, user)
    
    @classmethod
    def from_ods(cls, filename: str, user: Optional[User] = None) -> 'Sectors':
        """Create Sectors instance from ODS file"""
        sectors = []
        try:
            # Create ODS parser with sectors required fields - using exact CSV column names
            parser = ODSParser(required_fields={'Seq', 'Ratio', 'Weight', 'Abbr', 'Description'})
            sheets = parser.parse_ods(filename)
            
            if not sheets:
                raise ValueError(f"No sectors data found in {filename}")
            
            # Use the first sheet found
            sheet_name = list(sheets.keys())[0]
            rows = sheets[sheet_name]
            
            for i, row in enumerate(rows):
                sector = Sector.from_strings(
                    seq=row['Seq'],
                    ratio=row['Ratio'],
                    weight=int(row['Weight']),
                    abbr=row['Abbr'],
                    description=row['Description'],
                    user=user
                )
                sectors.append(sector)
                
            logging.info(f"Successfully loaded {len(sectors)} sectors from sheet '{sheet_name}' in {filename}")
        except FileNotFoundError:
            logging.error(f"Sectors file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading sectors file {filename}: {e}")
            raise
        
        return cls(sectors, user)
    
    @classmethod
    def from_xlsx(cls, filename: str, user: Optional[User] = None) -> 'Sectors':
        """Create Sectors instance from Excel XLSX file"""
        from .excel_utils import ExcelParser
        sectors = []
        try:
            parser = ExcelParser(required_fields={'Seq', 'Ratio', 'Weight', 'Abbr', 'Description'})
            sheets = parser.parse_excel(filename)
            if not sheets:
                raise ValueError(f"No sectors data found in {filename}")
            sheet_name = list(sheets.keys())[0]
            rows = sheets[sheet_name]
            for i, row in enumerate(rows):
                sector = Sector.from_strings(
                    seq=row['Seq'],
                    ratio=row['Ratio'],
                    weight=int(row['Weight']),
                    abbr=row['Abbr'],
                    description=row['Description'],
                    user=user
                )
                sectors.append(sector)
            logging.info(f"Successfully loaded {len(sectors)} sectors from sheet '{sheet_name}' in {filename}")
        except FileNotFoundError:
            logging.error(f"Sectors file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading sectors file {filename}: {e}")
            raise
        return cls(sectors, user)
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary"""
        result = {
            'sectors': [sector.to_dict() for sector in self.sectors],
            'metadata': {
                'total_sectors': len(self.sectors),
                'total_weight': self.get_total_weight()
            }
        }
        if self.user:
            result['user_id'] = self.user.id
        return result
    
    def to_csv(self, filename: str):
        """Write sectors to CSV file"""
        if not self.sectors:
            return
        
        try:
            with open(filename, 'w', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                # Write header lines matching the exact CSV format
                writer.writerow(['', '', '297', '', 'Total'])
                writer.writerow(['', '', str(len(self.sectors)), '', 'Rows'])
                writer.writerow(['', '', '29.70', '', 'Average'])
                writer.writerow(['', '', '', '', ''])
                writer.writerow(['Seq', 'Ratio', 'Weight', 'Abbr', 'Description'])
                
                for sector in self.sectors:
                    sector_dict = sector.to_dict()
                    writer.writerow([
                        sector_dict['seq'],
                        sector_dict['ratio'],
                        sector_dict['weight'],
                        sector_dict['abbr'],
                        sector_dict['description']
                    ])
            logging.info(f"Successfully wrote {len(self.sectors)} sectors to {filename}")
        except Exception as e:
            logging.error(f"Error writing sectors file {filename}: {e}")
            raise
    
    def get_total_weight(self) -> int:
        """Calculate total weight of all sectors"""
        return sum(sector.weight for sector in self.sectors)
    
    def get_sectors(self) -> List[Sector]:
        """Get all sectors"""
        return self.sectors
    
    def add_sector(self, sector: Sector):
        """Add a sector"""
        self.sectors.append(sector)
    
    def remove_sector(self, sector: Sector):
        """Remove a sector"""
        self.sectors.remove(sector)
    
    def get_sector_by_seq(self, seq: str) -> Optional[Sector]:
        """Get sector by sequence number (legacy, prefer abbr)"""
        # Prefer using get_sector_by_abbr for new code
        for sector in self.sectors:
            if sector.seq == seq:
                return sector
        return None
    
    def get_sector_by_abbr(self, abbr: str) -> Optional[Sector]:
        """Get sector by abbreviation (preferred)"""
        for sector in self.sectors:
            if sector.abbr == abbr:
                return sector
        return None
    
    def sort_by_weight(self, reverse: bool = True) -> List[Sector]:
        """Get sectors sorted by weight"""
        return sorted(self.sectors, key=lambda x: x.weight, reverse=reverse)
    
    def sort_by_ratio(self, reverse: bool = True) -> List[Sector]:
        """Get sectors sorted by ratio percentage"""
        return sorted(self.sectors, key=lambda x: x.ratio, reverse=reverse)
    
    def __len__(self) -> int:
        return len(self.sectors)
    
    def __iter__(self):
        return iter(self.sectors)
    
    def to_db(self) -> list:
        """Convert all sectors to ORM models for the user"""
        return [sector.to_db() for sector in self.sectors]