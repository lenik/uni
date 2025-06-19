import os
import configparser
from pathlib import Path
from typing import Optional
import logging


class Config:
    def __init__(self, config_file: Optional[str] = None):
        self.config_file = config_file or self._get_default_config_path()
        logging.info(f"Using config file: {self.config_file}")
        self.config = configparser.ConfigParser()
        self._load_config()
    
    def _get_default_config_path(self) -> str:
        """Get the default configuration file path"""
        home = os.path.expanduser("~")
        return os.path.join(home, ".config", "timetab", "default.ini")
    
    def _load_config(self):
        """Load configuration from file"""
        if os.path.exists(self.config_file):
            try:
                self.config.read(self.config_file)
                logging.debug(f"Successfully loaded config from {self.config_file}")
            except configparser.Error as e:
                logging.warning(f"Standard config parsing failed for {self.config_file}: {e}")
                logging.info("Attempting to parse config file without section headers...")
                
                # Try to parse the file manually without section headers
                if self._parse_simple_config_file():
                    logging.info("Successfully parsed config file without section headers")
                else:
                    logging.error(f"Config file path: {os.path.abspath(self.config_file)}")
                    
                    # Try to show the problematic content
                    try:
                        with open(self.config_file, 'r') as f:
                            content = f.read()
                            logging.error(f"Config file content:\n{content}")
                    except Exception as read_error:
                        logging.error(f"Could not read config file content: {read_error}")
                    
                    # Don't overwrite existing file - just raise the error
                    raise ValueError(f"Config file {self.config_file} has invalid format. Please fix it manually or remove it to create a new default config.")
                
        else:
            # Create default config only if it doesn't exist
            logging.info(f"Config file {self.config_file} not found, creating default")
            self._create_default_config()
    
    def _parse_simple_config_file(self) -> bool:
        """
        Parse simple config file without section headers and populate the config object.
        Returns True if successful, False otherwise.
        """
        try:
            with open(self.config_file, 'r') as f:
                content = f.read()
            
            # Parse the content manually
            config_data = {}
            lines = content.split('\n')
            for line in lines:
                line = line.strip()
                if line.startswith('#') or not line:
                    continue
                
                if '=' in line:
                    key, value = line.split('=', 1)
                    config_data[key.strip()] = value.strip()
            
            # If we found any config data, populate the config object
            if config_data:
                self.config['DEFAULT'] = config_data
                return True
            
            return False
            
        except Exception as e:
            logging.debug(f"Error parsing simple config file: {e}")
            return False
    
    def _create_default_config(self):
        """Create default configuration file"""
        config_dir = os.path.dirname(self.config_file)
        os.makedirs(config_dir, exist_ok=True)
        
        # Create config with DEFAULT section (works with all Python versions)
        self.config['DEFAULT'] = {
            'timetable': 'TimeTable.csv',
            'sectors': 'Sectors.csv'
        }
        
        # Write the config file
        with open(self.config_file, 'w') as f:
            f.write("# timetab settings:\n")
            f.write("# Relative pathnames are relative to the directory of this configfile.\n")
            f.write("# ~ are interpreted as $HOME.\n\n")
            self.config.write(f)
        
        logging.info(f"Created default config file: {self.config_file}")
    
    def get_timetable_path(self) -> str:
        """Get timetable file path from config"""
        try:
            path = self.config.get('DEFAULT', 'timetable', fallback='TimeTable.csv')
            expanded_path = self._expand_path(path)
            logging.debug(f"Timetable path from config: {path} -> {expanded_path}")
            return expanded_path
        except Exception as e:
            logging.error(f"Error getting timetable path from config: {e}")
            logging.error(f"Config file: {self.config_file}")
            raise
    
    def get_sectors_path(self) -> str:
        """Get sectors file path from config"""
        try:
            path = self.config.get('DEFAULT', 'sectors', fallback='Sectors.csv')
            expanded_path = self._expand_path(path)
            logging.debug(f"Sectors path from config: {path} -> {expanded_path}")
            return expanded_path
        except Exception as e:
            logging.error(f"Error getting sectors path from config: {e}")
            logging.error(f"Config file: {self.config_file}")
            raise
    
    def _expand_path(self, path: str) -> str:
        """Expand ~ and relative paths"""
        if path.startswith('~'):
            path = os.path.expanduser(path)
        elif not os.path.isabs(path):
            # Make relative to config file directory
            config_dir = os.path.dirname(self.config_file)
            path = os.path.join(config_dir, path)
        return os.path.abspath(path)