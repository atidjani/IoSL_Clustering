#!/bin/bash

# Run the Django development server
echo $(pwd)
python Visualizer/manage.py runserver 0.0.0.0:8000
