import csv
import logging
from typing import List, Optional
from pathlib import Path
from .models import Sector
from .csv_utils import CSVParser
from .ods_utils import ODSParser

class Sectors:
    def __init__(self, sectors: Optional[List[Sector]] = None):
        self.sectors = sectors or []
    
    @classmethod
    def from_csv(cls, filename: str) -> 'Sectors':
        """Create Sectors instance from CSV file"""
        sectors = []
        try:
            # Create CSV parser with required fields and field mappings
            parser = CSVParser(
                required_fields=['sector', 'occupy', 'weight', 'abbr', 'description'],
                field_mappings={
                    'Sector': 'sector',
                    'Occupy': 'occupy',
                    'Weight': 'weight',
                    'Abbr': 'abbr',
                    'Description': 'description'
                }
            )
            
            rows = parser.parse_csv(filename)
            
            for row in rows:
                sector = Sector.from_strings(
                    sector_id=row['sector'],
                    occupy=row['occupy'],
                    weight=int(row['weight']),
                    abbr=row['abbr'],
                    description=row['description']
                )
                sectors.append(sector)
                
            logging.info(f"Successfully loaded {len(sectors)} sectors from {filename}")
        except FileNotFoundError:
            logging.error(f"Sectors file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading sectors file {filename}: {e}")
            raise
        
        return cls(sectors)
    
    @classmethod
    def from_ods(cls, filename: str) -> 'Sectors':
        """Create Sectors instance from ODS file"""
        sectors = []
        try:
            # Create ODS parser with sectors required fields
            parser = ODSParser(required_fields={'sector', 'occupy', 'weight', 'abbr', 'description'})
            sheets = parser.parse_ods(filename)
            
            if not sheets:
                raise ValueError(f"No sectors data found in {filename}")
            
            # Use the first sheet found
            sheet_name = list(sheets.keys())[0]
            rows = sheets[sheet_name]
            
            for row in rows:
                sector = Sector.from_strings(
                    sector_id=row['Sector'],
                    occupy=row['Occupy'],
                    weight=int(row['Weight']),
                    abbr=row['Abbr'],
                    description=row['Description']
                )
                sectors.append(sector)
                
            logging.info(f"Successfully loaded {len(sectors)} sectors from sheet '{sheet_name}' in {filename}")
        except FileNotFoundError:
            logging.error(f"Sectors file not found: {filename}")
            raise
        except Exception as e:
            logging.error(f"Error reading sectors file {filename}: {e}")
            raise
        
        return cls(sectors)
    
    @classmethod
    def from_file(cls, filename: str) -> 'Sectors':
        """Create Sectors instance from file (CSV or ODS)"""
        file_path = Path(filename)
        
        if file_path.suffix.lower() == '.ods':
            return cls.from_ods(filename)
        elif file_path.suffix.lower() == '.csv':
            return cls.from_csv(filename)
        else:
            raise ValueError(f"Unsupported file format: {filename}. Supported formats: .csv, .ods")
    
    def to_csv(self, filename: str):
        """Write sectors to CSV file"""
        if not self.sectors:
            return
        
        try:
            with open(filename, 'w', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                # Write header lines
                writer.writerow(['', '', '297', '', 'Total'])
                writer.writerow(['', '', str(len(self.sectors)), '', 'Rows'])
                writer.writerow(['', '', '29.70', '', 'Average'])
                writer.writerow(['', '', '', '', ''])
                writer.writerow(['Sector', 'Occupy', 'Weight', 'Abbr', 'Description'])
                
                for sector in self.sectors:
                    sector_dict = sector.to_dict()
                    writer.writerow([
                        sector_dict['sector_id'],
                        sector_dict['occupy'],
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
    
    def get_sector_by_id(self, sector_id: str) -> Optional[Sector]:
        """Get sector by ID"""
        for sector in self.sectors:
            if sector.sector_id == sector_id:
                return sector
        return None
    
    def get_sector_by_abbr(self, abbr: str) -> Optional[Sector]:
        """Get sector by abbreviation"""
        for sector in self.sectors:
            if sector.abbr == abbr:
                return sector
        return None
    
    def sort_by_weight(self, reverse: bool = True) -> List[Sector]:
        """Get sectors sorted by weight"""
        return sorted(self.sectors, key=lambda x: x.weight, reverse=reverse)
    
    def sort_by_occupy(self, reverse: bool = True) -> List[Sector]:
        """Get sectors sorted by occupy percentage"""
        return sorted(self.sectors, key=lambda x: x.occupy, reverse=reverse)
    
    def __len__(self) -> int:
        return len(self.sectors)
    
    def __iter__(self):
        return iter(self.sectors)