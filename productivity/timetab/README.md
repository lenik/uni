# Timetab - Automatic Scheduler Tool

A Python tool for automatically allocating sectors to time slots based on weights.

## Installation

```bash
cd timetab
pip install -e .
```

## Usage

```bash
# Use default config (~/.config/timetab/default.ini)
timetab

# Override config file
timetab --config /path/to/config.ini

# Override individual files
timetab --timetable my_timetable.csv --sectors my_sectors.csv

# Verbose logging
timetab -v

# Quiet mode (only errors)
timetab -q
```

## Configuration

The tool reads configuration from `~/.config/timetab/default.ini`:

```ini
# timetab settings:
# Relative pathnames are relative to the directory of this configfile.
# ~ are interpreted as $HOME.

timetable=~/mytime/timetable.csv
sectors=~/mytime/sectors.csv
```

## Development

```bash
# Run directly from source
python -m src.main

# Run with specific files
python -m src.main --timetable TimeTable.csv --sectors Sectors.csv
```
```

**`requirements.txt`:**
