from setuptools import setup, find_packages

setup(
    name="timetab",
    version="1.0.0",
    description="Automatic scheduler tool for allocating sectors to time slots",
    author="Your Name",
    author_email="your.email@example.com",
    packages=find_packages(),
    install_requires=[
        # Add any dependencies here if needed
    ],
    entry_points={
        'console_scripts': [
            'timetab=src.main:main',
        ],
    },
    python_requires='>=3.7',
)
