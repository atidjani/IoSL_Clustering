from django.conf.urls import url
from . import views

urlpatterns = [
        url(r'^resultSTSC', views.ResultViewSTSC, name='ResultViewSTSC'),
        url(r'^resultOPTICS', views.ResultViewOPTICS, name='ResultViewOPTICS'),
        url(r'^error', views.ErrorView, name='ErrorView'),
        url(r'^$', views.UploadDatasetView, name='UploadDatasetView'),
]
