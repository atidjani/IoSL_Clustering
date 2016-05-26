#!/bin/bash

# Run the Django development server
python Visualizer/manage.py makemigrations
python Visualizer/manage.py migrate

# Todo - Make STSC

python Visualizer/manage.py runserver 0.0.0.0:8000
