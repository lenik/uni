import argparse
import logging
import sys
from pathlib import Path
from .config import Config
from .time_slots import TimeSlots
from .sectors import Sectors
from .scheduler import Scheduler
import os

def setup_logging(verbose: bool, quiet: bool):
    """Setup logging configuration based on verbosity flags"""
    if quiet:
        level = logging.ERROR
    elif verbose:
        level = logging.DEBUG
    else:
        level = logging.INFO
    
    logging.basicConfig(
        level=level,
        format='%(asctime)s - %(levelname)s - %(message)s',
        datefmt='%H:%M:%S'
    )

def check_ods_contains_data_type(file_path: str, data_type: str) -> bool:
    """
    Check if ODS file contains the specified data type.
    
    Args:
        file_path: Path to the ODS file
        data_type: 'timetable' or 'sectors'
        
    Returns:
        True if the file contains the specified data type
    """
    try:
        from .ods_utils import ODSParser
        
        if data_type == 'timetable':
            required_fields = {'order', 'start', 'duration', 'end', 'type', 'description'}
        elif data_type == 'sectors':
            required_fields = {'sector', 'occupy', 'weight', 'abbr', 'description'}
        else:
            return False
        
        parser = ODSParser(required_fields=required_fields)
        sheets = parser.parse_ods(file_path)
        return len(sheets) > 0
        
    except Exception:
        return False

def detect_ods_data_types(file_path: str) -> dict:
    """
    Detect what data types are available in an ODS file.
    
    Args:
        file_path: Path to the ODS file
        
    Returns:
        Dictionary with 'timetable' and 'sectors' keys, values are True/False
    """
    result = {'timetable': False, 'sectors': False}
    
    try:
        from .ods_utils import ODSParser
        
        # Check for timetable data
        timetable_parser = ODSParser(required_fields={'order', 'start', 'duration', 'end', 'type', 'description'})
        timetable_sheets = timetable_parser.parse_ods(file_path)
        result['timetable'] = len(timetable_sheets) > 0
        
        # Check for sectors data
        sectors_parser = ODSParser(required_fields={'sector', 'occupy', 'weight', 'abbr', 'description'})
        sectors_sheets = sectors_parser.parse_ods(file_path)
        result['sectors'] = len(sectors_sheets) > 0
        
    except Exception as e:
        logging.debug(f"Error detecting data types in {file_path}: {e}")
    
    return result

def load_data_from_ods(file_path: str, data_types: dict) -> tuple:
    """
    Load data from ODS file for the specified data types.
    
    Args:
        file_path: Path to the ODS file
        data_types: Dictionary with 'timetable' and 'sectors' keys
        
    Returns:
        Tuple of (time_slots, sectors) - None for missing data types
    """
    time_slots = None
    sectors = None
    
    if data_types.get('timetable'):
        try:
            time_slots = TimeSlots.from_ods(file_path)
            logging.info(f"Successfully loaded timetable from {file_path}")
        except Exception as e:
            logging.error(f"Failed to load timetable from {file_path}: {e}")
            raise
    
    if data_types.get('sectors'):
        try:
            sectors = Sectors.from_ods(file_path)
            logging.info(f"Successfully loaded sectors from {file_path}")
        except Exception as e:
            logging.error(f"Failed to load sectors from {file_path}: {e}")
            raise
    
    return time_slots, sectors

def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(
        description='Automatic scheduler tool for allocating sectors to time slots',
        epilog='For ODS files: specify just one file to load both data types if available, or use separate options for specific data types.'
    )
    parser.add_argument('-v', '--verbose', action='store_true', help='Enable verbose logging')
    parser.add_argument('-q', '--quiet', action='store_true', help='Suppress all logging except errors')
    parser.add_argument('--config', help='Configuration file path')
    parser.add_argument('--timetable', action='append', 
                       help='Timetable file path (CSV or ODS). If multiple specified, only last one used.')
    parser.add_argument('--sectors', action='append', 
                       help='Sectors file path (CSV or ODS). If multiple specified, only last one used.')
    parser.add_argument('-o', '--output', default='Scheduled_TimeTable.csv', 
                       help='Output file (default: Scheduled_TimeTable.csv)')
    
    args = parser.parse_args()
    
    # Setup logging
    setup_logging(args.verbose, args.quiet)
    
    try:
        # Load configuration
        logging.info("Loading configuration...")
        config = Config(args.config)
        
        # Determine file paths (command line overrides config)
        timetable_files = args.timetable if args.timetable else [config.get_timetable_path()]
        sectors_files = args.sectors if args.sectors else [config.get_sectors_path()]
        
        # Use only the last file of each type
        timetable_file = timetable_files[-1] if timetable_files else None
        sectors_file = sectors_files[-1] if sectors_files else None
        
        # Log the actual file paths being used
        logging.info(f"Timetable file path: {os.path.abspath(timetable_file) if timetable_file else 'None'}")
        logging.info(f"Sectors file path: {os.path.abspath(sectors_file) if sectors_file else 'None'}")
        
        time_slots = None
        sectors = None
        
        # Smart ODS handling: if both files are the same ODS file, load both data types from it
        if (timetable_file and sectors_file and 
            Path(timetable_file).resolve() == Path(sectors_file).resolve() and
            Path(timetable_file).suffix.lower() == '.ods'):
            
            logging.info(f"Detected same ODS file for both data types: {timetable_file}")
            
            # Detect what data types are available
            data_types = detect_ods_data_types(timetable_file)
            logging.info(f"ODS file contains: timetable={data_types['timetable']}, sectors={data_types['sectors']}")
            
            # Load both data types from the same file
            time_slots, sectors = load_data_from_ods(timetable_file, data_types)
            
        else:
            # Load data separately
            if timetable_file:
                logging.info(f"Loading timetable from: {timetable_file}")
                
                if Path(timetable_file).suffix.lower() == '.ods':
                    # For ODS files, check if they contain timetable data
                    if not check_ods_contains_data_type(timetable_file, 'timetable'):
                        logging.error(f"ODS file {timetable_file} does not contain timetable data")
                        raise ValueError(f"ODS file {timetable_file} does not contain timetable data")
                
                try:
                    time_slots = TimeSlots.from_file(timetable_file)
                    logging.info(f"Successfully loaded timetable from {timetable_file}")
                except Exception as e:
                    logging.error(f"Failed to load timetable from {timetable_file}: {e}")
                    raise
            
            if sectors_file:
                logging.info(f"Loading sectors from: {sectors_file}")
                
                if Path(sectors_file).suffix.lower() == '.ods':
                    # For ODS files, check if they contain sectors data
                    if not check_ods_contains_data_type(sectors_file, 'sectors'):
                        logging.error(f"ODS file {sectors_file} does not contain sectors data")
                        raise ValueError(f"ODS file {sectors_file} does not contain sectors data")
                
                try:
                    sectors = Sectors.from_file(sectors_file)
                    logging.info(f"Successfully loaded sectors from {sectors_file}")
                except Exception as e:
                    logging.error(f"Failed to load sectors from {sectors_file}: {e}")
                    raise
        
        # Validate that we have both required data types
        missing_data = []
        if time_slots is None:
            missing_data.append("timetable")
        if sectors is None:
            missing_data.append("sectors")
        
        if missing_data:
            error_msg = f"Missing required data: {', '.join(missing_data)}"
            if timetable_file and sectors_file and Path(timetable_file).resolve() == Path(sectors_file).resolve():
                error_msg += f"\nODS file {timetable_file} does not contain the required data types"
            logging.error(error_msg)
            raise ValueError(error_msg)
        
        logging.info(f"Loaded {len(time_slots)} time slots")
        logging.info(f"Loaded {len(sectors)} sectors")
        logging.info(f"Found {time_slots.count_available_slots()} available time slots (A- type)")
        
        # Allocate sectors to timetable
        scheduler = Scheduler()
        logging.info("Starting sector allocation...")
        updated_time_slots = scheduler.allocate_sectors_proportionally(time_slots, sectors)
        
        # Write output
        logging.info(f"Writing output to {args.output}")
        updated_time_slots.to_csv(args.output)
        
        logging.info("Scheduling completed successfully!")
        
        # Print summary of allocations (only in non-quiet mode)
        if not args.quiet:
            print("\nAllocation Summary:")
            for slot in updated_time_slots:
                if slot.slot_type.startswith('A-'):
                    print(f"{slot.start}-{slot.end} ({slot.duration}min, {slot.slot_type}): {slot.description}")
    
    except FileNotFoundError as e:
        logging.error(f"File not found: {e}")
        return 1
    except ValueError as e:
        logging.error(f"Invalid data: {e}")
        return 1
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        # Add more context for config-related errors
        if "config" in str(e).lower() or "ini" in str(e).lower():
            logging.error("This appears to be a configuration file error.")
            logging.error("Check that your config file has proper section headers.")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())