import os

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.core.exceptions import ValidationError

from .forms import UploadDatasetForm
from .models import Dataset
from .runners import StscRunner


# Uploader view
def UploadDatasetView(request):
    if request.method == 'POST':
        form = UploadDatasetForm(request.POST, request.FILES)
        if form.is_valid() :
            ds = Dataset(data=request.FILES['file'].read())
            try :
                ds.full_clean()
            except ValidationError:
                return HttpResponse("Error")

            ds.save()
            request.session['ds'] = ds.id
            return HttpResponseRedirect('/result')
    else :
        # Trick to avoid None session_key at the first request
        request.session['one'] = "One entry to rule them all and in the session bind them"
        form = UploadDatasetForm()
    return render(request, 'UploadDatasetTemplate.html', {'form': form})

# Result View
def ResultView(request) :
    # You fool. Go back to the Uploader View. The ds is not set
    if request.session.get('ds', None) == None :
        return HttpResponseRedirect('/')

    # Get element
    dsId = request.session.get('ds')
    ds = Dataset.objects.get(id=dsId)
    filePath = ds.writeFile() # Write dataset on disk
    stsc = StscRunner(filePath, 10, 6) # Execution of STSC
    output = stsc.run()
    os.remove(filePath) # Delete File
    return render(request, 'ResultTemplate.html', output)
