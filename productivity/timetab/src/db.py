from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
import os
import logging

Base = declarative_base()

# Default to PostgreSQL, but allow override via env or argument
def get_engine(db_url=None, db_user=None, db_password=None, db_host=None, db_port=None, db_name=None):
    if db_url:
        return create_engine(db_url)
    # Compose PostgreSQL URL
    user = db_user or os.environ.get('DB_USER', 'postgres')
    password = db_password or os.environ.get('DB_PASSWORD', '')
    host = db_host or os.environ.get('DB_HOST', 'localhost')
    port = db_port or os.environ.get('DB_PORT', '5432')
    name = db_name or os.environ.get('DB_NAME', 'timetab')
    url = f'postgresql+psycopg2://{user}:{password}@{host}:{port}/{name}'
    return create_engine(url)

SessionLocal = sessionmaker(autocommit=False, autoflush=False)

def get_session():
    """Get a database session"""
    engine = get_engine()
    SessionLocal.configure(bind=engine)
    return SessionLocal()

def create_tables():
    """Create all database tables"""
    try:
        engine = get_engine()
        Base.metadata.create_all(bind=engine)
        logging.info("Database tables created successfully")
    except Exception as e:
        logging.error(f"Failed to create database tables: {e}")
        raise 