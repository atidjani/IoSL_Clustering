import os

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.core.exceptions import ValidationError

from .forms import *
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

    if request.method == 'GET':
        # GET - First request prepare the form
        # Create Form
        form = ParametersSTSC(initial={'numClusters':10, 'k':7})
        # Set default parameters
        numClusters = 10
        k = 6
    else :
        # POST - New calculation requested
        form = ParametersSTSC(request.POST)
        if form.is_valid() :
            numClusters = form.cleaned_data['numClusters']
            k = form.cleaned_data['k'] - 1
        else :
            return HttpResponseRedirect('/result')

    # Get element
    dsId = request.session.get('ds')
    ds = Dataset.objects.get(id=dsId)
    filePath = ds.writeFile() # Write dataset on disk
    stsc = StscRunner(filePath, numClusters, k) # Execution of STSC
    output = stsc.run()
    os.remove(filePath) # Delete File

    # Put the form in the output to display it
    output['form'] = form

    return render(request, 'ResultTemplate.html', output)
