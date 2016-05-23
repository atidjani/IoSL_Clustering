from django.conf.urls import url
from . import views

urlpatterns = [
        url(r'^result', views.ResultView, name='ResultView'),
        url(r'^$', views.UploadDatasetView, name='UploadDatasetView'),
]
