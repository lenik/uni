import argparse
import logging
import sys
from .config import Config
from .time_slots import TimeSlots
from .sectors import Sectors
from .scheduler import Scheduler

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

def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(description='Automatic scheduler tool for allocating sectors to time slots')
    parser.add_argument('-v', '--verbose', action='store_true', help='Enable verbose logging')
    parser.add_argument('-q', '--quiet', action='store_true', help='Suppress all logging except errors')
    parser.add_argument('--config', help='Configuration file path')
    parser.add_argument('--timetable', help='Override timetable file path')
    parser.add_argument('--sectors', help='Override sectors file path')
    parser.add_argument('-o', '--output', default='Scheduled_TimeTable.csv', help='Output file (default: Scheduled_TimeTable.csv)')
    
    args = parser.parse_args()
    
    # Setup logging
    setup_logging(args.verbose, args.quiet)
    
    try:
        # Load configuration
        config = Config(args.config)
        
        # Determine file paths (command line overrides config)
        timetable_path = args.timetable or config.get_timetable_path()
        sectors_path = args.sectors or config.get_sectors_path()
        
        logging.info(f"Using timetable: {timetable_path}")
        logging.info(f"Using sectors: {sectors_path}")
        
        # Read input files using new classes
        time_slots = TimeSlots.from_csv(timetable_path)
        sectors = Sectors.from_csv(sectors_path)
        
        logging.info(f"Loaded {len(time_slots)} time slots from {timetable_path}")
        logging.info(f"Loaded {len(sectors)} sectors from {sectors_path}")
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
                if slot.is_available():
                    print(f"{slot.start}-{slot.end} ({slot.duration}min, {slot.slot_type}): {slot.description}")
    
    except FileNotFoundError as e:
        logging.error(f"File not found: {e}")
        return 1
    except ValueError as e:
        logging.error(f"Invalid data: {e}")
        return 1
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())