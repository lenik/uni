import pandas as pd
import logging
from typing import List, Dict, Any, Set

class ExcelParser:
    def __init__(self, required_fields: Set[str]):
        self.required_fields = {f.lower() for f in required_fields}

    def parse_excel(self, filename: str) -> Dict[str, List[Dict[str, Any]]]:
        """
        Parse an Excel file (.xlsx) and return a dict of sheet_name -> list of row dicts.
        Flexible header detection: skips leading lines, matches required fields case-insensitively.
        """
        try:
            xls = pd.ExcelFile(filename, engine='openpyxl')
        except Exception as e:
            logging.error(f"Error opening Excel file {filename}: {e}")
            raise
        sheets_data = {}
        for sheet_name in xls.sheet_names:
            try:
                df = pd.read_excel(xls, sheet_name=sheet_name, dtype=str)
            except Exception as e:
                logging.warning(f"Skipping sheet '{sheet_name}' in {filename}: {e}")
                continue
            # Find header row
            header_row_idx = None
            for i, row in df.iterrows():
                lower_row = [str(cell).strip().lower() for cell in row.values]
                if self.required_fields.issubset(set(lower_row)):
                    header_row_idx = i
                    break
            if header_row_idx is None:
                # Try first row as header
                lower_row = [str(cell).strip().lower() for cell in df.columns]
                if self.required_fields.issubset(set(lower_row)):
                    header_row_idx = -1  # Use columns as header
            if header_row_idx is None:
                logging.info(f"Sheet '{sheet_name}' in {filename} does not contain required fields: {self.required_fields}")
                continue
            if header_row_idx == -1:
                data_df = df
            else:
                # Use this row as header
                df2 = pd.read_excel(xls, sheet_name=sheet_name, header=header_row_idx, dtype=str)
                data_df = df2
            # Normalize columns
            data_df.columns = [str(col).strip().title() for col in data_df.columns]
            # Drop empty rows
            data_df = data_df.dropna(how='all')
            # Convert to list of dicts
            rows = data_df.to_dict(orient='records')
            # Filter out rows missing required fields
            filtered_rows = [row for row in rows if all(str(row.get(f.title(), '')).strip() != '' for f in self.required_fields)]
            if filtered_rows:
                sheets_data[sheet_name] = filtered_rows
        return sheets_data 