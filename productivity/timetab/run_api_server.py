#!/usr/bin/env python3
"""
TimeTab API Server Runner (from project root)
"""

import subprocess
import sys
import os
from pathlib import Path

def main():
    # Get the python directory path
    python_dir = Path(__file__).parent / "python"
    
    if not python_dir.exists():
        print("Error: python/ directory not found!")
        print("Please ensure you're running this from the project root directory.")
        sys.exit(1)
    
    # Change to python directory and run the server
    os.chdir(python_dir)
    
    print(f"Starting TimeTab API server from {python_dir}...")
    print("Press Ctrl+C to stop the server")
    
    try:
        # Run the server using the existing run_server.py script
        subprocess.run([sys.executable, "run_server.py"], check=True)
    except KeyboardInterrupt:
        print("\nServer stopped by user")
    except subprocess.CalledProcessError as e:
        print(f"Error running server: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 