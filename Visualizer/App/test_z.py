# from __future__ import absolute_import
import os,django,sys,re
from django.conf import settings
from pprint import pprint as p
curr_file_path = os.path.abspath(os.path.join(os.path.dirname(__file__)))
path = re.search(r'(.*/Visualizer)',curr_file_path).group(1)

# django.conf.settings.configure(DJANGO_SETTINGS_MODULE="%s.Visualizer.settings" %path, DEBUG=True)
# sys.path.append(path)
# sys.path.append(path+'/Visualizer/App')
# print '%s/Visualizer/App/models.py' %path

# sys.path.insert(0, r'/home/zahin/Dropbox/EIT_ICT/2nd_semester/IOS_Lab/IoSL_Clustering')
# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s' %path)))
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s' %path)))
# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s/Visualizer/App' %path)))

# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s/Visualizer/Visualizer/settings.py' %path)))

p(sys.path)
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "Visualizer.settings")
from django.core.wsgi import get_wsgi_application
application = get_wsgi_application()
import django
django.setup()
from App.models import Dataset

