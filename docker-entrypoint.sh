#!/bin/bash

# Run the Django development server
python Visualizer/manage.py makemigrations
python Visualizer/manage.py migrate

# Start cronjob to delete old entries
python Visualizer/manage.py crontab add

# Start server
python Visualizer/manage.py runserver 0.0.0.0:8000
