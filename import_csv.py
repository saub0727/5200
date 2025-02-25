import pandas as pd
import mysql.connector
from sqlalchemy import create_engine
import numpy as np
from datetime import datetime

def main():
    # Create database connection
    print("Connecting to database...")
    connection_str = 'mysql+mysqlconnector://root:root@localhost:3306/CarGenieDB'
    engine = create_engine(connection_str)

    try:
        # Read CSV file
        print("Reading CSV file...")
        df = pd.read_csv('vehicles.csv', low_memory=False, dtype={'year': 'Int64', 'odometer': 'Int64'})
        
        # Handle null values
        df = df.replace({np.nan: None})
        
        # Handle date format
        def parse_date(date_str):
            try:
                return pd.to_datetime(date_str).date()
            except:
                return None
        
        print("Processing dates...")
        df['posting_date'] = df['posting_date'].apply(parse_date)
        
        # Import data to temporary table
        print("Importing data to temporary table...")
        df.to_sql('temp_vehicles', engine, if_exists='replace', index=False, 
                  chunksize=1000, method='multi')
        
        print("Data successfully imported to temporary table!")
        print(f"Imported {len(df)} records.")
        
        # Close connection
        engine.dispose()
        
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        engine.dispose()
        return

if __name__ == "__main__":
    main()