#!/bin/bash

# Run the Django development server
python2 Visualizer/manage.py makemigrations
python2 Visualizer/manage.py migrate

# Start server
python2 Visualizer/manage.py runserver 0.0.0.0:8000
