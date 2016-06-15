from django.conf.urls import url
from . import views

urlpatterns = [
        url(r'^result', views.ResultViewSTSC, name='ResultViewSTSC'),
        url(r'^error', views.ErrorView, name='ErrorView'),
        url(r'^$', views.UploadDatasetView, name='UploadDatasetView'),
]
