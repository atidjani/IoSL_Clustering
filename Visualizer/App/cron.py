import time
import os,django,sys,re
from django.conf import settings
curr_file_path = os.path.abspath(os.path.join(os.path.dirname(__file__)))
path = re.search(r'(.*/Visualizer)',curr_file_path).group(1)
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s' %path)))
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "Visualizer.settings")
from django.core.wsgi import get_wsgi_application
application = get_wsgi_application()
django.setup()

from App.models import Dataset

# Cronjob, delete entries in the database older than 6 hours
def deleteOldEntries() :
    #unitl = int(time.time()) - 6*60*60 # 6h * 60m *60s
    #Dataset.objects.filter(creationTime<time.ctime(until).strftime('%Y-%m-%d %H:%M:%S')).delete()
    print("==================Old Entries deleted")
