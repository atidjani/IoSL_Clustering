from django.shortcuts import render
from django.http import HttpResponse

from .forms import UploadDatasetForm
from .templates import *

def UploadDatasetView(request):
    if request.method == 'POST':
        form = UploadDatasetForm()
    else :
        form = UploadDatasetForm()
    return render(request, 'UploadDatasetTemplate.html', {'form': form})

# Create your views here.
