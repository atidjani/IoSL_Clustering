import time
from __future__ import absolute_import
import os,django,sys
from django.conf import settings
path = os.getcwd()
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "Visualizer.settings")
from django.core.wsgi import get_wsgi_application
application = get_wsgi_application()
import django
django.setup()

from .models import Dataset

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s' %path)))
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s/Visualizer' %path)))

# Cronjob, delete entries in the database older than 6 hours
def deleteOldEntries() :
    #unitl = int(time.time()) - 6*60*60 # 6h * 60m *60s
    #Dataset.objects.filter(creationTime<time.ctime(until).strftime('%Y-%m-%d %H:%M:%S')).delete()
    print("==================Old Entries deleted")
