import pandas as pd
import mysql.connector
from sqlalchemy import create_engine
import numpy as np
from datetime import datetime
import time

def main():
    start_time = time.time()
    
    # Create database connection
    print("Connecting to database...")
    # if you set the password, use this: 
    connection_str = 'mysql+mysqlconnector://root:password@localhost:3306/CarGenieDB'
    # change the password to your own password
    # connection_str = 'mysql+mysqlconnector://root@localhost:3306/CarGenieDB'
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
        
        # Clean data: Remove rows with null VIN
        print("Cleaning data: Removing rows with null VIN...")
        df = df.dropna(subset=['VIN'])
        
        # Clean data: For each VIN, keep only the row with the latest posting_date
        print("Cleaning data: Keeping only the latest posting_date for each VIN...")
        df = df.sort_values('posting_date', ascending=False)
        df = df.drop_duplicates(subset=['VIN'], keep='first')
        
        # Import data to temp_vehicles table
        print("Importing data to temp_vehicles table...")
        df.to_sql('temp_vehicles', engine, if_exists='replace', index=False, 
                  chunksize=1000, method='multi')
        
        print("Data successfully imported to temp_vehicles table!")
        print(f"Imported {len(df)} records.")
        
        # Close connection
        engine.dispose()
        
        end_time = time.time()
        execution_time = end_time - start_time
        print(f"\run: {execution_time:.2f} s")
        
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        engine.dispose()
        return

if __name__ == "__main__":
    main()