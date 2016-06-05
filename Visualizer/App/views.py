import os

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.core.exceptions import ValidationError
from django.template import RequestContext

from .forms import *
from .models import Dataset
from .runners import StscRunner
from .noise import Noise


# Uploader view
def UploadDatasetView(request):
    if request.method == 'POST':
        form = UploadDatasetForm(request.POST, request.FILES)
        if form.is_valid() :
            ds = Dataset(data=request.FILES['file'].read())
            try :
                ds.full_clean()
            except ValidationError:
                return HttpResponseRedirect('/error')

            ds.save()
            request.session['ds'] = ds.id
            return HttpResponseRedirect('/result')
    else :
        # Trick to avoid None session_key at the first request
        request.session['noise'] = ''
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

            functions = form.cleaned_data['noiseFunctions']
            generateNoise = form.cleaned_data['generateNoise']

            if (functions != '' and generateNoise) :
                # Generate noise points
                noise = Noise(functions)
                noiseStr = noise.generatePoints()
                ds.noise = noiseStr
                ds.save()
            elif (functions == ''):
                ds.noise = ''
                ds.save()
        else :
            return HttpResponseRedirect('/result')

    filePath = ds.writeFile() # Write dataset on disk
    stsc = StscRunner(filePath, numClusters, k) # Execution of STSC
    output = stsc.run()
    os.remove(filePath) # Delete File

    # Put the form in the output to display it
    output['form'] = form

    return render(request, 'ResultTemplate.html', output)

# Show the error page
def ErrorView(request) :
    return render(request, 'ErrorPage.html', {'error': 'Not supported dataset'})

