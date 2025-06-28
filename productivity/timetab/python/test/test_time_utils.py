import unittest
from src.time_utils import Time, parse_time_to_minutes, minutes_to_time, parse_percentage, format_percentage


class TestTime(unittest.TestCase):
    
    def test_time_creation(self):
        """Test Time creation with valid hours and minutes."""
        time_obj = Time(6, 35)
        self.assertEqual(time_obj.hours, 6)
        self.assertEqual(time_obj.minutes, 35)
    
    def test_time_creation_invalid_hours(self):
        """Test Time creation with invalid hours."""
        with self.assertRaises(ValueError):
            Time(24, 0)  # Hours > 23
        with self.assertRaises(ValueError):
            Time(-1, 0)  # Negative hours
    
    def test_time_creation_invalid_minutes(self):
        """Test Time creation with invalid minutes."""
        with self.assertRaises(ValueError):
            Time(12, 60)  # Minutes > 59
        with self.assertRaises(ValueError):
            Time(12, -1)  # Negative minutes
    
    def test_from_string_valid(self):
        """Test Time.from_string with valid time strings."""
        test_cases = [
            ("00:00", 0, 0),
            ("00:30", 0, 30),
            ("01:00", 1, 0),
            ("01:30", 1, 30),
            ("06:35", 6, 35),
            ("12:00", 12, 0),
            ("23:59", 23, 59),
        ]
        
        for time_str, expected_hours, expected_minutes in test_cases:
            with self.subTest(time_str=time_str):
                time_obj = Time.from_string(time_str)
                self.assertEqual(time_obj.hours, expected_hours)
                self.assertEqual(time_obj.minutes, expected_minutes)
    
    def test_from_string_invalid(self):
        """Test Time.from_string with invalid time strings."""
        invalid_times = [
            "25:00",  # Invalid hour
            "12:60",  # Invalid minute
            "12",     # Missing minutes
            ":30",    # Missing hours
            "12:30:45",  # Too many parts
            "abc",    # Non-numeric
            "",       # Empty string
            "12:5",   # Single digit minutes
        ]
        
        for invalid_time in invalid_times:
            with self.subTest(invalid_time=invalid_time):
                with self.assertRaises(ValueError):
                    Time.from_string(invalid_time)
    
    def test_from_minutes(self):
        """Test Time.from_minutes method."""
        test_cases = [
            (0, 0, 0),
            (30, 0, 30),
            (60, 1, 0),
            (90, 1, 30),
            (395, 6, 35),
            (720, 12, 0),
            (1439, 23, 59),
        ]
        
        for total_minutes, expected_hours, expected_minutes in test_cases:
            with self.subTest(total_minutes=total_minutes):
                time_obj = Time.from_minutes(total_minutes)
                self.assertEqual(time_obj.hours, expected_hours)
                self.assertEqual(time_obj.minutes, expected_minutes)
    
    def test_from_minutes_negative(self):
        """Test Time.from_minutes with negative value."""
        with self.assertRaises(ValueError):
            Time.from_minutes(-1)
    
    def test_to_minutes(self):
        """Test to_minutes method."""
        test_cases = [
            (Time(0, 0), 0),
            (Time(0, 30), 30),
            (Time(1, 0), 60),
            (Time(1, 30), 90),
            (Time(6, 35), 395),
            (Time(12, 0), 720),
            (Time(23, 59), 1439),
        ]
        
        for time_obj, expected_minutes in test_cases:
            with self.subTest(time_obj=str(time_obj)):
                self.assertEqual(time_obj.to_minutes(), expected_minutes)
    
    def test_to_string(self):
        """Test to_string method."""
        test_cases = [
            (Time(0, 0), "00:00"),
            (Time(0, 30), "00:30"),
            (Time(1, 0), "01:00"),
            (Time(1, 30), "01:30"),
            (Time(6, 35), "06:35"),
            (Time(12, 0), "12:00"),
            (Time(23, 59), "23:59"),
        ]
        
        for time_obj, expected_string in test_cases:
            with self.subTest(time_obj=str(time_obj)):
                self.assertEqual(time_obj.to_string(), expected_string)
    
    def test_add_minutes(self):
        """Test add_minutes method."""
        time_obj = Time(6, 35)
        
        # Add 25 minutes
        result = time_obj.add_minutes(25)
        self.assertEqual(result, Time(7, 0))
        
        # Add 60 minutes
        result = time_obj.add_minutes(60)
        self.assertEqual(result, Time(7, 35))
        
        # Add 120 minutes
        result = time_obj.add_minutes(120)
        self.assertEqual(result, Time(8, 35))
    
    def test_subtract_minutes(self):
        """Test subtract_minutes method."""
        time_obj = Time(7, 0)
        
        # Subtract 25 minutes
        result = time_obj.subtract_minutes(25)
        self.assertEqual(result, Time(6, 35))
        
        # Subtract 60 minutes
        result = time_obj.subtract_minutes(60)
        self.assertEqual(result, Time(6, 0))
    
    def test_subtract_minutes_negative(self):
        """Test subtract_minutes with too many minutes."""
        time_obj = Time(1, 0)
        with self.assertRaises(ValueError):
            time_obj.subtract_minutes(120)
    
    def test_comparison_operators(self):
        """Test comparison operators."""
        time1 = Time(6, 35)
        time2 = Time(7, 0)
        time3 = Time(7, 0)
        
        self.assertTrue(time1 < time2)
        self.assertTrue(time2 > time1)
        self.assertTrue(time2 == time3)
        self.assertTrue(time2 <= time3)
        self.assertTrue(time2 >= time3)
        self.assertFalse(time1 == time2)
    
    def test_comparison_invalid_type(self):
        """Test comparison with invalid type."""
        time_obj = Time(6, 35)
        with self.assertRaises(TypeError):
            time_obj < "invalid"
    
    def test_str_repr(self):
        """Test string and representation methods."""
        time_obj = Time(6, 35)
        self.assertEqual(str(time_obj), "06:35")
        self.assertEqual(repr(time_obj), "Time(6, 35)")
    
    def test_hash(self):
        """Test hash method."""
        time1 = Time(6, 35)
        time2 = Time(6, 35)
        time3 = Time(7, 0)
        
        self.assertEqual(hash(time1), hash(time2))
        self.assertNotEqual(hash(time1), hash(time3))


class TestTimeUtils(unittest.TestCase):
    
    def test_parse_time_to_minutes(self):
        """Test parse_time_to_minutes function."""
        test_cases = [
            ("00:00", 0),
            ("00:30", 30),
            ("01:00", 60),
            ("06:35", 395),
            ("12:00", 720),
            ("23:59", 1439),
        ]
        
        for time_str, expected_minutes in test_cases:
            with self.subTest(time_str=time_str):
                result = parse_time_to_minutes(time_str)
                self.assertEqual(result, expected_minutes)
    
    def test_minutes_to_time(self):
        """Test minutes_to_time function."""
        test_cases = [
            (0, "00:00"),
            (30, "00:30"),
            (60, "01:00"),
            (395, "06:35"),
            (720, "12:00"),
            (1439, "23:59"),
        ]
        
        for minutes, expected_time in test_cases:
            with self.subTest(minutes=minutes):
                result = minutes_to_time(minutes)
                self.assertEqual(result, expected_time)
    
    def test_round_trip_conversion(self):
        """Test round-trip conversion: time string -> minutes -> time string."""
        test_times = [
            "00:00",
            "00:30",
            "01:00",
            "06:35",
            "12:00",
            "23:59",
        ]
        
        for time_str in test_times:
            with self.subTest(time_str=time_str):
                minutes = parse_time_to_minutes(time_str)
                converted_back = minutes_to_time(minutes)
                self.assertEqual(converted_back, time_str)


class TestPercentageParsing(unittest.TestCase):
    
    def test_parse_percentage_valid(self):
        """Test parse_percentage with valid percentage strings."""
        test_cases = [
            ("0%", 0.0),
            ("33.7%", 33.7),
            ("6.7%", 6.7),
            ("100%", 100.0),
            ("50.0%", 50.0),
            ("25.5%", 25.5),
        ]
        
        for percentage_str, expected_value in test_cases:
            with self.subTest(percentage_str=percentage_str):
                result = parse_percentage(percentage_str)
                self.assertEqual(result, expected_value)
    
    def test_parse_percentage_invalid(self):
        """Test parse_percentage with invalid percentage strings."""
        invalid_percentages = [
            "33.7",  # Missing %
            "abc%",  # Non-numeric
            "%",     # No number
            "",      # Empty string
            "33.7%%",  # Double %
            "-10%",  # Negative
        ]
        
        for invalid_percentage in invalid_percentages:
            with self.subTest(invalid_percentage=invalid_percentage):
                with self.assertRaises(ValueError):
                    parse_percentage(invalid_percentage)
    
    def test_parse_percentage_invalid_type(self):
        """Test parse_percentage with invalid type."""
        with self.assertRaises(ValueError):
            parse_percentage(33.7)  # Not a string
    
    def test_format_percentage(self):
        """Test format_percentage function."""
        test_cases = [
            (0.0, "0%"),
            (33.7, "33.7%"),
            (6.7, "6.7%"),
            (100.0, "100%"),
            (50.0, "50%"),  # Removes trailing .0
            (25.5, "25.5%"),
        ]
        
        for value, expected_string in test_cases:
            with self.subTest(value=value):
                result = format_percentage(value)
                self.assertEqual(result, expected_string)
    
    def test_format_percentage_invalid(self):
        """Test format_percentage with invalid values."""
        invalid_values = [
            -10.0,  # Negative
            "abc",  # Non-numeric
        ]
        
        for invalid_value in invalid_values:
            with self.subTest(invalid_value=invalid_value):
                with self.assertRaises(ValueError):
                    format_percentage(invalid_value)
    
    def test_round_trip_percentage(self):
        """Test round-trip conversion: percentage string -> float -> percentage string."""
        test_percentages = [
            "0%",
            "33.7%",
            "6.7%",
            "100%",
            "50.0%",
            "25.5%",
        ]
        
        for percentage_str in test_percentages:
            with self.subTest(percentage_str=percentage_str):
                value = parse_percentage(percentage_str)
                formatted = format_percentage(value)
                # Note: "50.0%" becomes "50%" after round-trip, which is expected
                if percentage_str.endswith('.0%'):
                    expected = percentage_str.replace('.0%', '%')
                else:
                    expected = percentage_str
                self.assertEqual(formatted, expected)


if __name__ == '__main__':
    unittest.main() 