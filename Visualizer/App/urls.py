from django.conf.urls import url
from . import views

# Match the url with the right view
urlpatterns = [
        url(r'^resultSTSC', views.ResultViewSTSC, name='ResultViewSTSC'),
        url(r'^resultOPTICSR', views.ResultViewOPTICSR, name='ResultViewOPTICSR'),
        url(r'^resultOPTICSP', views.ResultViewOPTICSP, name='ResultViewOPTICSP'),
        url(r'^resultOPTICSJ', views.ResultViewOPTICSJ, name='ResultViewOPTICSJ'),
        url(r'^error', views.ErrorView, name='ErrorView'),
        url(r'^$', views.UploadDatasetView, name='UploadDatasetView'),
        url(r'^.*$', views.HomepageRedirect, name='HomepageRedirect'),
]
