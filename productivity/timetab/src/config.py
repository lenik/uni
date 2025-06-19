import os
import configparser
from pathlib import Path
from typing import Optional

class Config:
    def __init__(self, config_file: Optional[str] = None):
        self.config_file = config_file or self._get_default_config_path()
        self.config = configparser.ConfigParser()
        self._load_config()
    
    def _get_default_config_path(self) -> str:
        """Get the default configuration file path"""
        home = os.path.expanduser("~")
        return os.path.join(home, ".config", "timetab", "default.ini")
    
    def _load_config(self):
        """Load configuration from file"""
        if os.path.exists(self.config_file):
            self.config.read(self.config_file)
        else:
            # Create default config if it doesn't exist
            self._create_default_config()
    
    def _create_default_config(self):
        """Create default configuration file"""
        config_dir = os.path.dirname(self.config_file)
        os.makedirs(config_dir, exist_ok=True)
        
        self.config['DEFAULT'] = {
            'timetable': 'TimeTable.csv',
            'sectors': 'Sectors.csv'
        }
        
        with open(self.config_file, 'w') as f:
            f.write("# timetab settings:\n")
            f.write("# Relative pathnames are relative to the directory of this configfile.\n")
            f.write("# ~ are interpreted as $HOME.\n\n")
            self.config.write(f)
    
    def get_timetable_path(self) -> str:
        """Get timetable file path from config"""
        path = self.config.get('DEFAULT', 'timetable', fallback='TimeTable.csv')
        return self._expand_path(path)
    
    def get_sectors_path(self) -> str:
        """Get sectors file path from config"""
        path = self.config.get('DEFAULT', 'sectors', fallback='Sectors.csv')
        return self._expand_path(path)
    
    def _expand_path(self, path: str) -> str:
        """Expand ~ and relative paths"""
        if path.startswith('~'):
            path = os.path.expanduser(path)
        elif not os.path.isabs(path):
            # Make relative to config file directory
            config_dir = os.path.dirname(self.config_file)
            path = os.path.join(config_dir, path)
        return path