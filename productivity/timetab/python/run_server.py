#!/usr/bin/env python3
"""
TimeTab ASGI Server Startup Script
"""

import uvicorn
import os
import sys
from pathlib import Path
from dotenv import load_dotenv

# Add the python directory to Python path
python_dir = Path(__file__).parent
sys.path.insert(0, str(python_dir))

# Load environment variables
load_dotenv()

if __name__ == "__main__":
    # Configuration
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    reload = os.getenv("RELOAD", "true").lower() == "true"
    
    print(f"Starting TimeTab API server...")
    print(f"Host: {host}")
    print(f"Port: {port}")
    print(f"Reload: {reload}")
    print(f"API Documentation: http://{host}:{port}/docs")
    print(f"Health Check: http://{host}:{port}/health")
    
    # Start the server
    uvicorn.run(
        "src.api:app",
        host=host,
        port=port,
        reload=reload,
        log_level="info"
    ) 