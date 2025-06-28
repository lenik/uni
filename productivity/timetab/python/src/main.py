import argparse
import logging
import sys
from pathlib import Path
from typing import Optional, Dict, Any
from .user import User
from .config import Config
from .time_table import TimeTable
from .sectors import Sectors
from .scheduler import Scheduler
from .db import get_session, create_tables
from .time_slot import TimeSlot
from .sector import Sector
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

def load_data_from_db(user: User) -> tuple:
    """
    Load data from database for the specified user.
    
    Args:
        user: User object
        
    Returns:
        Tuple of (time_table, sectors) - None for missing data types
    """
    from .user_orm import UserORM
    from .time_table_orm import TimeTableORM
    from .time_slot_orm import TimeSlotORM
    from .sector_orm import SectorORM
    
    session = get_session()
    time_table = None
    sectors = None
    
    try:
        # Get or create user in database
        user_orm = UserORM.get_by_name(session, user.name)
        if not user_orm:
            user_orm = user.to_db()
            user_orm.save(session)
            logging.info(f"Created new user in database: {user.name}")
        else:
            logging.info(f"Found existing user in database: {user.name}")
        
        # Load timetables for user
        timetables = TimeTableORM.get_by_user(session, user_orm.id)
        if timetables:
            # Use the first timetable (could be enhanced to select specific one)
            timetable_orm = timetables[0]
            time_table = TimeTable.from_db(timetable_orm)
            
            # Load timeslots for this timetable
            timeslot_orms = TimeSlotORM.get_by_parent(session, timetable_orm.id)
            for ts_orm in timeslot_orms:
                timeslot = TimeSlot.from_db(ts_orm)
                time_table.add_slot(timeslot)
            
            logging.info(f"Loaded timetable '{time_table.label}' with {len(time_table)} slots from database")
        
        # Load sectors for user
        sector_orms = SectorORM.get_by_user(session, user_orm.id)
        if sector_orms:
            sectors = Sectors([Sector.from_db(s_orm) for s_orm in sector_orms], user)
            logging.info(f"Loaded {len(sectors)} sectors for user '{user.name}' from database")
        
    except Exception as e:
        logging.error(f"Failed to load data from database: {e}")
        raise
    finally:
        session.close()
    
    return time_table, sectors

def save_data_to_db(time_table: TimeTable, sectors: Sectors, user: User):
    """
    Save data to database for the specified user.
    
    Args:
        time_table: TimeTable object to save
        sectors: Sectors object to save
        user: User object
    """
    from .user_orm import UserORM
    from .time_table_orm import TimeTableORM
    from .time_slot_orm import TimeSlotORM
    from .sector_orm import SectorORM
    
    session = get_session()
    
    try:
        # Get or create user in database
        user_orm = UserORM.get_by_name(session, user.name)
        if not user_orm:
            user_orm = user.to_db()
            user_orm.save(session)
            logging.info(f"Created new user in database: {user.name}")
        else:
            logging.info(f"Found existing user in database: {user.name}")
        
        # Save timetable
        if time_table:
            timetable_orm = time_table.to_db(user_orm.id)
            timetable_orm.save(session)
            
            # Save timeslots
            for slot in time_table:
                timeslot_orm = slot.to_db(timetable_orm.id)
                timeslot_orm.save(session)
            
            logging.info(f"Saved timetable '{time_table.label}' with {len(time_table)} slots to database")
        
        # Save sectors
        if sectors:
            # Remove existing sectors for this user
            existing_sectors = SectorORM.get_by_user(session, user_orm.id)
            for s in existing_sectors:
                s.delete(session)
            # Save new sectors
            for sector in sectors:
                sector_orm = sector.to_db()
                sector_orm.save(session)
            logging.info(f"Saved {len(sectors)} sectors for user '{user.name}' to database")
        
    except Exception as e:
        logging.error(f"Failed to save data to database: {e}")
        raise
    finally:
        session.close()

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
            required_fields = {'Seq', 'Start', 'Duration', 'End', 'Type', 'Description'}
        elif data_type == 'sectors':
            required_fields = {'Seq', 'Ratio', 'Weight', 'Abbr', 'Description'}
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
        timetable_parser = ODSParser(required_fields={'Seq', 'Start', 'Duration', 'End', 'Type', 'Description'})
        timetable_sheets = timetable_parser.parse_ods(file_path)
        result['timetable'] = len(timetable_sheets) > 0
        
        # Check for sectors data
        sectors_parser = ODSParser(required_fields={'Seq', 'Ratio', 'Weight', 'Abbr', 'Description'})
        sectors_sheets = sectors_parser.parse_ods(file_path)
        result['sectors'] = len(sectors_sheets) > 0
        
    except Exception as e:
        logging.debug(f"Error detecting data types in {file_path}: {e}")
    
    return result

def load_data_from_ods(file_path: str, data_types: dict, user: Optional[User] = None) -> tuple:
    """
    Load data from ODS file for the specified data types.
    
    Args:
        file_path: Path to the ODS file
        data_types: Dictionary with 'timetable' and 'sectors' keys
        user: Optional user object
        
    Returns:
        Tuple of (time_table, sectors) - None for missing data types
    """
    time_table = None
    sectors = None
    
    if data_types.get('timetable'):
        try:
            time_table = TimeTable.from_ods(file_path, user)
            logging.info(f"Successfully loaded timetable from {file_path}")
        except Exception as e:
            logging.error(f"Failed to load timetable from {file_path}: {e}")
            raise
    
    if data_types.get('sectors'):
        try:
            sectors = Sectors.from_ods(file_path, user)
            logging.info(f"Successfully loaded sectors from {file_path}")
        except Exception as e:
            logging.error(f"Failed to load sectors from {file_path}: {e}")
            raise
    
    return time_table, sectors

def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(
        description='Automatic scheduler tool for allocating sectors to time slots',
        epilog='For ODS files: specify just one file to load both data types if available, or use separate options for specific data types.'
    )
    parser.add_argument('-v', '--verbose', action='store_true', help='Enable verbose logging')
    parser.add_argument('-q', '--quiet', action='store_true', help='Suppress all logging except errors')
    parser.add_argument('-u', '--user', help='Username for user-specific data directory and configuration')
    parser.add_argument('--config', help='Configuration file path')
    parser.add_argument('--timetable', action='append', 
                       help='Timetable file path (CSV or ODS). If multiple specified, only last one used.')
    parser.add_argument('--sectors', action='append', 
                       help='Sectors file path (CSV or ODS). If multiple specified, only last one used.')
    parser.add_argument('-o', '--output', 
                       help='Output file (if not specified, no file is written)')
    parser.add_argument('-s', '--shuffle', action='store_true', 
                       help='Shuffle sectors in random order before allocation')
    parser.add_argument('-j', '--large-first', action='store_true', 
                       help='Order sectors by weight (largest first)')
    parser.add_argument('-a', '--all', action='store_true', 
                       help='Output all time slots (default: only available/allocated slots)')
    parser.add_argument('-n', '--maxload', type=int, metavar='MINUTES',
                       help='Maximum load per time slice (splits sectors into smaller chunks)')
    parser.add_argument('-b', '--break', dest='break_minutes', type=int, metavar='MINUTES', default=0,
                       help='Break minutes between time slices (default: 0)')
    parser.add_argument('--db-url', type=str, help='SQLAlchemy DB URL (overrides all other DB options)')
    parser.add_argument('--db-host', type=str, default=None, help='Database host (default: localhost)')
    parser.add_argument('--db-port', type=str, default=None, help='Database port (default: 5432)')
    parser.add_argument('--db-user', type=str, default=None, help='Database user (default: postgres)')
    parser.add_argument('--db-password', type=str, default=None, help='Database password')
    parser.add_argument('--db-name', type=str, default=None, help='Database name (default: timetab)')
    
    args = parser.parse_args()
    
    # Setup logging
    setup_logging(args.verbose, args.quiet)
    
    # Create user object if username is specified
    user_obj = None
    if args.user:
        user_obj = User(name=args.user, display_name=args.user)
        logging.info(f"Using user: {user_obj.display_name}")
    
    try:
        # Check if database options are specified
        use_database = any([
            args.db_url,
            args.db_host,
            args.db_port,
            args.db_user,
            args.db_password,
            args.db_name
        ])
        
        if use_database:
            logging.info("Database options detected, using database mode")
            # Initialize database connection and create tables
            create_tables()
            
            # Load data from database
            if not user_obj:
                logging.error("User must be specified when using database mode")
                raise ValueError("User must be specified when using database mode")
            
            time_table, sectors = load_data_from_db(user_obj)
            
            # Validate that we have both required data types
            missing_data = []
            if time_table is None:
                missing_data.append("timetable")
            if sectors is None:
                missing_data.append("sectors")
            
            if missing_data:
                error_msg = f"Missing required data in database: {', '.join(missing_data)}"
                logging.error(error_msg)
                raise ValueError(error_msg)
                
        else:
            # Load configuration
            logging.info("Loading configuration...")
            config = Config(args.config, user_obj)
            
            # Determine file paths (command line overrides config)
            timetable_files = args.timetable if args.timetable else [config.get_timetable_path()]
            sectors_files = args.sectors if args.sectors else [config.get_sectors_path()]
            
            # Use only the last file of each type
            timetable_file = timetable_files[-1] if timetable_files else None
            sectors_file = sectors_files[-1] if sectors_files else None
            
            # Log the actual file paths being used
            logging.info(f"Timetable file path: {os.path.abspath(timetable_file) if timetable_file else 'None'}")
            logging.info(f"Sectors file path: {os.path.abspath(sectors_file) if sectors_file else 'None'}")
            
            time_table = None
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
                time_table, sectors = load_data_from_ods(timetable_file, data_types, user_obj)
                
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
                        time_table = TimeTable.from_file(timetable_file, user_obj)
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
                        sectors = Sectors.from_file(sectors_file, user_obj)
                        logging.info(f"Successfully loaded sectors from {sectors_file}")
                    except Exception as e:
                        logging.error(f"Failed to load sectors from {sectors_file}: {e}")
                        raise
            
            # Validate that we have both required data types
            missing_data = []
            if time_table is None:
                missing_data.append("timetable")
            if sectors is None:
                missing_data.append("sectors")
            
            if missing_data:
                error_msg = f"Missing required data: {', '.join(missing_data)}"
                if timetable_file and sectors_file and Path(timetable_file).resolve() == Path(sectors_file).resolve():
                    error_msg += f"\nODS file {timetable_file} does not contain the required data types"
                logging.error(error_msg)
                raise ValueError(error_msg)
        
        logging.info(f"Loaded {len(time_table)} time slots")
        logging.info(f"Loaded {len(sectors)} sectors")
        logging.info(f"Found {time_table.count_available_slots()} available time slots")
        
        # Log ordering and load management options
        if args.shuffle:
            logging.info("Using shuffle mode: sectors will be allocated in random order")
        elif args.large_first:
            logging.info("Using large-first mode: sectors will be allocated by weight (largest first)")
        else:
            logging.info("Using default mode: sectors will be allocated in original file order")
        
        if args.maxload:
            logging.info(f"Using maxload mode: maximum {args.maxload} minutes per time slice")
        if args.break_minutes > 0:
            logging.info(f"Using break mode: {args.break_minutes} minutes break between slices")
        
        # Allocate sectors to timetable
        scheduler = Scheduler(
            shuffle_sectors=args.shuffle, 
            large_first=args.large_first,
            maxload=args.maxload,
            break_minutes=args.break_minutes
        )
        logging.info("Starting sector allocation...")
        updated_time_table = scheduler.allocate_sectors_proportionally(time_table, sectors)
        
        # Log allocation results
        allocated_slots = updated_time_table.get_allocated_slots()
        logging.info(f"Allocation completed: {len(allocated_slots)} slots allocated")
        
        if allocated_slots:
            for slot in allocated_slots:
                logging.debug(f"Allocated: {slot.start}-{slot.end} ({slot.duration}min): {slot.description}")
        
        # Save to database if using database mode
        if use_database:
            logging.info("Saving updated data to database...")
            save_data_to_db(updated_time_table, sectors, user_obj)
        
        # Write output
        if args.output:
            logging.info(f"Writing output to {args.output}")
            if args.all:
                logging.info("Writing all time slots to output file")
                updated_time_table.to_csv(args.output, all_slots=True)
            else:
                logging.info("Writing only available/allocated time slots to output file")
                updated_time_table.to_csv(args.output, all_slots=False)
        
        logging.info("Scheduling completed successfully!")
        
        # Print summary of allocations (only in non-quiet mode)
        if not args.quiet:
            print("\nAllocation Summary:")
            
            if args.all:
                # Show all time slots
                print("All time slots:")
                for slot in updated_time_table:
                    print(f"{slot.start}-{slot.end} ({slot.duration}min, {slot.slot_type}): {slot.description}")
                
                # Show statistics
                total_slots = len(updated_time_table)
                available_slots = updated_time_table.count_available_slots()
                allocated_slots_count = len(allocated_slots)
                
                print(f"\nSummary Statistics:")
                print(f"- Total time slots: {total_slots}")
                print(f"- Available slots: {available_slots}")
                print(f"- Allocated slots: {allocated_slots_count}")
                print(f"- Non-available slots: {total_slots - available_slots}")
            else:
                # Show only allocated slots (default behavior)
                if allocated_slots:
                    for slot in allocated_slots:
                        print(f"{slot.start}-{slot.end} ({slot.duration}min, {slot.slot_type}): {slot.description}")
                    print(f"\nTotal allocated slots: {len(allocated_slots)}")
                else:
                    print("No sectors were allocated to available time slots.")
                    print("This might happen if:")
                    print("- No available time slots were found")
                    print("- No sectors were loaded")
                    print("- All available slots were too short for allocation")
    
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