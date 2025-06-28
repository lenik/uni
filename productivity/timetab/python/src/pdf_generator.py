import logging
import tempfile
import os
from datetime import datetime
from pathlib import Path
from typing import List, Dict, Any, Optional
from jinja2 import Environment, FileSystemLoader, Template
from weasyprint import HTML, CSS
from weasyprint.text.fonts import FontConfiguration
from .user import User
from .time_table import TimeTable
from .time_slot import TimeSlot
from .sector import Sector
from .time_table_orm import TimeTableORM
from .time_slot_orm import TimeSlotORM
from .db import get_session

class PDFGenerator:
    def __init__(self):
        self.template_dir = Path(__file__).parent / "templates"
        self.template_dir.mkdir(exist_ok=True)
        self.env = Environment(loader=FileSystemLoader(str(self.template_dir)))
        self._create_default_template()
    
    def _create_default_template(self):
        """Create default HTML template for timetable PDF"""
        template_content = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>{{ title or "Timetable" }}</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .header h2 {
            margin: 10px 0 0 0;
            font-size: 1.2em;
            font-weight: 300;
            opacity: 0.9;
        }
        .metadata {
            background-color: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
        }
        .metadata-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .metadata-item {
            text-align: center;
        }
        .metadata-value {
            font-size: 1.5em;
            font-weight: bold;
            color: #495057;
        }
        .metadata-label {
            font-size: 0.9em;
            color: #6c757d;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .timetable {
            padding: 20px;
        }
        .day-section {
            margin-bottom: 30px;
        }
        .day-header {
            background-color: #e9ecef;
            padding: 10px 15px;
            border-radius: 5px;
            margin-bottom: 15px;
            font-weight: bold;
            color: #495057;
        }
        .time-slot {
            display: flex;
            align-items: center;
            padding: 12px 15px;
            margin-bottom: 8px;
            border-radius: 6px;
            border-left: 4px solid #007bff;
            background-color: #f8f9fa;
            transition: all 0.2s ease;
        }
        .time-slot:hover {
            background-color: #e9ecef;
        }
        .time-slot.allocated {
            border-left-color: #28a745;
            background-color: #d4edda;
        }
        .time-slot.break {
            border-left-color: #ffc107;
            background-color: #fff3cd;
        }
        .time-info {
            flex: 1;
            min-width: 0;
        }
        .time-range {
            font-weight: bold;
            color: #495057;
            font-size: 1.1em;
        }
        .duration {
            font-size: 0.9em;
            color: #6c757d;
        }
        .slot-details {
            flex: 2;
            margin-left: 20px;
        }
        .slot-type {
            font-weight: bold;
            color: #495057;
            margin-bottom: 4px;
        }
        .slot-description {
            color: #6c757d;
            font-size: 0.95em;
        }
        .sector-info {
            flex: 1;
            text-align: right;
            margin-left: 20px;
        }
        .sector-abbr {
            font-weight: bold;
            color: #28a745;
            font-size: 1.1em;
        }
        .sector-weight {
            font-size: 0.9em;
            color: #6c757d;
        }
        .split-info {
            background-color: #007bff;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            margin-left: 10px;
        }
        .footer {
            background-color: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #6c757d;
            font-size: 0.9em;
            border-top: 1px solid #e9ecef;
        }
        .no-slots {
            text-align: center;
            padding: 40px;
            color: #6c757d;
            font-style: italic;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{{ title or "Timetable" }}</h1>
            {% if subtitle %}
            <h2>{{ subtitle }}</h2>
            {% endif %}
        </div>
        
        <div class="metadata">
            <div class="metadata-grid">
                <div class="metadata-item">
                    <div class="metadata-value">{{ total_slots }}</div>
                    <div class="metadata-label">Total Slots</div>
                </div>
                <div class="metadata-item">
                    <div class="metadata-value">{{ available_slots }}</div>
                    <div class="metadata-label">Available</div>
                </div>
                <div class="metadata-item">
                    <div class="metadata-value">{{ allocated_slots }}</div>
                    <div class="metadata-label">Allocated</div>
                </div>
                <div class="metadata-item">
                    <div class="metadata-value">{{ total_hours }}h {{ total_minutes }}m</div>
                    <div class="metadata-label">Total Time</div>
                </div>
            </div>
        </div>
        
        <div class="timetable">
            {% if slots %}
                {% for slot in slots %}
                <div class="time-slot {% if slot.sector %}allocated{% elif 'Break' in slot.slot_type %}break{% endif %}">
                    <div class="time-info">
                        <div class="time-range">{{ slot.start }} - {{ slot.end }}</div>
                        <div class="duration">{{ slot.duration }} minutes</div>
                    </div>
                    <div class="slot-details">
                        <div class="slot-type">{{ slot.slot_type }}</div>
                        <div class="slot-description">{{ slot.description }}</div>
                    </div>
                    {% if slot.sector %}
                    <div class="sector-info">
                        <div class="sector-abbr">{{ slot.sector.abbr }}</div>
                        <div class="sector-weight">{{ slot.sector.weight }} pts</div>
                        {% if slot.split %}
                        <span class="split-info">Part {{ slot.split }}</span>
                        {% endif %}
                    </div>
                    {% endif %}
                </div>
                {% endfor %}
            {% else %}
                <div class="no-slots">
                    No time slots found in this timetable.
                </div>
            {% endif %}
        </div>
        
        <div class="footer">
            Generated on {{ generation_date }} for {{ user_name }}
        </div>
    </div>
</body>
</html>
        """
        
        template_path = self.template_dir / "timetable.html"
        if not template_path.exists():
            with open(template_path, 'w') as f:
                f.write(template_content)
    
    def generate_timetable_pdf(self, user: User, timetable_id: str, 
                              title: Optional[str] = None, 
                              subtitle: Optional[str] = None,
                              include_all_slots: bool = False) -> str:
        """Generate PDF for a user's timetable"""
        session = get_session()
        try:
            # Load timetable
            timetable_orm = TimeTableORM.get_by_id(session, int(timetable_id))
            if not timetable_orm:
                raise ValueError("Timetable not found")
            
            # Verify ownership
            if timetable_orm.uid != int(user.id):
                raise ValueError("Access denied")
            
            time_table = TimeTable.from_db(timetable_orm)
            
            # Load timeslots for this timetable
            timeslot_orms = TimeSlotORM.get_by_parent(session, int(timetable_id))
            for ts_orm in timeslot_orms:
                timeslot = TimeSlot.from_db(ts_orm)
                time_table.add_slot(timeslot)
            
            # Determine which slots to include
            if include_all_slots:
                slots_to_include = time_table.slots
            else:
                slots_to_include = time_table.get_available_slots()
            
            # Prepare slot data for template
            slots_data = []
            for slot in slots_to_include:
                slot_data = {
                    'start': slot.start.to_string(),
                    'end': slot.end.to_string(),
                    'duration': slot.duration,
                    'slot_type': slot.slot_type,
                    'description': slot.description,
                    'sector': None,
                    'split': slot.split
                }
                
                if slot.sector:
                    slot_data['sector'] = {
                        'abbr': slot.sector.abbr,
                        'weight': slot.sector.weight,
                        'description': slot.sector.description
                    }
                
                slots_data.append(slot_data)
            
            # Calculate total time
            total_minutes = time_table.get_total_available_time()
            total_hours = total_minutes // 60
            remaining_minutes = total_minutes % 60
            
            # Prepare template context
            context = {
                'title': title or f"Timetable for {user.display_name}",
                'subtitle': subtitle,
                'user_name': user.display_name,
                'slots': slots_data,
                'total_slots': len(time_table),
                'available_slots': time_table.count_available_slots(),
                'allocated_slots': time_table.count_allocated_slots(),
                'total_hours': total_hours,
                'total_minutes': remaining_minutes,
                'generation_date': datetime.now().strftime("%B %d, %Y at %I:%M %p")
            }
            
            # Render template
            template = self.env.get_template("timetable.html")
            html_content = template.render(**context)
            
            # Generate PDF
            font_config = FontConfiguration()
            css = CSS(string='''
                @page { 
                    size: A4; 
                    margin: 1cm;
                    @bottom-center {
                        content: "Page " counter(page) " of " counter(pages);
                        font-size: 10pt;
                        color: #666;
                    }
                }
            ''', font_config=font_config)
            
            html = HTML(string=html_content)
            
            # Create temporary PDF file
            with tempfile.NamedTemporaryFile(delete=False, suffix='.pdf') as temp_file:
                html.write_pdf(temp_file.name, stylesheets=[css], font_config=font_config)
                return temp_file.name
                
        finally:
            session.close()
    
    def generate_sectors_pdf(self, user: User, title: Optional[str] = None) -> str:
        """Generate PDF for a user's sectors"""
        from .sector_orm import SectorORM
        
        session = get_session()
        try:
            # Load sectors for user
            sector_orms = SectorORM.get_by_user(session, int(user.id))
            sectors = [Sector.from_db(s_orm) for s_orm in sector_orms]
            
            # Create sectors template
            sectors_template = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>{{ title or "Sectors" }}</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .sectors-table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        .sectors-table th {
            background-color: #f8f9fa;
            padding: 12px;
            text-align: left;
            border-bottom: 2px solid #dee2e6;
            font-weight: bold;
            color: #495057;
        }
        .sectors-table td {
            padding: 12px;
            border-bottom: 1px solid #dee2e6;
        }
        .sectors-table tr:hover {
            background-color: #f8f9fa;
        }
        .sector-abbr {
            font-weight: bold;
            color: #28a745;
        }
        .sector-weight {
            text-align: center;
            font-weight: bold;
        }
        .sector-ratio {
            text-align: center;
            color: #6c757d;
        }
        .footer {
            background-color: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #6c757d;
            font-size: 0.9em;
            border-top: 1px solid #e9ecef;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{{ title or "Sectors" }}</h1>
        </div>
        
        <div style="padding: 20px;">
            <table class="sectors-table">
                <thead>
                    <tr>
                        <th>Seq</th>
                        <th>Abbreviation</th>
                        <th>Description</th>
                        <th>Weight</th>
                        <th>Ratio</th>
                    </tr>
                </thead>
                <tbody>
                    {% for sector in sectors %}
                    <tr>
                        <td>{{ sector.seq }}</td>
                        <td class="sector-abbr">{{ sector.abbr }}</td>
                        <td>{{ sector.description }}</td>
                        <td class="sector-weight">{{ sector.weight }}</td>
                        <td class="sector-ratio">{{ "%.1f"|format(sector.ratio) }}%</td>
                    </tr>
                    {% endfor %}
                </tbody>
            </table>
        </div>
        
        <div class="footer">
            Generated on {{ generation_date }} for {{ user_name }}
        </div>
    </div>
</body>
</html>
            """
            
            # Prepare context
            context = {
                'title': title or f"Sectors for {user.display_name}",
                'user_name': user.display_name,
                'sectors': sectors,
                'generation_date': datetime.now().strftime("%B %d, %Y at %I:%M %p")
            }
            
            # Generate PDF
            font_config = FontConfiguration()
            css = CSS(string='''
                @page { 
                    size: A4; 
                    margin: 1cm;
                }
            ''', font_config=font_config)
            
            html = HTML(string=sectors_template)
            
            # Create temporary PDF file
            with tempfile.NamedTemporaryFile(delete=False, suffix='.pdf') as temp_file:
                html.write_pdf(temp_file.name, stylesheets=[css], font_config=font_config)
                return temp_file.name
                
        finally:
            session.close() 