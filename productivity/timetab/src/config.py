import os
import configparser
from pathlib import Path
from typing import Optional
import logging
from .user import User


class Config:
    def __init__(self, config_file: Optional[str] = None, user: Optional[User] = None):
        self.user = user
        self.config_file = config_file or self._get_default_config_path()
        logging.info(f"Using config file: {self.config_file}")
        self.config = configparser.ConfigParser()
        self._load_config()
    
    def _get_default_config_path(self) -> str:
        """Get the default configuration file path"""
        user_data_dir = self.get_user_data_dir()
        return os.path.join(user_data_dir, "default.ini")
    
    @classmethod
    def _get_user_data_dir(cls, user: Optional[User]) -> str:
        """Get user-specific data directory"""
        if user is None:
            user_home = os.path.expanduser("~")
        else:
            user_home = f"/home/{user.name}"
        return os.path.join(user_home, ".config", "timetab")
    
    def get_user_data_dir(self) -> str:
        """Get user-specific data directory"""
        return Config._get_user_data_dir(self.user)
    
    def get_default_timetable_path(self) -> str:
        """Get default timetable path for user"""
        if self.user:
            user_data_dir = self._get_user_data_dir(self.user)
            return os.path.join(user_data_dir, "TimeTable.csv")
        else:
            return "TimeTable.csv"
    
    def get_default_sectors_path(self) -> str:
        """Get default sectors path for user"""
        if self.user:
            user_data_dir = self._get_user_data_dir(self.user)
            return os.path.join(user_data_dir, "Sectors.csv")
        else:
            return "Sectors.csv"
    
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
            'timetable': self.get_default_timetable_path(),
            'sectors': self.get_default_sectors_path()
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
            path = self.config.get('DEFAULT', 'timetable', fallback=self.get_default_timetable_path())
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
            path = self.config.get('DEFAULT', 'sectors', fallback=self.get_default_sectors_path())
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